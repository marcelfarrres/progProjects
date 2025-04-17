// FILE: Business/Scanner/Scanner.java
package Business.Scanner;

import Business.Scanner.Tokens.Token;
import java.io.*;
// Removed unused imports: LinkedList, Queue
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scanner {

    // Make inputFile configurable or passed to constructor if needed
    public static final String inputFile = "Codes/Code1.txt"; // Asumiendo que Code1.txt es el archivo a escanear

    private BufferedReader reader;
    private String currentLine;
    private int lineNumber = 0;
    private int currentPos = 0;
    private boolean eofReached = false;

    // --- MODIFIED REGEX ---
    // Added "NOT=" before the identifier pattern "[a-zA-Z_]..."
    // Also ensure keywords from your language that are not identifiers (like "main", "if", etc.)
    // are either handled here explicitly OR correctly identified by the Dictionary later.
    // The Dictionary approach is usually preferred.
    private static final Pattern TOKEN_PATTERNS = Pattern.compile(
            "//.*" +                          // Single line comment
                    "|/\\*.*?\\*/" +                  // Multi-line comment (non-greedy)
                    "|\"(?:\\\\.|[^\\\\\"])*\"" +     // String literal (handles escaped quotes)
                    "|\'(?:\\\\.|[^\\\\\'])?\'" +     // Character literal (handles escaped characters)
                    "|\\d+" +                         // Integer literal
                    // -- Multi-word/char operators prioritized --
                    "|NOT=" +                         // <<<--- ADDED "NOT=" HERE
                    "|&&|\\|\\||==|!=|<=|>=|\\+\\+|--" + // Other multi-char operators
                    // -- Keywords & Identifiers --
                    // The Dictionary will distinguish keywords (like 'if', 'main') from identifiers
                    "|[a-zA-Z_][a-zA-Z0-9_]*" +      // Identifier or keyword pattern
                    // -- Single-char operators/symbols --
                    "|\\{|\\}|\\(|\\)|;|:|,|=|>|<|\\+|-|\\*|/|%|!" +
                    // -- Whitespace --
                    "|\\s+" +                         // Whitespace (to be skipped)
                    // -- Error catcher --
                    "|."                              // Any other single character (potential error)
    );
    // --- END MODIFIED REGEX ---


    public Scanner() throws FileNotFoundException {
        try {
            // Consider passing the file path instead of hardcoding
            File file = new File(inputFile);
            if (!file.exists()) {
                throw new FileNotFoundException("Input file not found: " + inputFile);
            }
            this.reader = new BufferedReader(new FileReader(file));
            readLine(); // Read the first line
        } catch (IOException e) {
            // Handle initialization error more gracefully if needed
            eofReached = true;
            System.err.println("Error initializing scanner: " + e.getMessage());
            throw new FileNotFoundException("Error initializing scanner: " + e.getMessage()); // Re-throw for main to catch
        }
    }

    private void readLine() throws IOException {
        currentLine = reader.readLine();
        lineNumber++;
        currentPos = 0;
        if (currentLine == null) {
            eofReached = true;
        }
    }


    public Token nextToken() throws IOException {
        if (eofReached) {
            // Return a special EOF token that the parser recognizes ($)
            // Ensure line number for EOF is consistent or handled appropriately downstream
            return new Token("$", "$", lineNumber);
        }

        while (!eofReached) {
            if (currentLine == null || currentPos >= currentLine.length()) {
                readLine();
                if (eofReached) {
                    return new Token("$", "$", lineNumber); // EOF token
                }
                // If readLine resulted in a null line again (empty file or consecutive newlines), re-check eofReached
                if (currentLine == null) {
                    eofReached = true; // Mark EOF if readLine returns null immediately
                    return new Token("$", "$", lineNumber);
                }
                continue; // Try reading from the new line
            }

            // Use Matcher on the remainder of the current line
            Matcher matcher = TOKEN_PATTERNS.matcher(currentLine.substring(currentPos));

            if (matcher.lookingAt()) { // Check if pattern matches at the beginning
                String match = matcher.group();
                int matchEnd = matcher.end();

                // Determine the actual end position in the original line string
                int absoluteEndPos = currentPos + matchEnd;

                // Skip whitespace and comments BEFORE updating currentPos permanently
                if (match.matches("\\s+") || match.startsWith("//") || match.startsWith("/*")) {
                    currentPos = absoluteEndPos; // Consume whitespace/comment
                    // Handle potential multi-line comments spanning across lines if necessary (more complex)
                    if (match.startsWith("/*")) {
                        // Basic multi-line comment handling within a line;
                        // Real multi-line needs state across readLine calls.
                        // Assuming comments don't span lines or the regex handles it okay for now.
                        if (!match.endsWith("*/")) {
                            System.err.println("Warning: Potentially unclosed multi-line comment detected at line " + lineNumber);
                            // Could implement full multi-line comment skipping here
                        }
                    }
                    continue; // Skip and find the next token
                }

                // We found a potential token
                // Now update currentPos
                currentPos = absoluteEndPos;

                // Use the Dictionary to classify the token
                Token token = Dictionary.getTokenFromCode(match, lineNumber);

                if (token != null && !token.getName().equals("ERROR_UNKNOWN")) {
                    return token; // Return the valid token found
                } else {
                    // Handle unknown tokens (matched by '.' or if Dictionary returns ERROR_UNKNOWN)
                    System.err.println("Error Léxico: Token desconocido '" + match + "' en línea " + lineNumber);
                    // Return an error token or decide on error recovery (e.g., skip and continue)
                    // Returning error token for now:
                    return new Token(match, "ERROR_UNKNOWN", lineNumber);
                }

            } else {
                // This should only happen if the "." pattern fails, which is unlikely.
                // Could indicate an issue with the regex or input stream.
                System.err.println("Scanning Error: No pattern matched at line " + lineNumber + ", position " + currentPos + ". Remainder: " + currentLine.substring(currentPos));
                eofReached = true; // Stop scanning on unrecoverable error
                return new Token("SCAN_ERROR", "$", lineNumber); // Signal end due to error, using a distinct token name might help debugging
            }
        }
        // Should only be reached if loop terminates unexpectedly, return EOF.
        return new Token("$", "$", lineNumber);
    }


    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    // Static printTokens method remains unchanged
    public static void printTokens(java.util.List<Token> tokens) {
        for (Token token : tokens) {
            String valuePart = (token.getValue() != null && !token.getValue().equals(token.getName())) ? " (" + token.getValue() + ")" : "";
            // Avoid printing line number for EOF token
            if (token.getName().equals("$")) {
                System.out.println("Token: $ (EOF)");
            } else {
                System.out.println("Token: " + token.getName() + valuePart + " (Line: " + token.getLine() + ")");
            }
        }
    }
}