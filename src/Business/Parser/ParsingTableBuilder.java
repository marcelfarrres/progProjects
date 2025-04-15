package Business.Parser;

import java.util.*;

public class ParsingTableBuilder {

    private HashMap<String, List<String>> grammarMap;
    private HashMap<String, Set<String>> firstSet;
    private HashMap<String, Set<String>> followSet;
    private HashMap<String, HashMap<String, String>> parsingTable;

    public ParsingTableBuilder(HashMap<String, List<String>> grammarMap,
                               HashMap<String, Set<String>> firstSet,
                               HashMap<String, Set<String>> followSet) {

        this.grammarMap = grammarMap;
        this.firstSet = firstSet;
        this.followSet = followSet;
        this.parsingTable = new HashMap<>();
        buildParsingTable();
        printParsingTable();
    }

    private void buildParsingTable() {
        for (String nonTerminal : grammarMap.keySet()) {
            parsingTable.put(nonTerminal, new HashMap<>());

            for (String production : grammarMap.get(nonTerminal)) {
                // Compute FIRST of the entire production
                String[] symbols = production.split("\\s+");
                Set<String> first = computeFirstOfProduction(symbols);

                // For each terminal in FIRST, map it to this production (unless it's ε)
                for (String terminal : first) {
                    if (!terminal.equals("ε")) {
                        parsingTable.get(nonTerminal).put(terminal, production);
                    }
                }

                // If FIRST contains ε, then also use FOLLOW(nonTerminal)
                if (first.contains("ε")) {
                    Set<String> follow = followSet.get(nonTerminal);
                    for (String terminal : follow) {
                        parsingTable.get(nonTerminal).put(terminal, production);
                    }
                }
            }
        }
    }

    // Returns the FIRST set of a sequence of symbols (like <T> <E'>), stopping if a terminal is found
    private Set<String> computeFirstOfProduction(String[] symbols) {
        Set<String> result = new HashSet<>();
        boolean allCouldProduceEpsilon = true;

        for (String symbol : symbols) {
            // If it's terminal, add it and stop
            if (isTerminal(symbol)) {
                result.add(symbol);
                allCouldProduceEpsilon = false;
                break;
            } else {
                // Otherwise, add that non-terminal’s FIRST (except ε)
                Set<String> symbolFirst = firstSet.get(symbol);
                result.addAll(symbolFirst);
                // If this symbol’s FIRST does not have ε, then we stop adding further
                if (!symbolFirst.contains("ε")) {
                    allCouldProduceEpsilon = false;
                    break;
                }
            }
        }
        // If every symbol could produce ε, then ε is in FIRST
        if (allCouldProduceEpsilon) {
            result.add("ε");
        }
        return result;
    }

    private boolean isTerminal(String symbol) {
        // Terminal means it's not a key in the grammar
        return !grammarMap.containsKey(symbol);
    }

    private void printParsingTable() {
        System.out.println("Parsing Table:");

        // Gather all terminals (including '$') that appear in any row
        Set<String> terminals = new HashSet<>();
        for (HashMap<String, String> row : parsingTable.values()) {
            terminals.addAll(row.keySet());
        }

        // Print header (list of terminals)
        System.out.print("\t");
        for (String terminal : terminals) {
            System.out.print(terminal + "\t");
        }
        System.out.println();

        // Print each nonterminal row
        for (String nonTerminal : parsingTable.keySet()) {
            System.out.print(nonTerminal + "\t");
            HashMap<String, String> row = parsingTable.get(nonTerminal);
            for (String terminal : terminals) {
                String prod = row.get(terminal);
                if (prod != null) {
                    System.out.print(prod + "\t");
                } else {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }
    }

    public HashMap<String, HashMap<String, String>> getParsingTable() {
        return parsingTable;
    }
}
