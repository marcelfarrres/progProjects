package Business.Scanner.Tokens;


public class Literal extends Token {
    
    private Integer valueInt; // El valor si es un entero
    private String valueString; // El valor si es una cadena

    public Literal(int valueInt, String name, Integer line) {
        super(name, line); 
        this.valueInt = valueInt;
        this.valueString = null; 
    }

    public Literal(String valueString, String name, Integer line) {
        super(name, line);
        this.valueString = valueString;
        this.valueInt = null; 
    }

    @Override
    public Integer getValueInt() {
        return valueInt;
    }

    @Override
    public String getValueString() {
        return valueString;
    }
}
