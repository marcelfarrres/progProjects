// FILE: Main.java (adjust package if needed)
import Business.Parser.*;
import Business.Scanner.*;
import Business.Scanner.Tokens.Token; // Keep Token import

import java.io.FileNotFoundException;
import java.io.IOException;
// Removed unused imports: HashMap, List

public class Main {

    // Scanner and Parser are now instance variables, initialized in main
    private static Scanner scanner = null;
    private static Parser parser = null;

    public static void main(String[] args) {

        try {
            // --- Scanner Initialization ---
            System.out.println("--- Initializing Scanner ---");
            scanner = new Scanner(); // Handles file opening
            System.out.println("Scanner initialized for file: " + Scanner.inputFile);

            // --- Parser Initialization (includes Grammar, First/Follow, Table) ---
            System.out.println("\n--- Initializing Parser and Building Structures ---");
            // The Parser constructor now handles building grammar, first/follow, table
            parser = new Parser(scanner); // Pass the scanner instance
            System.out.println("Parser initialized and structures built.");

            // --- Parse Tree Construction (Token by Token) ---
            // The parser's parse method now drives the token-by-token process
            ParseTreeNode parseTreeRoot = parser.parse();

            // --- Print Parse Tree (Optional) ---
            if (parseTreeRoot != null) {
                System.out.println("\n--- Final Parse Tree ---");
                parseTreeRoot.printTree("", true);
            } else {
                System.out.println("\nParse tree construction failed due to errors.");
            }

        } catch (FileNotFoundException e) {
            System.err.println("Error: Input file not found. " + e.getMessage());
            // Exit or handle as appropriate
            System.exit(1);
        } catch (IOException e) {
            System.err.println("An IO error occurred: " + e.getMessage());
            e.printStackTrace();
            // Exit or handle as appropriate
            System.exit(1);
        } catch (RuntimeException e) {
            // Catch runtime exceptions potentially thrown during parser init (e.g., grammar load failure)
            System.err.println("A runtime error occurred during setup: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        } finally {
            // --- Cleanup ---
            if (scanner != null) {
                try {
                    scanner.close();
                    System.out.println("\nScanner closed.");
                } catch (IOException e) {
                    System.err.println("Error closing scanner: " + e.getMessage());
                }
            }
        }
    }
}