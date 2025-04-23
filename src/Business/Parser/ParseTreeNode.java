package Business.Parser; // or whatever pkg u use

import Business.Scanner.Tokens.Token;
import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {
    private String symbol; // terminal or NONterminal name
    private Token token;   // real token if this is a leaf
    private List<ParseTreeNode> children;

    // constructor for non-terminals
    public ParseTreeNode(String symbol) {
        this.symbol = symbol;
        this.token = null;
        this.children = new ArrayList<>();
    }

    // constructor for terminals (aka leaves)
    public ParseTreeNode(Token token) {
        this.symbol = token.getName(); // use the token name for symbol
        this.token = token;
        this.children = new ArrayList<>(); // leaves got no kids at start
    }

    public void addChild(ParseTreeNode child) {
        this.children.add(child);
    }

    public String getSymbol() {
        return symbol;
    }

    public Token getToken() {
        return token;
    }

    public List<ParseTreeNode> getChildren() {
        return children;
    }

    // print the whole tree recursively, kinda nice to see
    public void printTree(String indent, boolean last) {
        System.out.print(indent);
        if (last) {
            System.out.print("└─ ");
            indent += "   ";
        } else {
            System.out.print("├─ ");
            indent += "│  ";
        }

        // show symbol and value (if any)
        String nodeValue = (token != null && token.getValue() != null) ? " [" + token.getValue() + "]" : "";
        System.out.println(symbol + nodeValue);

        for (int i = 0; i < children.size(); i++) {
            children.get(i).printTree(indent, i == children.size() - 1);
        }
    }
}
