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
        throw new SyntaxException("Помилка при створенні функції");
    }

    private Statement returnStatement() {
        match(TokenType.ID); match(TokenType.OPEN_BRACKET); match(TokenType.CLOSE_BRACKET);
        match(TokenType.COLON); match(TokenType.INDENT);
        if (match(TokenType.RETURN)) {
            return new ReturnStatement(expression());
        }
        throw new SyntaxException("Помилка при створенні функції");
    }

    private Expression expression() {
        return subtraction();
    }

    private Expression subtraction() {
        Expression result = division();

        while (true) {
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression('-', result, division());
                continue;
            }
            break;
        }
        return result;
    }

    private Expression division() {
        Expression result = unary();

        while (true) {
            if (match(TokenType.DIVISION)) {
                result = new BinaryExpression('/', result, unary());
                continue;
            }
            break;
        }
        return result;
    }

    private Expression unary() {
        if (match(TokenType.MINUS)) {
            return new UnaryExpression('-', primary());
        }
        return primary();
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
        if (match(TokenType.OPEN_BRACKET)) {
            Expression result = expression();
            match(TokenType.CLOSE_BRACKET);
            return result;
        }

        throw new SyntaxException("Неможливо привести до int");
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
}
