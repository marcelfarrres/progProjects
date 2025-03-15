package Business.Scanner;

import Business.Scanner.Tokens.Token;
import Business.Scanner.Tokens.Literal;

public class Dictionary {

    public static Token getTokenFromCode(String word, int line) {
        return switch (word) {
            case "main" -> new Token("main", line);
            case "number" -> new Token("number", line);
            case "boolean" -> new Token("boolean", line);
            case "character" -> new Token("character", line);
            case "word" -> new Token("word", line);
            case "print" -> new Token("print", line);
            case "scan" -> new Token("scan", line);
            case "repeat_until" -> new Token("repeat_until", line);
            case "if" -> new Token("if", line);
            case "otherwise" -> new Token("otherwise", line);
            case "get_back" -> new Token("return", line);
            case "decision_block" -> new Token("decision_block", line);
            case "case" -> new Token("case", line);
            case "break" -> new Token("break", line);
            case "yay" -> new Token("true", line);
            case "nay" -> new Token("false", line);
            case "process_" -> new Token("process", line);
            case "light_process_" -> new Token("light_process", line);
            case "ascii" -> new Token("ascii", line);
            case "(" -> new Token("op", line);
            case ")" -> new Token("cp", line);
            case "{" -> new Token("ocb", line);
            case "}" -> new Token("ccb", line);
            case ";" -> new Token(";", line);
            case "," -> new Token(",", line);
            case "‘" -> new Token("‘", line);
            case "“" -> new Token("“", line);
            case "=" -> new Token("assign", line);
            case "+" -> new Token("plus", line);
            case "-" -> new Token("minus", line);
            case "*" -> new Token("multiply", line);
            case "/" -> new Token("divide", line);
            case "%" -> new Token("modulo", line);
            case "AND" -> new Token("and", line);
            case "OR" -> new Token("or", line);
            case "NOT" -> new Token("not", line);
            case "==" -> new Token("equality", line);
            case "NOT=" -> new Token("not_equal", line);
            case ">" -> new Token("greater", line);
            case "<" -> new Token("less", line);
            case ">=" -> new Token("greater_equal", line);
            case "<=" -> new Token("less_equal", line);
            case "++" -> new Token("increment", line);
            case "--" -> new Token("decrement", line);

            default -> {
                if (word.matches("\\d+")) {
                    yield new Literal(Integer.parseInt(word), "int", line); // Literal entero
                } else if (word.matches("\".*\"")) {
                    yield new Literal(word, "string", line); // Literal string
                } else if (word.matches("'.'")) {
                    yield new Literal(word, "char", line); // Literal carácter
                } else {
                    yield new Literal(word, "identifier", line); // Guardar el identificador con su valor
                }
            }
        };
    }
}
