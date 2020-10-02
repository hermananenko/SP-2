package ast;

public class ReturnStatement implements Statement{
    private final Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
