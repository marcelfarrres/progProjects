package Business.Scanner;

import Business.Scanner.Tokens.Token;
import Business.Scanner.Tokens.Literal;
import Business.Scanner.Dictionary;


import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Scanner {

    public static final String inputFile = "Codes/Code1.txt";

    public static List<Token> scan() {
        List<Token> tokens = new ArrayList<>();
        try {
            String cleanedCode = removeComments(readFile(inputFile));
            String[] lines = cleanedCode.split("\n");
            int lineNumber = 1;
            for (String cleanLine : lines) {
                String[] words = cleanLine.split("\\s+");
                
                for (String word : words) {
                    if (!word.trim().isEmpty()) {
                        Token token = Dictionary.getTokenFromCode(word, lineNumber);
                        tokens.add(token);
                        System.out.println("Token: " + token + " (Line: " + lineNumber + ")");
                    }
                }
                lineNumber++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tokens;
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder code = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                code.append(line).append("\n");
            }
        }
        return code.toString();
    }

    private static String removeComments(String code) {
        code = code.replaceAll("/\\*.*?\\*/", "");
        code = code.replaceAll("//.*", "");
        return code.trim();
    }
}
