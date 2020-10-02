package ast;

public class DefStatement implements Statement{
    private final String name;
    private final Statement returnStatement;

    public DefStatement(String name, Statement returnStatement) {
        this.name = name;
        this.returnStatement = returnStatement;
    }

    public String getName() {
        return name;
    }

    public Statement getReturnStatement() {
        return returnStatement;
    }
}
