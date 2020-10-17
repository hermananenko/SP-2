package ast;

public class DefExpression implements Expression {
    private final String name;

    public DefExpression(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
