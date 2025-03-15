package Business.Scanner.Tokens;


public class Token {
    

    
    private Integer line; // line of where token is found
    private String name; // token from the disctionary

    
    public Token(String name, Integer line){
        this.name = name;
        this.line = line;
    }

    public String getName() {
        return name;
    }

    public Integer getLine() {
        return line;
    }

}
