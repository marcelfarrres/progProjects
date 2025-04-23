package Business.Parser;

import Business.Scanner.Scanner;
import Business.Scanner.Tokens.Token;
import java.util.*;
import java.io.IOException;

public class Parser {

    private Scanner scanner;
    private Token currentToken;
    private HashMap<String, List<String>> grammarMap;
    private HashMap<String, Set<String>> firstSet;
    private HashMap<String, Set<String>> followSet;
    private HashMap<String, HashMap<String, String>> parsingTable;
    private ParseTreeNode root;
    private Stack<Object> symbolStack;
    private Stack<ParseTreeNode> nodeStack;

    public Parser(Scanner scanner) throws IOException {
        this.scanner = scanner;
        this.symbolStack = new Stack<>();
        this.nodeStack = new Stack<>();

        System.out.println("\n--- Initializing Parser and Building Structures ---");
        try {
            GrammarAnalysis grammarAnalysis = new GrammarAnalysis(null);
            grammarAnalysis.analyzeGrammar();
            this.grammarMap = grammarAnalysis.getGrammarMap();
            if (this.grammarMap == null || this.grammarMap.isEmpty()) {
                throw new RuntimeException("Failed to load grammar.");
            }

            FirstFollowBuilder firstFollowBuilder = new FirstFollowBuilder(this.grammarMap);
            this.firstSet = firstFollowBuilder.getFirstSet();
            this.followSet = firstFollowBuilder.getFollowSet();

            ParsingTableBuilder parsingTableBuilder = new ParsingTableBuilder(
                    this.grammarMap, this.firstSet, this.followSet);
            this.parsingTable = parsingTableBuilder.getParsingTable();
            if (this.parsingTable == null || this.parsingTable.isEmpty()) {
                throw new RuntimeException("Failed to build parsing table.");
            }
            System.out.println("Parser structures built successfully.");

        } catch (Exception e) {
            System.err.println("Error during parser initialization: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Parser initialization failed.", e);
        }

        fetchNextToken();
    }

    private void fetchNextToken() throws IOException {
        currentToken = scanner.nextToken();
        System.out.println(">>> Fetched Token: " + currentToken.getName() +
                (currentToken.getValue() != null && !currentToken.getValue().equals(currentToken.getName()) ? " ["+currentToken.getValue()+"]" : "") +
                (currentToken.getName().equals("$") ? "" : " (Line: " + currentToken.getLine() + ")"));
    }

    public ParseTreeNode parse() throws IOException {
        System.out.println("\nStarting Parse Tree Construction (Token by Token)...");

        symbolStack.push("$");
        String startSymbol = "<program>";
        if (!grammarMap.containsKey(startSymbol)) {
            System.err.println("Error: Start symbol '" + startSymbol + "' not found in grammar.");
            return null;
        }
        symbolStack.push(startSymbol);
        root = new ParseTreeNode(startSymbol);
        nodeStack.push(root);

        while (!symbolStack.isEmpty() && !symbolStack.peek().equals("$")) {

            String topSymbol = (String) symbolStack.peek();
            String currentTokenName = currentToken.getName();
            int currentLine = currentToken.getLine();

            System.out.println("\n-----------------------------------------");
            System.out.println(">>> DEBUG: Current Token = " + currentTokenName + " (Line: " + currentLine + ")");
            System.out.println(">>> DEBUG: Symbol Stack Top = " + topSymbol);
            System.out.println(">>> DEBUG: Symbol Stack (Full): " + symbolStack);
            List<String> nodeSymbols = new ArrayList<>();
            for (ParseTreeNode node : nodeStack) {
                nodeSymbols.add(node.getSymbol());
            }
            System.out.println(">>> DEBUG: Node Stack (Symbols): " + nodeSymbols);
            System.out.println("-----------------------------------------");

            if (isTerminal(topSymbol)) {
                System.out.println(">>> Action: Trying to match TERMINAL " + topSymbol);
                if (topSymbol.equals(currentTokenName)) {
                    System.out.println(">>> Success: Matched terminal " + topSymbol);
                    symbolStack.pop();
                    nodeStack.pop();
                    fetchNextToken();
                } else {
                    handleError("Terminal mismatch", topSymbol, currentToken);
                    root = null;
                    return root;
                }
            } else {
                System.out.println(">>> Action: Trying to expand NON-TERMINAL " + topSymbol);
                String production = getProduction(topSymbol, currentTokenName);

                if (production != null) {
                    System.out.println("(Line " + String.format("%-3d", currentLine) +
                            " Token: " + String.format("%-15s", currentTokenName) +
                            ") Applying Rule: " + topSymbol + " ::= " +
                            (production.equals("ε") ? "ε" : production));

                    symbolStack.pop();
                    ParseTreeNode parentNode = nodeStack.pop();

                    String[] productionSymbols = production.isEmpty() || production.equals("ε")
                            ? new String[0]
                            : production.split("\\s+");

                    if (productionSymbols.length == 0) {
                        System.out.println(">>> Action: Applying Epsilon production for " + parentNode.getSymbol());
                        parentNode.addChild(new ParseTreeNode("ε"));
                        continue;
                    }

                    List<ParseTreeNode> childNodes = new ArrayList<>();
                    System.out.print(">>> Action: Creating children for " + parentNode.getSymbol() + ": [");
                    for (String prodSymbol : productionSymbols) {
                        if (!prodSymbol.isEmpty()) {
                            ParseTreeNode childNode = new ParseTreeNode(prodSymbol);
                            childNodes.add(childNode);
                            parentNode.addChild(childNode);
                            System.out.print(childNode.getSymbol() + " ");
                        }
                    }
                    System.out.println("]");

                    System.out.print(">>> Action: Pushing to stacks (Symbol, Node): ");
                    for (int i = productionSymbols.length - 1; i >= 0; i--) {
                        String symbol = productionSymbols[i];
                        if (!symbol.isEmpty()) {
                            symbolStack.push(symbol);
                            if(i < childNodes.size()){
                                nodeStack.push(childNodes.get(i));
                                System.out.print("(" + symbol + ", Node(" + childNodes.get(i).getSymbol() + ")) ");
                            } else {
                                System.err.println("\nCRITICAL: Mismatch between production symbols and created child nodes for rule: " + production);
                                return null;
                            }
                        }
                    }
                    System.out.println();

                } else {
                    handleError("No production found in table", topSymbol, currentToken);
                    root = null;
                    return root;
                }
            }
        }

        if (symbolStack.peek().equals("$") && currentToken.getName().equals("$")) {
            System.out.println("\nParsing Successful! Parse tree construction complete.");
            if (!nodeStack.isEmpty()) {
                System.err.println("Warning: Node stack is not empty after successful parse. Size: " + nodeStack.size());
            }
        } else {
            System.err.println("\nParsing Failed or Incomplete.");
            if (symbolStack.isEmpty() || !symbolStack.peek().equals("$")) {
                System.err.println("  Reason: Symbol stack state incorrect. Top: " + (symbolStack.isEmpty() ? "EMPTY" : symbolStack.peek()));
            } else if (!currentToken.getName().equals("$")) {
                System.err.println("  Reason: Input not fully consumed. Next token: " + currentToken.getName() + " at line " + currentToken.getLine());
            } else {
                System.err.println("  Reason: Unknown parsing failure state.");
            }
            root = null;
        }

        return root;
    }

