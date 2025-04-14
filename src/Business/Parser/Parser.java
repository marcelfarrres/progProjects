package Business.Parser;

import Business.Scanner.Tokens.Token;

import java.util.HashMap;
import java.util.List;

public class Parser {

    private List<Token> tokenFromScanner;
    private HashMap<String, List<String>> grammarMap = new HashMap<>();

    public Parser(List<Token> ScannerListOfTokens){
        this.tokenFromScanner = ScannerListOfTokens;
    }

    public void parse(){
        GrammarAnalysis grammarAnalysis = new GrammarAnalysis(tokenFromScanner);
        grammarAnalysis.analyzeGrammar();
        grammarMap = grammarAnalysis.getGrammarMap();
        FirstFollowBuilder firstFollowBuilder = new FirstFollowBuilder(grammarMap);
        ParsingTableBuilder parsingTableBuilder = new ParsingTableBuilder(firstFollowBuilder.getGrammarMap(), firstFollowBuilder.getFirstSet(), firstFollowBuilder.getFollowSet());
    }

}
