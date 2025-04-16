package Business.Parser; // Or your chosen package

import Business.Scanner.Tokens.Token;
import java.util.ArrayList;
import java.util.List;

public class ParseTreeNode {
    private String symbol; // Non-terminal or terminal name
    private Token token;   // The actual token if it's a terminal leaf
    private List<ParseTreeNode> children;

    // Constructor for non-terminal nodes
    public ParseTreeNode(String symbol) {
        this.symbol = symbol;
        this.token = null;
        this.children = new ArrayList<>();
    }

    // Constructor for terminal/leaf nodes
    public ParseTreeNode(Token token) {
        this.symbol = token.getName(); // Use token name as symbol
        this.token = token;
        this.children = new ArrayList<>(); // Leaf nodes have no children initially
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

    // Method to print the tree (recursive) - for visualization
    public void printTree(String indent, boolean last) {
        System.out.print(indent);
        if (last) {
            System.out.print("└─ ");
            indent += "   ";
        } else {
            System.out.print("├─ ");
            indent += "│  ";
        }

        // Print node information (symbol and value if applicable)
        String nodeValue = (token != null && token.getValue() != null) ? " [" + token.getValue() + "]" : "";
        System.out.println(symbol + nodeValue);

        for (int i = 0; i < children.size(); i++) {
            children.get(i).printTree(indent, i == children.size() - 1);
        }
    }
}