import ast.Statement;
import generator.CodeGenerator;
import lib.Variables;
import parser.Lexer;
import parser.Parser;
import parser.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Lexer lexer = new Lexer(FileWorker.read("/4-2-Java-IO-83-Ananenko.py"));
            List<Token> tokenList = lexer.tokenize();

            for (Token tk : tokenList) {
                System.out.println(tk);
            }

            List<Statement> stmtList = new Parser(tokenList).parse();
            String code = new CodeGenerator(stmtList).getCode();
            FileWorker.write("4-2-Java-IO-83-Ananenko.asm", code);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
