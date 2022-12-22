package ast;

public class AssignmentStatement implements Statement {
    private final String variable;
    private final boolean isParam;
    private final Expression expression;
    private final Character option;

    public AssignmentStatement(String variable, boolean isParam, Expression expression, Character option) {
        this.isParam = isParam;
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

    public boolean isParam() {
        return isParam;
    }
}
