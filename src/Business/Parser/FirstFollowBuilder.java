package Business.Parser;

import java.util.*;

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
        initializeFollowSet();
        buildFollowSet();
        printFollowSets();
    }

    public void buildFirstSet() {
        for (String nonTerminal : grammarMap.keySet()) {
            firstSet.put(nonTerminal, new HashSet<>());
        }

        for (String nonTerminal : grammarMap.keySet()) {
            computeFirstSet(nonTerminal, new HashSet<>());
        }

        for (String nonTerminal : grammarMap.keySet()) {
            fullyExpandFirstSet(nonTerminal);
        }
    }

    private void computeFirstSet(String nonTerminal, Set<String> visited) {
        if (visited.contains(nonTerminal)) {
            return;
        }
        visited.add(nonTerminal);

        Set<String> result = firstSet.get(nonTerminal);

        for (String production : grammarMap.get(nonTerminal)) {
            String[] symbols = production.split(" ");
            boolean containsEpsilon = true;

            for (String symbol : symbols) {
                if (isTerminal(symbol)) {
                    if (result.add(symbol)) {
                        firstSet.put(nonTerminal, result);
                    }
                    containsEpsilon = false;
                    break;
                } else {
                    computeFirstSet(symbol, visited);
                    Set<String> symbolFirstSet = firstSet.get(symbol);

                    boolean epsilonInSymbol = symbolFirstSet.contains("ε");

                    for (String terminal : symbolFirstSet) {
                        if (!terminal.equals("ε")) {
                            if (result.add(terminal)) {
                                firstSet.put(nonTerminal, result);
                            }
                        }
                    }

                    if (!epsilonInSymbol) {
                        containsEpsilon = false;
                        break;
                    }
                }
            }

            if (containsEpsilon) {
                if (result.add("ε")) {
                    firstSet.put(nonTerminal, result);
                }
            }
        }
    }

    private void fullyExpandFirstSet(String nonTerminal) {
        Set<String> expandedSet = new HashSet<>();
        Set<String> originalSet = firstSet.get(nonTerminal);

        for (String symbol : originalSet) {
            if (isTerminal(symbol) || symbol.equals("ε")) {
                expandedSet.add(symbol);
            } else {
                expandedSet.addAll(firstSet.get(symbol));
            }
        }

        firstSet.put(nonTerminal, expandedSet);
    }

    private boolean isTerminal(String symbol) {
        return !grammarMap.containsKey(symbol);
    }

    public void printFirstSets() {
        System.out.println("First Sets:");
        for (String nonTerminal : firstSet.keySet()) {
            System.out.println(nonTerminal + " : " + firstSet.get(nonTerminal));
        }
    }

    private void initializeFollowSet() {
        for (String nonTerminal : grammarMap.keySet()) {
            followSet.put(nonTerminal, new HashSet<>());
        }

        followSet.get("<program>").add("$");
    }


    public void buildFollowSet() {
        boolean updated = true;

        while (updated) {
            updated = false;

            for (String nonTerminal : grammarMap.keySet()) {
                for (String production : grammarMap.get(nonTerminal)) {
                    String[] symbols = production.split(" ");

                    for (int i = 0; i < symbols.length; i++) {
                        String symbol = symbols[i];

                        if (!isTerminal(symbol)) { // Only consider non-terminals
                            if (i < symbols.length - 1) {
                                // Case: Symbol followed by another symbol
                                String nextSymbol = symbols[i + 1];

                                if (isTerminal(nextSymbol)) {
                                    // If next symbol is terminal, add it to Follow(symbol)
                                    if (followSet.get(symbol).add(nextSymbol)) {
                                        updated = true;
                                    }
                                } else {
                                    // If next symbol is non-terminal, add its First set (excluding ε)
                                    Set<String> firstNext = firstSet.get(nextSymbol);
                                    if (firstNext != null) {
                                        for (String terminal : firstNext) {
                                            if (!terminal.equals("ε")) {
                                                if (followSet.get(symbol).add(terminal)) {
                                                    updated = true;
                                                }
                                            }
                                        }
                                    }
                                    // If next symbol has ε, inherit Follow(nonTerminal)
                                    if (firstNext.contains("ε")) {
                                        if (followSet.get(symbol).addAll(followSet.get(nonTerminal))) {
                                            updated = true;
                                        }
                                    }
                                }
                            } else {
                                // Case: Symbol is last in production, inherit Follow(nonTerminal)
                                if (followSet.get(symbol).addAll(followSet.get(nonTerminal))) {
                                    updated = true;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void printFollowSets() {
        System.out.println("Follow Sets:");
        for (String nonTerminal : followSet.keySet()) {
            System.out.println(nonTerminal + " : " + followSet.get(nonTerminal));
        }
    }

    //get follow set
    public HashMap<String, Set<String>> getFirstSet(){
        return firstSet;
    }

    public HashMap<String, Set<String>> getFollowSet(){
        return followSet;
    }

    public HashMap<String, List<String>> getGrammarMap(){
        return grammarMap;
    }

}
