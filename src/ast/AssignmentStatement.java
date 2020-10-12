package ast;

public class AssignmentStatement implements Statement {
    private final String variable;
    private final Expression expression;

    public AssignmentStatement(String variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    public String getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }
}
