package Business.Scanner;

import Business.Scanner.Tokens.Token;

public class Dictionary {

    public static Token getTokenFromCode(String word, int line) {
        return switch (word) {
            case "main" -> new Token(word, "main", line);
            case "number" -> new Token(word, "number", line);
            case "boolean" -> new Token(word, "boolean", line);
            case "character" -> new Token(word, "character", line);
            case "word" -> new Token(word, "word", line);
            case "print" -> new Token(word, "print", line);
            case "scan" -> new Token(word, "scan", line);
            case "repeat_until" -> new Token(word, "repeat_until", line);
            case "if" -> new Token(word, "if", line);
            case "otherwise" -> new Token(word, "otherwise", line);
            case "get_back" -> new Token(word, "get_back", line);
            case "decision_block" -> new Token(word, "decision_block", line);
            case "case" -> new Token(word, "case", line);
            case "break" -> new Token(word, "break", line);
            case "yay" -> new Token(word, "yay", line);
            case "nay" -> new Token(word, "nay", line);
            case "process_" -> new Token(word, "process_", line);
            case "light_process_" -> new Token(word, "light_process_", line);
            case "ascii" -> new Token(word, "ascii", line);
            case "(" -> new Token(word, "(", line);
            case ")" -> new Token(word, ")", line);
            case "{" -> new Token(word, "{", line);
            case "}" -> new Token(word, "}", line);
            case ";" -> new Token(word, ";", line);
            case "," -> new Token(word, ",", line);
            case "‘" -> new Token(word, "‘", line);
            case "“" -> new Token(word, "“", line);
            case "=" -> new Token(word, "=", line);
            case "+" -> new Token(word, "+", line);
            case "-" -> new Token(word, "-", line);
            case "*" -> new Token(word, "*", line);
            case "/" -> new Token(word, "/", line);
            case "%" -> new Token(word, "%", line);
            case "AND" -> new Token(word, "AND", line);
            case "OR" -> new Token(word, "OR", line);
            case "NOT" -> new Token(word, "NOT", line);
            case "==" -> new Token(word, "==", line);
            case "NOT=" -> new Token(word, "NOT=", line);
            case ">" -> new Token(word, ">", line);
            case "<" -> new Token(word, "<", line);
            case ">=" -> new Token(word, ">=", line);
            case "<=" -> new Token(word, "<=", line);
            case "++" -> new Token(word, "++", line);
            case "--" -> new Token(word, "--", line);

            default -> {
                if (word.matches("\\d+")) {
                    yield new Token(word, "int", line); // Token entero
                } else if (word.matches("\".*\"")) {
                    yield new Token(word, "string", line); // Token string
                } else if (word.matches("'.'")) {
                    yield new Token(word, "char", line); // Token carácter
                } else {
                    yield new Token(word, "identifier", line); // Guardar el identificador con su valor
                }
            }
        };
    }
}
