package ast;

public class DefStatement implements Statement {
    private final String name;
    private final Statement body;
    private final Statement returnStatement;

    public DefStatement(String name,Statement body, Statement returnStatement) {
        this.name = name;
        this.body = body;
        this.returnStatement = returnStatement;
    }

    public String getName() {
        return name;
    }

    public Statement getBody() {
        return body;
    }

    public Statement getReturnStatement() {
        return returnStatement;
    }
}
