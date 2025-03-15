package Business.Scanner;

import Business.Scanner.Tokens.Token;
import Business.Scanner.Tokens.Literal;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Scanner {

    // Path to the code to scan
    public static final String inputFile = "Codes/Code1.txt";

    public static List<Token> scan() {
        List<Token> tokens = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                // Split the line by space only
                String[] words = line.split("\\s+");
                
                for (String word : words) {
                    if (!word.trim().isEmpty()) {
                        Token token = Dictionary.getTokenFromCode(word, lineNumber);
                        tokens.add(token);
                    }
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

   
}
