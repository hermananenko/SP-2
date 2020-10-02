package parser;

import ast.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private static final Token EOF = new Token(TokenType.EOF, "");

    private final List<Token> tokens;
    private final int size;
    private int pos;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        size = tokens.size();
    }

    public List<Statement> parse() {
        final List<Statement> result = new ArrayList<>();
        while (!match(TokenType.EOF)) {
            result.add(defStatement());
        }
        return result;
    }

    private Statement defStatement() {
        if (match(TokenType.DEF)) {
            return new DefStatement(get(0).getText(), returnStatement());
        }
        throw new SysntaxExeption("Помилка при створенні функції");
    }

    private Statement returnStatement() {
        match(TokenType.ID); match(TokenType.OPEN_BRACKET); match(TokenType.CLOSE_BRACKET);
        match(TokenType.COLON); match(TokenType.INDENT);
        if (match(TokenType.RETURN)) {
            return new ReturnStatement(primary());
        }
        throw new SysntaxExeption("Помилка при створенні функції");
    }

    private Expression primary() {
        final Token current = get(0);
        if (match(TokenType.NUM)) {
            return new NumberExpression(Integer.parseInt(current.getText()));
        } else if (match(TokenType.REAL)) {
            return new NumberExpression((int) Double.parseDouble(current.getText()));
        } else if (match(TokenType.BIN_NUMBER)) {
            return new NumberExpression(Integer.parseInt(current.getText(), 2));
        } else if (match(TokenType.CHARACTER)) {
            return new NumberExpression(current.getText().charAt(0));
        }

        throw new SysntaxExeption("Неможливо привести до int");
    }

    private boolean match(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }

    private Token get(int relativePosition) {
        final int position = pos + relativePosition;
        if(position >= size) {
            return EOF;
        }
        return tokens.get(position);
    }

    class SysntaxExeption extends RuntimeException {
        public SysntaxExeption(String message) {
            super(message);
        }
    }
}