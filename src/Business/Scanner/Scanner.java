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
            int flagString = 0;
            int tokenTOTAL = 0;
            String temporaryWord = "";
            for (String cleanLine : lines) {
                String[] words = cleanLine.split("\\s+");
                
                for (String word : words) {

                    if (!word.trim().isEmpty()) {
                        if(word.charAt(0) == '\"' && flagString == 1){
                            if(word.charAt(word.length()-1) != '\"' && word.length() > 1){
                                temporaryWord += (" " + word);


                            }else{
                                Token token = Dictionary.getTokenFromCode(temporaryWord, lineNumber);
                                tokens.add(token);
                                System.out.println(token.getName() + " (Line: " + lineNumber + ")");
                                tokenTOTAL++;

                                temporaryWord = "";
                                flagString = 0;
                            }
                        }else{
                            tokenTOTAL++;
                            flagString = 1;
                            Token token = Dictionary.getTokenFromCode(word, lineNumber);
                            tokens.add(token);
                            System.out.println( token.getName() + " (Line: " + lineNumber + ")");


                        }
                        }
                }
                lineNumber++;


            }
            System.out.println("Total Tokens: " + tokenTOTAL);
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
