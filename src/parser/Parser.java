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
                body = functionBody();
            }
            return new DefStatement(name, body, returnStatement((BodyStatement) body));
        }
        return null;
    }

    private Statement functionBody() {
        final BodyStatement body = new BodyStatement();
        while (! (get(0).getType() == TokenType.RETURN)) {
            if (match(TokenType.IF)) {
                body.add(ifElse(body));
            } else {
                body.add(assignmentStatement(body));
                consume(TokenType.INDENT);
            }
        }
        return body;
    }

    private Statement block(BodyStatement parent) {
        final BodyStatement block = new BodyStatement();
        for (String parentVar : parent.getVariables()) {
            block.addVariable(parentVar);
        }
        boolean isFirstIteration = true;
        int blockInd = 0;
        int currInd;
        while (match(TokenType.INDENT)) {
            blockInd++;
        }
        currInd = blockInd;
        while (true) {
            if (!isFirstIteration) {
                while (match(TokenType.INDENT)) {
                    currInd++;
                }
            }
            isFirstIteration = false;
            if (currInd != blockInd) {
                break;
            }
            currInd = 0;
            if (match(TokenType.IF)) {
                block.add(ifElse(block));
            } else {
                block.add(assignmentStatement(block));
            }
        }
        return block;
    }

    private Statement assignmentStatement(BodyStatement block) {
        final Token current = get(0);

        if (match(TokenType.ID) && get(0).getType() == TokenType.EQ) {
            final String variable = current.getText();
            consume(TokenType.EQ);
            if (!Variables.isExists(variable)) {
                Variables.add(variable);
            }
            if (!block.isExist(variable)) {
                block.addVariable(variable);
            }
            return new AssignmentStatement(variable, expression(block));
        }
        return null;
    }

    private Statement ifElse(BodyStatement block) {
        consume(TokenType.OPEN_BRACKET);
        final Expression condition = expression(block);
        consume(TokenType.CLOSE_BRACKET);
        consume(TokenType.COLON);
        final Statement ifStatement = block(block);
        final Statement elseStatement;
        if (match(TokenType.ELSE)) {
            match(TokenType.COLON);
            elseStatement = block(block);
            for (String ifVar : ((BodyStatement) ifStatement).getVariables()) {
                for (String elsVar : ((BodyStatement) elseStatement).getVariables()) {
                    if (ifVar.equals(elsVar) && !block.isExist(ifVar)) {
                        block.addVariable(ifVar);
                    }
                }
            }
        } else {
            elseStatement = null;
        }
        return new IfStatement(condition, ifStatement, elseStatement);
    }

    private Statement returnStatement(BodyStatement block) {
        if (match(TokenType.RETURN)) {
            return new ReturnStatement(expression(block));
        }
        return null;
    }

    private Expression expression(BodyStatement block) {
        return logical(block);
    }

    private Expression logical(BodyStatement block) {
        Expression result = subtraction(block);

        while (true) {
            if (match(TokenType.OR)) {
                result = new BinaryExpression('o', result, subtraction(block));
                continue;
            }
            break;
        }
        return result;
    }

    private Expression subtraction(BodyStatement block) {
        Expression result = division(block);

        while (true) {
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression('-', result, division(block));
                continue;
            }
            break;
        }
        return result;
    }

    private Expression division(BodyStatement block) {
        Expression result = unary(block);

        while (true) {
            if (match(TokenType.DIVISION)) {
                result = new BinaryExpression('/', result, unary(block));
                continue;
            }
            if (match(TokenType.MUL)) {
                result = new BinaryExpression('*', result, unary(block));
                continue;
            }
            break;
        }
        return result;
    }

    private Expression unary(BodyStatement block) {
        if (match(TokenType.MINUS)) {
            if (get(0).getType() == TokenType.MINUS) {
                return new UnaryExpression('-', unary(block));
            }
            return new UnaryExpression('-', primary(block));
        }
        return primary(block);
    }

    private Expression primary(BodyStatement block) {
        final Token current = get(0);
        if (match(TokenType.NUM)) {
            return new NumberExpression(Integer.parseInt(current.getText()));
        } else if (match(TokenType.ID)){
            if (block.isExist(current.getText())) {
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
            Expression result = expression(block);
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
