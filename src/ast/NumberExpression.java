package ast;

public class NumberExpression implements Expression{
    private final int value;
    private char reg;

    public NumberExpression(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public char getReg() {
        return reg;
    }

    public void setReg(char reg) {
        this.reg = reg;
    }
}