    private boolean isTerminal(String symbol) {
        if (symbol == null) return false;
        if (symbol.equals("$")) return true;
        if (symbol.equals("ε")) return true;
        return !grammarMap.containsKey(symbol);
    }

    private String getProduction(String nonTerminal, String terminal) {
        if (parsingTable.containsKey(nonTerminal)) {
            Map<String, String> row = parsingTable.get(nonTerminal);
            if (row.containsKey(terminal)) {
                String production = row.get(terminal);
                if (production == null || production.trim().isEmpty()) {
                    List<String> rules = grammarMap.get(nonTerminal);
                    for (String rule : rules) {
                        if (rule.equals("ε") || rule.trim().isEmpty()) return "ε";
                    }
                    return null;
                }
                if (production.equals("ε")) return "ε";
                return production;
            } else if (firstSet != null && firstSet.containsKey(nonTerminal) && firstSet.get(nonTerminal).contains("ε")) {
                if (followSet != null && followSet.containsKey(nonTerminal) && followSet.get(nonTerminal).contains(terminal)) {
                    List<String> rules = grammarMap.get(nonTerminal);
                    for (String rule : rules) {
                        if (rule.equals("ε") || rule.trim().isEmpty()) {
                            return "ε";
                        }
                    }
                }
            }
        }
        return null;
    }

    private void handleError(String message, String expectedOrNonTerminal, Token found) {
        System.err.println("\n--------------------");
        System.err.println("Parsing Error at Line " + found.getLine() + ": " + message);
        if (isTerminal(expectedOrNonTerminal)) {
            System.err.println("  Expected Terminal: " + expectedOrNonTerminal);
        } else {
            System.err.println("  While processing NonTerminal: " + expectedOrNonTerminal);
        }
        System.err.println("  Found Token: '" + found.getName() + "' (Value: " + (found.getValue() != null ? found.getValue() : "N/A") + ")");
        System.err.println("  Current Symbol Stack Top: " + (symbolStack.isEmpty() ? "EMPTY" : symbolStack.peek()));
        if (!isTerminal(expectedOrNonTerminal) && parsingTable.containsKey(expectedOrNonTerminal)) {
            System.err.println("  Possible next terminals for " + expectedOrNonTerminal + ": " + parsingTable.get(expectedOrNonTerminal).keySet());
        }
        System.err.println("  Symbol Stack on Error: " + symbolStack);
        List<String> nodeSymbols = new ArrayList<>();
        for (ParseTreeNode node : nodeStack) {
            nodeSymbols.add(node.getSymbol());
        }
        System.err.println("  Node Stack Symbols on Error: " + nodeSymbols);
        System.err.println("--------------------");
    }

    public ParseTreeNode getParseTreeRoot() {
        return root;
    }
}
