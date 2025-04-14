package Business.Parser;

import java.util.*;

public class ParsingTableBuilder {

    private HashMap<String, List<String>> grammarMap;
    private HashMap<String, Set<String>> firstSet;
    private HashMap<String, Set<String>> followSet;
    private HashMap<String, HashMap<String, String>> parsingTable;

    public ParsingTableBuilder(HashMap<String, List<String>> grammarMap, HashMap<String, Set<String>> firstSet, HashMap<String, Set<String>> followSet) {
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
                String[] symbols = production.split(" ");
                Set<String> first = computeFirstOfProduction(symbols);

                for (String terminal : first) {
                    if (!terminal.equals("ε")) {
                        parsingTable.get(nonTerminal).put(terminal, production);
                    }
                }

                if (first.contains("ε")) {
                    Set<String> follow = followSet.get(nonTerminal);
                    for (String terminal : follow) {
                        parsingTable.get(nonTerminal).put(terminal, production);
                    }
                }
            }
        }
    }

    private Set<String> computeFirstOfProduction(String[] symbols) {
        Set<String> result = new HashSet<>();
        boolean containsEpsilon = true;

        for (String symbol : symbols) {
            if (isTerminal(symbol)) {
                result.add(symbol);
                containsEpsilon = false;
                break;
            } else {
                Set<String> first = firstSet.get(symbol);
                result.addAll(first);
                if (!first.contains("ε")) {
                    containsEpsilon = false;
                    break;
                }
            }
        }

        if (containsEpsilon) {
            result.add("ε");
        }

        return result;
    }

    private boolean isTerminal(String symbol) {
        return !grammarMap.containsKey(symbol);
    }

    private void printParsingTable() {
        System.out.println("Parsing Table:");
        // Print header
        System.out.print("\t");
        Set<String> terminals = new HashSet<>();
        for (HashMap<String, String> row : parsingTable.values()) {
            terminals.addAll(row.keySet());
        }
        for (String terminal : terminals) {
            System.out.print(terminal + "\t");
        }
        System.out.println();

        // Print rows
        for (String nonTerminal : parsingTable.keySet()) {
            System.out.print(nonTerminal + "\t");
            HashMap<String, String> row = parsingTable.get(nonTerminal);
            for (String terminal : terminals) {
                String production = row.get(terminal);
                if (production != null) {
                    System.out.print(production + "\t");
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