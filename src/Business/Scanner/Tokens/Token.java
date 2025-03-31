package Business.Scanner.Tokens;


public class Token {
    

    
    private Integer line; // line of where token is found
    private String name; // token from the disctionary
    private String value; // El valor si es una cadena
    
    public Token(String value, String name, Integer line){
        this.name = name;
        this.line = line;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Integer getLine() {
        return line;
    }

    public String getValue() {
        return value;
    }

}
