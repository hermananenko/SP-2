package ast;

public class BinaryExpression implements Expression {
    private final Expression expr1, expr2;
    private final char operation;

    public BinaryExpression(char operation, Expression expr1, Expression expr2) {
        this.operation = operation;
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    public char getOperation() {
        return operation;
    }

    public Expression getExpr1() {
        return expr1;
    }

    public Expression getExpr2() {
        return expr2;
    }
}
