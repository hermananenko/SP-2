package ast;

import java.util.ArrayList;
import java.util.List;

public class BodyStatement implements Statement {
    private final List<Statement> statements;
    private final List<String> variables;

    public BodyStatement() {
        statements = new ArrayList<>();
        variables = new ArrayList<>();
    }

    public void add(Statement statement) {
        statements.add(statement);
    }

    public void addVariable(String var) {
        variables.add(var);
    }

    public List<String> getVariables() {
        return variables;
    }

    public boolean isExist(String var) {
        return variables.contains(var);
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
