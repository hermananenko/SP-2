package ast;

public class DefStatement implements Statement {
    private final String name;
    private final Statement body;

    public DefStatement(String name,Statement body) {
        this.name = name;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public Statement getBody() {
        return body;
    }
}
