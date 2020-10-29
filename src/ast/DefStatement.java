package ast;

import java.util.ArrayList;
import java.util.List;

public class DefStatement implements Statement {
    private final String name;
    private final Statement body;
    private final List<String> parameters;

    public DefStatement(String name, Statement body, List<String> parameters) {
        this.name = name;
        this.body = body;
        this.parameters = parameters;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public String getName() {
        return name;
    }

    public Statement getBody() {
        return body;
    }
}