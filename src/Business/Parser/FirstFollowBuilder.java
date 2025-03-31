package Business.Parser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FirstFollowBuilder {

    private HashMap<String, List<String>> grammarMap;
    private HashMap<String, Set<String>> firstSet;
    private HashMap<String, Set<String>> followSet;

    public FirstFollowBuilder(HashMap<String, List<String>> grammarMap) {
        this.grammarMap = grammarMap;
        this.firstSet = new HashMap<>();
        this.followSet = new HashMap<>();
        buildFirstSet();
        printFirstSets();
    }

    public void buildFirstSet() {
        for (String nonTerminal : grammarMap.keySet()) {
            firstSet.put(nonTerminal, new HashSet<>());
        }

        boolean updated = true;

        while (updated) {
            updated = false;

            for (String nonTerminal : grammarMap.keySet()) {
                Set<String> currentFirstSet = firstSet.get(nonTerminal);

                for (String production : grammarMap.get(nonTerminal)) {
                    String[] symbols = production.split(" ");

                    boolean containsEpsilon = true; // Assume ε until proven otherwise

                    for (String symbol : symbols) {
                        if (isTerminal(symbol)) {
                            if (currentFirstSet.add(symbol)) {
                                updated = true;
                            }
                            containsEpsilon = false; // Terminal means no ε
                            break;
                        } else {
                            Set<String> symbolFirstSet = firstSet.get(symbol);
                            if (symbolFirstSet == null) {
                                symbolFirstSet = new HashSet<>();
                                firstSet.put(symbol, symbolFirstSet);
                            }

                            boolean epsilonInSymbol = symbolFirstSet.contains("ε");

                            for (String terminal : symbolFirstSet) {
                                if (!terminal.equals("ε") && currentFirstSet.add(terminal)) {
                                    updated = true;
                                }
                            }

                            if (!epsilonInSymbol) {
                                containsEpsilon = false;
                                break;
                            }
                        }
                    }

                    if (containsEpsilon) {
                        if (currentFirstSet.add("ε")) {
                            updated = true;
                        }
                    }
                }
            }
        }
    }


    private boolean isTerminal(String symbol) {
        return !(symbol.startsWith("<") && symbol.endsWith(">")) || symbol.matches("[a-zA-Z0-9_+\\-*/=!><]+");
    }


    // Method to print the First sets
    public void printFirstSets() {
        System.out.println("First Sets:");
        for (String nonTerminal : firstSet.keySet()) {
            System.out.println(nonTerminal + " : " + firstSet.get(nonTerminal));
        }
    }
}
