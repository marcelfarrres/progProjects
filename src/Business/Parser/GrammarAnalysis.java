package Business.Parser;

import Business.Scanner.Tokens.Token;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class GrammarAnalysis {

    private HashMap<String, List<String>> grammarMap = new HashMap<>();

    public GrammarAnalysis(List<Token> ScannerListOfTokens) {
    }

    public void analyzeGrammar() {
        try {
            Scanner scanner = new Scanner(new File("FilesToUse/grammarFile.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }
                String[] parts = line.split("::=", 2); // Split only on the first "::="
                if (parts.length == 2) {
                    String left = parts[0].trim();
                    String right = parts[1].trim();

                    // Preserve operators and avoid breaking symbols incorrectly
                    List<String> productionList = new ArrayList<>();
                    String[] productions = right.split("\\|");

                    for (String production : productions) {
                        production = production.trim();

                        // Fix for operators
                        production = production.replaceAll("\"", ""); // Remove quotes around special characters
                        production = production.replaceAll("assign", "="); // Convert to standard assignment

                        productionList.add(production);
                    }

                    grammarMap.put(left, productionList);
                }
            }

            scanner.close();
            printGrammar();
        } catch (FileNotFoundException e) {
            System.err.println("Grammar file not found.");
            e.printStackTrace();
        }
    }


    public HashMap<String, List<String>> getGrammarMap() {
        return grammarMap;
    }


    //print grammar
    public void printGrammar() {
        System.out.println("Grammar:");
        for (String nonTerminal : grammarMap.keySet()) {
            System.out.print(nonTerminal + " ::= ");
            List<String> productions = grammarMap.get(nonTerminal);
            for (int i = 0; i < productions.size(); i++) {
                System.out.print(productions.get(i));
                if (i < productions.size() - 1) {
                    System.out.print(" | ");
                }
            }
            System.out.println();
        }
    }

}
