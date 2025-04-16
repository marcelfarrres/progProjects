import Business.Parser.*;
import Business.Scanner.*;
import Business.Scanner.Tokens.Token;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class Main {

    private static final Scanner scanner = new Scanner();
    private static Parser parser;

    public static void main(String[] args) throws IOException {

        // --- Scanner Phase ---
        System.out.println("--- Scanning Code ---");
        List<Token> scannerListOfTokens = scanner.scan();
        // Optional: Print tokens
        // System.out.println("\n--- Tokens ---");
        // Scanner.printTokens(scannerListOfTokens);
        System.out.println("Scanning complete.");

        // --- Parser Phase ---
        System.out.println("\n--- Building Parser Structures (Grammar, First/Follow, Table) ---");
        parser = new Parser(scannerListOfTokens); // Builds internal structures
        System.out.println("Parser structures built.");


        // --- Parse Tree Construction ---
        ParseTreeNode parseTreeRoot = parser.parse(); // This now builds the tree

        // --- Print Parse Tree (Optional) ---
        if (parseTreeRoot != null) {
            System.out.println("\n--- Parse Tree ---");
            parseTreeRoot.printTree("", true);
        } else {
            System.out.println("\nParse tree could not be constructed due to parsing errors.");
        }
    }
}