package parser;

import ast.*;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Parser {

    private static final Token EOF = new Token(TokenType.EOF, "");

    private final List<Token> tokens;
    private final int size;
    private int pos;

    private final List<List<String>> defNames;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        size = tokens.size();
        defNames = new ArrayList<>();
    }

    public List<Statement> parse() {
        final List<Statement> result = new ArrayList<>();
        while (!match(TokenType.EOF)) {
            if (match(TokenType.DEF)) {
                result.add(defStatement());
            } else if (get(0).getType() == TokenType.ID) {
                String name = get(0).getText();
                int line = get(0).getLine();
                int paramCount = 0;
                List<Expression> parameters = new ArrayList<>();
                consume(TokenType.ID);
                consume(TokenType.OPEN_BRACKET);
                if (!match(TokenType.CLOSE_BRACKET)) {
                    while (!match(TokenType.CLOSE_BRACKET)) {
                        Expression parameter = expression(new BodyStatement(), new ArrayList<>());
                        if (get(0).getType() != TokenType.CLOSE_BRACKET) {
                            consume(TokenType.COMMA);
                        }
                        parameters.add(parameter);
                        paramCount++;
                    }
                }

                boolean defIsExists = false;
                for (List<String> def : defNames) {
                    if (def.contains(name)) {
                        for (String numOfIndexes : def) {
                            if (Integer.toString(paramCount).equals(numOfIndexes)) {
                                defIsExists = true;
                            }
                        }
                    }
                }

                if (defIsExists) {
                    result.add(new DefExpression(name, parameters));
                } else {
                    String message = String.format("Рядок %d: функцію %s(%d params) не знайдено", line, name, paramCount);
                    throw new SyntaxException(message);
                }
            }
        }
        return result;
    }

    private Statement defStatement() {
        String name = get(0).getText();
        int paramCount = 0;
        List<String> parameters = new ArrayList<>();
        consume(TokenType.ID); consume(TokenType.OPEN_BRACKET);
        if (!match(TokenType.CLOSE_BRACKET)) {
            while (!match(TokenType.CLOSE_BRACKET)) {
                String paramName = consume(TokenType.ID).getText();
                if (get(0).getType() != TokenType.CLOSE_BRACKET) {
                    consume(TokenType.COMMA);
                }
                parameters.add(paramName);
                paramCount++;
            }
        }
        consume(TokenType.COLON);

        boolean defNameIsExists = false;
        for (List<String> def : defNames) {
            if (def.contains(name)) {
                def.add(Integer.toString(paramCount));
                defNameIsExists = true;
                break;
            }
        }
        if (!defNameIsExists) {
            defNames.add(new ArrayList<>());
            defNames.get(defNames.size() - 1).add(name);
            defNames.get(defNames.size() - 1).add(Integer.toString(paramCount));
        }

        return new DefStatement(name, block(null, parameters), parameters);
    }

    private Statement block(BodyStatement parent, List<String> params) {
        final BodyStatement block = new BodyStatement();
        if (parent != null) {
            for (String parentVar : parent.getVariables()) {
                block.addVariable(parentVar);
            }
        }
        boolean isFirstIteration = true;
        int blockInd = 0;
        int currInd;
        while (match(TokenType.INDENT)) {
            blockInd++;
        }
        currInd = blockInd;
        while (get(1).getType() != TokenType.EOF) {
            while (get(currInd).getType() == TokenType.INDENT && !isFirstIteration) {
                currInd++;
            }

            if (currInd != blockInd) {
                if (get(currInd).getType() == TokenType.ELSE) {
                    while (match(TokenType.INDENT)) { }
                }
                break;
            } else {
                while (match(TokenType.INDENT)) { }
            }

            if (match(TokenType.IF)) {
                block.add(ifElse(block, params));
            } else if (match(TokenType.RETURN)) {
                block.add(returnStatement(block, params));
            } else if (get(1).getType() == TokenType.EQ || get(1).getType() == TokenType.MUL_EQ) {
                block.add(assignmentStatement(block, params));
            } else {
                throw new SyntaxException(String.format("Рядок %d : Невідома операція!", get(0).getLine()));
            }

            isFirstIteration = false;
            currInd = 0;
        }
        return block;
    }

    private Statement assignmentStatement(BodyStatement block, List<String> params) {
        final Token current = get(0);

        if (match(TokenType.ID)) {
            final String variable = current.getText();
            if (match(TokenType.EQ)) {
                if (!block.isExist(variable)) {
                    block.addVariable(variable);
                }
                return new AssignmentStatement(variable, expression(block, params), 'n');
            } else if (match(TokenType.MUL_EQ)) {
                if (block.isExist(variable)) {
                    return new AssignmentStatement(variable, expression(block, params), '*');
                } else {
                    String message = String.format("Рядок %d: змінної \"%s\" не знайдено!", current.getLine(), variable);
                    throw new SyntaxException(message);
                }
            }
        }
        throw new SyntaxException("Помилка яка ніколи не виникне:)");
    }

    private Statement ifElse(BodyStatement block, List<String> params) {
        consume(TokenType.OPEN_BRACKET);
        final Expression condition = expression(block, params);
        consume(TokenType.CLOSE_BRACKET);
        consume(TokenType.COLON);
        final Statement ifStatement = block(block, params);
        final Statement elseStatement;
        if (match(TokenType.ELSE)) {
            match(TokenType.COLON);
            elseStatement = block(block, params);
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

    private Statement returnStatement(BodyStatement block, List<String> params) {
            return new ReturnStatement(expression(block, params));
    }

    private Expression expression(BodyStatement block, List<String> params) {
        return logical(block, params);
    }

    private Expression logical(BodyStatement block, List<String> params) {
        Expression result = subtraction(block, params);

        while (true) {
            if (match(TokenType.OR)) {
                result = new BinaryExpression('o', result, subtraction(block, params));
                continue;
            }
            break;
        }
        return result;
    }

    private Expression subtraction(BodyStatement block, List<String> params) {
        Expression result = division(block, params);

        while (true) {
            if (match(TokenType.MINUS)) {
                result = new BinaryExpression('-', result, division(block, params));
                continue;
            }
            break;
        }
        return result;
    }

    private Expression division(BodyStatement block, List<String> params) {
        Expression result = unary(block, params);

        while (true) {
            if (match(TokenType.DIVISION)) {
                result = new BinaryExpression('/', result, unary(block, params));
                continue;
            }
            if (match(TokenType.MUL)) {
                result = new BinaryExpression('*', result, unary(block, params));
                continue;
            }
            break;
        }
        return result;
    }

    private Expression unary(BodyStatement block, List<String> params) {
        if (match(TokenType.MINUS)) {
            if (get(0).getType() == TokenType.MINUS) {
                return new UnaryExpression('-', unary(block, params));
            }
            return new UnaryExpression('-', primary(block, params));
        }
        return primary(block, params);
    }

    private Expression primary(BodyStatement block, List<String> params) {
        final Token current = get(0);
        if (match(TokenType.NUM)) {
            return new NumberExpression(Integer.parseInt(current.getText()));
        } else if (match(TokenType.ID)){
            String name = current.getText();
            if (get(0).getType() == TokenType.OPEN_BRACKET) {
                List<Expression> parameters = new ArrayList<>();;
                int paramCount = 0;
                consume(TokenType.OPEN_BRACKET);
                if (!match(TokenType.CLOSE_BRACKET)) {
                    while (!match(TokenType.CLOSE_BRACKET)) {
                        Expression parameter = expression(new BodyStatement(), params);
                        if (get(0).getType() != TokenType.CLOSE_BRACKET) {
                            consume(TokenType.COMMA);
                        }
                        parameters.add(parameter);
                        paramCount++;
                    }

                    boolean defIsExists = false;
                    for (List<String> def : defNames) {
                        if (def.contains(name)) {
                            for (String numOfIndexes : def) {
                                if (Integer.toString(paramCount).equals(numOfIndexes)) {
                                    defIsExists = true;
                                }
                            }
                        }
                    }

                    if (defIsExists) {
                        return new DefExpression(name, parameters);
                    } else {
                        String message = String.format("Рядок %d: функцію %s(%d params) не знайдено", get(0).getLine(), name, paramCount);
                        throw new SyntaxException(message);
                    }
                }
            }

            if (block.isExist(current.getText())) {
                return new VariableExpression(name);
            } else if (params.contains(name)) {
                return new ParameterExpression(name);
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
            Expression result = expression(block, params);
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
