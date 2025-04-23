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
            Scanner scanner = new Scanner(new File("FilesToUse/grammarFile2.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty() || line.startsWith("//")) {
                    continue;
                }
                String[] parts = line.split("::=", 2);
                if (parts.length == 2) {
                    String left = parts[0].trim();
                    String right = parts[1].trim();

                    List<String> productionList = new ArrayList<>();
                    String[] productions = right.split("(?<!\\|)\\|(?!\\|)");

                    for (String production : productions) {
                        production = production.trim();
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
