package ast;

public class WhileStatement implements Statement {
    private final Expression expression;
    private final Statement body;

    public WhileStatement(Expression expression, Statement body) {
        this.expression = expression;
        this.body = body;
    }

    public Expression getExpression() {
        return expression;
    }

    public Statement getBody() {
        return body;
    }
}
