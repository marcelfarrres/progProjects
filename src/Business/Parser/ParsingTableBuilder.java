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
                // check FIRST of the whole prod
                String[] symbols = production.split("\\s+");
                Set<String> first = computeFirstOfProduction(symbols);

                // add prod for each symbol in FIRST (but skip ε)
                for (String terminal : first) {
                    if (!terminal.equals("ε")) {
                        parsingTable.get(nonTerminal).put(terminal, production);
                    }
                }

                // if ε is in FIRST, we gotta add stuff from FOLLOW too
                if (first.contains("ε")) {
                    Set<String> follow = followSet.get(nonTerminal);
                    for (String terminal : follow) {
                        parsingTable.get(nonTerminal).put(terminal, production);
                    }
                }
            }
        }
    }

    // gets FIRST for a whole chain like <T> <E'> etc. stops if finds terminal
    private Set<String> computeFirstOfProduction(String[] symbols) {
        Set<String> result = new HashSet<>();
        boolean allCouldProduceEpsilon = true;

        for (String symbol : symbols) {
            // if it's a terminal just add it & stop
            if (isTerminal(symbol)) {
                result.add(symbol);
                allCouldProduceEpsilon = false;
                break;
            } else {
                // if not, add its FIRST (skip ε)
                Set<String> symbolFirst = firstSet.get(symbol);
                result.addAll(symbolFirst);
                // if this one can’t produce ε we done
                if (!symbolFirst.contains("ε")) {
                    allCouldProduceEpsilon = false;
                    break;
                }
            }
        }
        // all could be ε? fine, add ε to result too
        if (allCouldProduceEpsilon) {
            result.add("ε");
        }
        return result;
    }

    private boolean isTerminal(String symbol) {
        // not in grammar map? then yep, it's terminal
        return !grammarMap.containsKey(symbol);
    }

    private void printParsingTable() {
        System.out.println("Parsing Table:");

        // grab all terminals (even '$' ones)
        Set<String> terminals = new HashSet<>();
        for (HashMap<String, String> row : parsingTable.values()) {
            terminals.addAll(row.keySet());
        }

        // print header row (all terminals)
        System.out.print("\t");
        for (String terminal : terminals) {
            System.out.print(terminal + "\t");
        }
        System.out.println();

        // one row per NONterminal
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
