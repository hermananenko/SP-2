package ast;

public class ParameterExpression implements Expression {
    private final String name;

    public ParameterExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
