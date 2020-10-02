import ast.Statement;
import generator.CodeGenerator;
import parser.Lexer;
import parser.Parser;
import parser.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Lexer lexer = new Lexer(FileWorker.read("/1-2-Java-IO-83-Ananenko.py"));
            List<Token> tokenList = lexer.tokenize();

            for (int i = 0; i < tokenList.size(); i++) {
                System.out.println(tokenList.get(i));
            }

            List<Statement> stmtList = new Parser(tokenList).parse();
            String code = new CodeGenerator(stmtList, args.length).getCode();
            FileWorker.write("1-2-Java-IO-83-Ananenko.asm", code);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
