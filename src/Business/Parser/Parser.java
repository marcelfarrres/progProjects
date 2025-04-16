package Business.Parser;

import Business.Scanner.Tokens.Token;
import java.util.*;

public class Parser {

    private List<Token> tokens;
    private int currentTokenIndex = 0;
    private HashMap<String, List<String>> grammarMap;
    private HashMap<String, HashMap<String, String>> parsingTable;
    private ParseTreeNode root; // Root of the parse tree

    // Assume GrammarAnalysis, FirstFollowBuilder, ParsingTableBuilder classes exist and work
    // Assume Token class exists
    // Assume ParseTreeNode class exists

    public Parser(List<Token> scannerListOfTokens) {
        this.tokens = new ArrayList<>(scannerListOfTokens);
        // Add end-of-input marker ONLY if your grammar explicitly uses $
        // If your grammar uses ε for program end, don't add $
        // Let's assume $ is used based on previous context
        this.tokens.add(new Token("$", "$", -1)); // End-of-input marker

        // These lines assume the necessary classes are available and functional
        // In a real scenario, handle potential exceptions from file loading etc.
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis(this.tokens); // Pass tokens if needed by GrammarAnalysis
        grammarAnalysis.analyzeGrammar(); // Loads grammar from file
        this.grammarMap = grammarAnalysis.getGrammarMap();

        FirstFollowBuilder firstFollowBuilder = new FirstFollowBuilder(this.grammarMap);
        // You might need to pass first/follow sets to the parsing table builder
        ParsingTableBuilder parsingTableBuilder = new ParsingTableBuilder(
                firstFollowBuilder.getGrammarMap(),
                firstFollowBuilder.getFirstSet(),
                firstFollowBuilder.getFollowSet()
        );
        this.parsingTable = parsingTableBuilder.getParsingTable(); // Gets the generated table
    }

    public ParseTreeNode parse() {
        System.out.println("\nStarting Parse Tree Construction...");
        Stack<String> symbolStack = new Stack<>();         // Stack for grammar symbols (terminals/non-terminals)
        Stack<ParseTreeNode> nodeStack = new Stack<>(); // Stack for corresponding ParseTreeNodes (only non-terminals)

        // Initialize stacks
        symbolStack.push("$"); // Assuming grammar uses $ as end marker
        String startSymbol = "<program>"; // Ensure this matches your grammar's start symbol
        symbolStack.push(startSymbol);

        // Create root node and push to node stack
        root = new ParseTreeNode(startSymbol);
        nodeStack.push(root); // Push the root non-terminal node

        while (!symbolStack.isEmpty() && !symbolStack.peek().equals("$")) {
            String topSymbol = symbolStack.peek();
            Token currentToken = tokens.get(currentTokenIndex);
            String currentTokenName = currentToken.getName();
            int currentLine = currentToken.getLine(); // Get current line number

            // System.out.println("\nTop of Stack: " + topSymbol);
            // System.out.println("Current Token: " + currentTokenName + " (Line: " + currentLine + ")");

            if (isTerminal(topSymbol)) {
                if (topSymbol.equals(currentTokenName)) {
                    // System.out.println("Match Terminal: " + topSymbol + " at line " + currentLine);
                    symbolStack.pop(); // Pop matched terminal symbol

                    // Create the terminal node and add it as a child to the CURRENT parent node
                    if (!nodeStack.isEmpty()) {
                        ParseTreeNode parentNode = nodeStack.peek();
                        ParseTreeNode terminalNode = new ParseTreeNode(currentToken);
                        parentNode.addChild(terminalNode);
                    } else {
                        // This case should ideally not happen if grammar/logic is correct
                        System.err.println("Error: Node stack empty when matching terminal " + topSymbol);
                    }
                    currentTokenIndex++; // Advance input token
                } else {
                    // Pass currentLine to handleError
                    handleError("Terminal mismatch", topSymbol, currentToken);
                    root = null;
                    return root;
                }
            } else { // Top is Non-Terminal
                // Look up production in the parsing table
                String production = getProduction(topSymbol, currentTokenName);

                if (production != null) {
                    // *** DEBUG PRINT MODIFIED HERE ***
                    System.out.println("(Line " + String.format("%-3d", currentLine) + " Token: " + String.format("%-15s", currentTokenName) + ") Applying Rule: " + topSymbol + " ::= " + (production.isEmpty() || production.equals("ε") ? "ε" : production));
                    // ********************************

                    symbolStack.pop(); // Pop the non-terminal symbol
                    ParseTreeNode parentNode = nodeStack.pop(); // Pop its corresponding node

                    String[] productionSymbols = production.split("\\s+");

                    // Handle epsilon production specifically
                    if (production.equals("ε")) { // Check against consistent epsilon representation
                        parentNode.addChild(new ParseTreeNode("ε")); // Add epsilon representation
                        continue; // Epsilon production complete, continue loop
                    }

                    // --- Apply Production ---
                    // 1. Create child nodes and add them to the parent IN ORDER
                    List<ParseTreeNode> childNodesForStack = new ArrayList<>();
                    for (String prodSymbol : productionSymbols) {
                        if (!prodSymbol.isEmpty()) {
                            ParseTreeNode childNode = new ParseTreeNode(prodSymbol);
                            parentNode.addChild(childNode);
                            if (!isTerminal(prodSymbol)) {
                                childNodesForStack.add(childNode);
                            }
                        }
                    }

                    // 2. Push production symbols onto the symbolStack in REVERSE order
                    for (int i = productionSymbols.length - 1; i >= 0; i--) {
                        String symbol = productionSymbols[i];
                        // Make sure not to push empty strings if split resulted in them
                        if (!symbol.isEmpty() && !symbol.equals("ε")) {
                            symbolStack.push(symbol);
                        }
                    }

                    // 3. Push the non-terminal child nodes onto the nodeStack in REVERSE order
                    for (int i = childNodesForStack.size() - 1; i >= 0; i--) {
                        nodeStack.push(childNodesForStack.get(i));
                    }

                } else {
                    // Pass currentLine to handleError
                    handleError("No production found in table", topSymbol, currentToken);
                    root = null;
                    return root;
                }
            }
        }

        // Final check after loop
        Token finalToken = tokens.get(currentTokenIndex); // Token at the end ($)
        if (!symbolStack.isEmpty() && symbolStack.peek().equals("$") && finalToken.getName().equals("$")) {
            System.out.println("\nParsing Successful! Parse tree constructed.");
        } else {
            System.err.println("\nParsing Failed or Incomplete.");
            if (symbolStack.isEmpty()) {
                System.err.println("  Reason: Symbol stack became empty unexpectedly.");
            } else if (!symbolStack.peek().equals("$")) {
                System.err.println("  Reason: Stack not empty. Top: " + symbolStack.peek());
            } else if (!finalToken.getName().equals("$")) {
                System.err.println("  Reason: Input not fully consumed. Next token: " + finalToken.getName() + " at line " + finalToken.getLine());
            } else {
                System.err.println("  Reason: Unknown parsing failure state.");
            }
            root = null; // Indicate failure
        }

        return root;
    }


