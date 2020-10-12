package ast;

import java.util.ArrayList;
import java.util.List;

public class BodyStatement implements Statement {
    private final List<Statement> statements;

    public BodyStatement() {
        statements = new ArrayList<>();
    }

    public void add(Statement statement) {
        statements.add(statement);
    }

    public List<Statement> getStatements() {
        return statements;
    }
}
