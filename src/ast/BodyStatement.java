package ast;

import java.util.ArrayList;
import java.util.List;

public class BodyStatement implements Statement {
    private final List<Statement> statements;
    private final List<String> localVariables;
    private final List<String> allVariables;

    public BodyStatement() {
        statements = new ArrayList<>();
        localVariables = new ArrayList<>();
        allVariables = new ArrayList<>();
    }

    public void add(Statement statement) {
        statements.add(statement);
    }

    public void addLocalVariable(String var) {
        localVariables.add(var);
    }

    public void addAllVariable(String var) {
        allVariables.add(var);
    }

    public List<String> getLocalVariables() {
        return localVariables;
    }

    public List<String> getAllVariables() {
        return allVariables;
    }

    public boolean isExist(String var) {
        return localVariables.contains(var);
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
