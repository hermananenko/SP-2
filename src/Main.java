import ast.Statement;
import generator.CodeGenerator;
import parser.Lexer;
import parser.Parser;
import parser.Token;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            Lexer lexer = new Lexer(FileWorker.read("/5-2-Java-IO-83-Ananenko.py"));
            List<Token> tokenList = lexer.tokenize();

            for (Token tk : tokenList) {
                System.out.println(tk);
            }

            Parser parser = new Parser(tokenList);
            List<Statement> stmtList = parser.parse();
            String code = new CodeGenerator(stmtList, parser.orIsExist, parser.ltIsExist, parser.gtIsExist, parser.eqIsExist).getCode();
            FileWorker.write("5-2-Java-IO-83-Ananenko.asm", code);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
