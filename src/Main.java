import Business.Parser.*;
import Business.Scanner.*;
import Business.Scanner.Tokens.Token;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


public class Main {

    private static final Scanner scanner = new Scanner();
    private static Parser parser;

    public static void main(String[] args) throws IOException {
       

        List<Token> ScannerListOfTokens = scanner.scan();
        parser = new Parser(ScannerListOfTokens);
        parser.parse();

    }
}
