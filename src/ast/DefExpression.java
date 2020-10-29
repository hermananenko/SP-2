package ast;

import java.util.List;

public class DefExpression implements Expression, Statement {
    private final String name;
    private final List<Expression> parameters;

    public DefExpression(String name, List<Expression> parameters) {
        this.name = name;
        this.parameters = parameters;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getParameters() {
        return parameters;
    }
}