    private boolean isTerminal(String symbol) {
        // Terminals are symbols not present as keys in the grammar map
        // Also handle $ conceptually
        if (symbol.equals("$")) return true; // End marker is terminal
        // Epsilon (ε) is handled during production application, not treated as a stack symbol here
        return !grammarMap.containsKey(symbol);
    }

    private String getProduction(String nonTerminal, String terminal) {
        // Check if nonTerminal exists in the table
        if (parsingTable.containsKey(nonTerminal)) {
            Map<String, String> row = parsingTable.get(nonTerminal);
            // Check if the terminal exists in the row for that nonTerminal
            if (row.containsKey(terminal)) {
                String production = row.get(terminal);
                // Use "ε" consistently for epsilon checks
                if (production.equals("ε")) {
                    return "ε";
                }
                // Treat empty string in table as epsilon
                if (production.trim().isEmpty()) {
                    return "ε";
                }
                return production; // Return the production string
            }
        }
        // Check if nonTerminal can produce epsilon and if terminal is in FOLLOW set
        // This handles the case where the explicit table entry might be missing but epsilon is implied
        // NOTE: This part requires access to computed First/Follow sets for full accuracy
        if (firstSetContainsEpsilon(nonTerminal) && followSetContains(nonTerminal, terminal)) {
            // System.out.println("DEBUG: Implicit epsilon for " + nonTerminal + " on token " + terminal);
            return "ε"; // Implicit epsilon production
        }

        // If no explicit production and epsilon isn't implied, return null
        return null;
    }

    // Helper to check if First(A) contains epsilon
    // NOTE: Requires access to actual First sets for full accuracy
    private boolean firstSetContainsEpsilon(String nonTerminal) {
        if (grammarMap.containsKey(nonTerminal)) {
            for (String production : grammarMap.get(nonTerminal)) {
                // Check if the production itself is epsilon or an empty string
                if (production.equals("ε") || production.trim().isEmpty()) {
                    return true;
                }
                // TODO: A more complete check would involve seeing if FIRST(production) contains epsilon
            }
        }
        // Placeholder/Simplification: Look for explicit epsilon rules
        Set<String> firstSetForNonTerminal = getFirstSetForRule(nonTerminal); // Needs implementation
        return firstSetForNonTerminal.contains("ε"); // Check if pre-computed First set has epsilon
        // return false; // Default if check is not implemented
    }

    // Helper to check if Follow(A) contains terminal b
    // NOTE: Requires access to actual Follow sets for full accuracy
    private boolean followSetContains(String nonTerminal, String terminal) {
        // Needs proper implementation by passing Follow sets to Parser or re-computing
        Set<String> followSetForNonTerminal = getFollowSetForRule(nonTerminal); // Needs implementation
        return followSetForNonTerminal.contains(terminal); // Check if pre-computed Follow set has terminal
        // return false; // Default if check is not implemented
    }

    // --- Placeholder methods for accessing First/Follow sets ---
    // --- You need to replace these with actual access to your computed sets ---
    private Set<String> getFirstSetForRule(String nonTerminal) {
        // Example: Retrieve from a map passed during construction or recompute
        // For now, return empty set to avoid null pointers
        return Collections.emptySet(); // Replace with actual logic
    }
    private Set<String> getFollowSetForRule(String nonTerminal) {
        // Example: Retrieve from a map passed during construction or recompute
        // For now, return empty set to avoid null pointers
        return Collections.emptySet(); // Replace with actual logic
    }
    // --- End Placeholder methods ---


    // Modified to accept current line number
    private void handleError(String message, String expectedOrNonTerminal, Token found) {
        System.err.println("Parsing Error at Line " + found.getLine() + ": " + message); // Use found.getLine()
        if (isTerminal(expectedOrNonTerminal)) {
            System.err.println("  While expecting Terminal: " + expectedOrNonTerminal);
        } else {
            System.err.println("  While processing NonTerminal: " + expectedOrNonTerminal);
        }
        System.err.println("  Found Token: '" + found.getName() + "' (Value: " + found.getValue() + ")");
        System.err.println("  No valid production rule found in parsing table for this combination.");
    }

    public ParseTreeNode getParseTreeRoot() {
        return root;
    }
}