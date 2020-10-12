package parser;

import ast.*;
import lib.Variables;

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
            String name = get(0).getText();
            consume(TokenType.ID); consume(TokenType.OPEN_BRACKET);
            consume(TokenType.CLOSE_BRACKET); consume(TokenType.COLON); consume(TokenType.INDENT);
            Statement body;
            if (get(0).getType() == TokenType.RETURN) {
                body = null;
            } else {
                body = body();
            }
            return new DefStatement(name, body, returnStatement());
        }
        return null;
    }

    private Statement body() {
        final BodyStatement body = new BodyStatement();
        while (! (get(0).getType() == TokenType.RETURN)) {
            body.add(assignmentStatement());
            consume(TokenType.INDENT);
        }
        return body;
    }

    private Statement assignmentStatement() {
        final Token current = get(0);

        if (match(TokenType.ID) && get(0).getType() == TokenType.EQ) {
            final String variable = current.getText();
            consume(TokenType.EQ);
            Variables.add(variable);
            return new AssignmentStatement(variable, expression());
        }
        return null;
    }

    private Statement returnStatement() {
        if (match(TokenType.RETURN)) {
            return new ReturnStatement(expression());
        }
        return null;
    }

    private Expression expression() {
        return logical();
    }

    private Expression logical() {
        Expression result = subtraction();

        while (true) {
            if (match(TokenType.OR)) {
                result = new BinaryExpression('o', result, subtraction());
                continue;
            }
            break;
        }
        return result;
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
            if (match(TokenType.MUL)) {
                result = new BinaryExpression('*', result, unary());
            }
            break;
        }
        return result;
    }

    private Expression unary() {
        if (match(TokenType.MINUS)) {
            if (get(0).getType() == TokenType.MINUS) {
                return new UnaryExpression('-', unary());
            }
            return new UnaryExpression('-', primary());
        }
        return primary();
    }

    private Expression primary() {
        final Token current = get(0);
        if (match(TokenType.NUM)) {
            return new NumberExpression(Integer.parseInt(current.getText()));
        } else if (match(TokenType.ID)){
            if (Variables.isExists(current.getText())) {
                return new VariableExpression(current.getText());
            } else {
                throw new SyntaxException(String.format("Рядок %d : Змінної \"%s\" не знайдено!", current.getLine(), current.getText()));
            }
        }else if (match(TokenType.REAL)) {
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

        throw new SyntaxException(String.format("Рядок %d : \"%s\" неможливо привести до int", current.getLine(), current.getText()));
    }

    private boolean match(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) return false;
        pos++;
        return true;
    }

    private Token consume(TokenType type) {
        final Token current = get(0);
        if (type != current.getType()) {
            throw new SyntaxException(String.format("Рядок %d : токен ", current.getLine()) + current.getType() + ", повинно бути " + type);
        }
        pos++;
        return current;
    }

    private Token get(int relativePosition) {
        final int position = pos + relativePosition;
        if(position >= size) {
            return EOF;
        }
        return tokens.get(position);
    }
}
