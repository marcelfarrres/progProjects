public class Dictionary {

    public static String getTokenFromCode(String word) {
        return switch (word) {
            case "main" -> "main";
            case "number" -> "number";
            case "boolean" -> "boolean";
            case "character" -> "character";
            case "word" -> "word";
            case "print" -> "print";
            case "scan" -> "scan";
            case "repeat_until" -> "repeat_until";
            case "if" -> "if";
            case "otherwise" -> "otherwise";
            case "get_back" -> "return";
            case "decision_block" -> "decision_block";
            case "case" -> "case";
            case "break" -> "break";
            case "yay" -> "true";
            case "nay" -> "false";
            case "=" -> "assign";
            case "+" -> "plus";
            case "-" -> "minus";
            case "*" -> "multiply";
            case "/" -> "divide";
            case "%" -> "modulo";
            case "AND" -> "and";
            case "OR" -> "or";
            case "NOT" -> "not";
            case "==" -> "equality";
            case "NOT=" -> "not_equal";
            case ">" -> "greater";
            case "<" -> "less";
            case ">=" -> "greater_equal";
            case "<=" -> "less_equal";
            case "++" -> "increment";
            case "--" -> "decrement";
            default -> {
                if (word.matches("\\d+")) yield "int";
                else if (word.matches("\".*\"")) yield "string";
                else if (word.matches("'.'")) yield "char";
                else yield "identifier";
            }
        };
    }
}