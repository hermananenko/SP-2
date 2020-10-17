package ast;

public class IfStatement implements Statement {
    private final Expression expression;
    private final Statement ifStatement;
    private final Statement elseStatement;

    public IfStatement(Expression expression, Statement ifStatement, Statement elseStatement) {
        this.expression = expression;
        this.ifStatement = ifStatement;
        this.elseStatement = elseStatement;
    }

    public Expression getExpression() {
        return expression;
    }

    public Statement getIfStatement() {
        return ifStatement;
    }

    public Statement getElseStatement() {
        return elseStatement;
    }
}
