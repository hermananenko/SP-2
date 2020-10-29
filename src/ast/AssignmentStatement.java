package ast;

public class AssignmentStatement implements Statement {
    private final String variable;
    private final Expression expression;
    private final Character option;

    public AssignmentStatement(String variable, Expression expression, Character option) {
        this.variable = variable;
        this.expression = expression;
        this.option = option;
    }

    public String getVariable() {
        return variable;
    }

    public Expression getExpression() {
        return expression;
    }

    public Character getOption() {
        return option;
    }
}
