package Business.Scanner;

import Business.Scanner.Tokens.Token;

public class Dictionary {

    public static Token getTokenFromCode(String word, int line) {
        return switch (word) {
            // Palabras clave (ajustadas para coincidir con la gramática donde sea necesario)
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
            case "get_back" -> new Token(word, "get_back", line); // <-- Corregido (antes era "return")
            case "decision_block" -> new Token(word, "decision_block", line);
            case "case" -> new Token(word, "case", line);
            case "break" -> new Token(word, "break", line);
            case "yay" -> new Token(word, "bool", line);   // <-- Corregido (gramática usa 'bool' para literales)
            case "nay" -> new Token(word, "bool", line);   // <-- Corregido (gramática usa 'bool' para literales)
            case "process" -> new Token(word, "process", line); // <-- Corregido (antes era "process_")
            case "light_process" -> new Token(word, "light_process", line);
            case "ascii" -> new Token(word, "ascii", line);
            case "default" -> new Token(word, "default", line); // Añadido por si acaso

            // Símbolos y Operadores (Corregidos para usar el símbolo literal como nombre de token)
            case "(" -> new Token(word, "op", line);      // ok (Gramática usa 'op')
            case ")" -> new Token(word, "cp", line);      // ok (Gramática usa 'cp')
            case "{" -> new Token(word, "ocb", line);     // ok (Gramática usa 'ocb')
            case "}" -> new Token(word, "ccb", line);     // ok (Gramática usa 'ccb')
            case ";" -> new Token(word, ";", line);       // ok (Nombre = Símbolo)
            case "," -> new Token(word, ",", line);       // ok (Nombre = Símbolo)
            case ":" -> new Token(word, ":", line);       // <-- AÑADIDO (Necesario para case/default)
            case "=" -> new Token(word, "=", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case "+" -> new Token(word, "+", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case "-" -> new Token(word, "-", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case "" -> new Token(word, "", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case "/" -> new Token(word, "/", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case "%" -> new Token(word, "%", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case "&&" -> new Token(word, "&&", line);     // <-- CORREGIDO (Nombre = Símbolo)
            case "||" -> new Token(word, "||", line);     // <-- CORREGIDO (Nombre = Símbolo)
            case "!" -> new Token(word, "!", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case "==" -> new Token(word, "==", line);     // <-- CORREGIDO (Nombre = Símbolo)
            case "!=" -> new Token(word, "!=", line);     // <-- CORREGIDO (Para usar != consistentemente)
            case "NOT=" -> new Token(word, "!=", line);   // <-- CORREGIDO (Mapea NOT= del código a != para el parser)
            case ">" -> new Token(word, ">", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case "<" -> new Token(word, "<", line);       // <-- CORREGIDO (Nombre = Símbolo)
            case ">=" -> new Token(word, ">=", line);     // <-- CORREGIDO (Nombre = Símbolo)
            case "<=" -> new Token(word, "<=", line);     // <-- CORREGIDO (Nombre = Símbolo)
            case "++" -> new Token(word, "++", line);     // <-- CORREGIDO (Nombre = Símbolo)
            case "--" -> new Token(word, "--", line);     // <-- CORREGIDO (Nombre = Símbolo)
            default -> {
                if (word.matches("\\d+")) {
                    yield new Token(word, "int", line); // Gramática usa 'int'
                } else if (word.matches("\".*\"")) {
                    String valueWithoutQuotes = word.substring(1, word.length() - 1);
                    yield new Token(valueWithoutQuotes, "string", line); // Gramática usa 'string'
                } else if (word.matches("'.'")) {
                    String valueWithoutQuotes = word.substring(1, word.length() - 1);
                    yield new Token(valueWithoutQuotes, "char", line); // Gramática usa 'char'
                }
                else if (word.matches("[a-zA-Z_][a-zA-Z0-9_]*")) { // Una regex básica para identificadores
                    yield new Token(word, "identifier", line); // Gramática usa 'identifier'
                } else {
                    System.err.println("Error Léxico: Token desconocido '" + word + "' en línea " + line);
                    yield new Token(word, "ERROR_UNKNOWN", line); // O manejarlo como prefieras
                }
            }
        };
    }
}