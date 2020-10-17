package generator;

import ast.*;
import lib.Variables;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    private final List<Statement> ast;
    private final StringBuilder code = new StringBuilder();
    int conditionalCount = 0;

    public CodeGenerator(List<Statement> ast) {
        this.ast = ast;

        code.append(".386\r\n");
        code.append(".model flat, stdcall\r\n");
        code.append("option casemap :none\r\n\r\n");
        code.append("include     C:\\masm32\\include\\masm32rt.inc\r\n\r\n");
        code.append("includelib  C:\\masm32\\lib\\masm32rt.lib\r\n\r\n");
        code.append(".data\r\n");
        code.append(".code\r\n\r\n");
        code.append("orp proc\r\n");
        code.append("\tcmp eax, 0\r\n");
        code.append("\tje _there\r\n");
        code.append("\tret\r\n");
        code.append("_there:\r\n");
        code.append("\tmov eax, ebx\r\n");
        code.append("\tret\r\n");
        code.append("orp endp\r\n\r\n");
        generate();
        code.append("end start");
    }

    public String getCode() {
        return code.toString();
    }

    private void generate() {
        for (Statement st : ast) {
            def((DefStatement) st);
        }
    }

    private void def(DefStatement def) {
        code.append(String.format("%s proc\r\n", def.getName()));
        for (String str : Variables.getVariables()) {
            code.append("\tpush 0\r\n");
        }
        code.append("\tpush ebp\r\n");
        code.append("\tmov ebp, esp\r\n");
        if (def.getBody() != null) {
            body(((BodyStatement) def.getBody()));
        }
        if (def.getReturnStatement() != null) {
            ret((ReturnStatement) def.getReturnStatement());
        }
        code.append(String.format("%s endp\r\n", def.getName()));
        code.append("start:\r\n");
        code.append(String.format("\tinvoke %s\r\n", def.getName()));
        code.append("\tinvoke ExitProcess,0\r\n");
    }

    private void body(BodyStatement bodyStatement) {
        List<Statement> statements = bodyStatement.getStatements();
        for (Statement st : statements) {
            if (st instanceof AssignmentStatement) {
                assignStatement((AssignmentStatement) st);
            }
            if (st instanceof IfStatement) {
                ifStatement((IfStatement) st);
            }
            if (st instanceof ReturnStatement) {
                ret((ReturnStatement) st);
            }
        }
    }

    private void assignStatement(AssignmentStatement assignStatement) {
        if (assignStatement.getExpression() instanceof BinaryExpression) {
            binary((BinaryExpression) assignStatement.getExpression());
        } else if (assignStatement.getExpression() instanceof UnaryExpression) {
            unary((UnaryExpression) assignStatement.getExpression());
        } else if (assignStatement.getExpression() instanceof NumberExpression) {
            num((NumberExpression) assignStatement.getExpression());
        } else if (assignStatement.getExpression() instanceof VariableExpression) {
            var((VariableExpression) assignStatement.getExpression());
        }
        int index = 4 * (Variables.getIndex(assignStatement.getVariable()) + 1);
        code.append("\tpop eax\r\n");
        code.append(String.format("\tmov dword ptr[ebp + %d], eax\r\n", index));
    }

    private void ifStatement(IfStatement ifStatement) {
        int thisCondCount = conditionalCount++;
        if (ifStatement.getExpression() instanceof BinaryExpression) {
            binary((BinaryExpression) ifStatement.getExpression());
        } else if (ifStatement.getExpression() instanceof UnaryExpression) {
            unary((UnaryExpression) ifStatement.getExpression());
        } else if (ifStatement.getExpression() instanceof NumberExpression) {
            num((NumberExpression) ifStatement.getExpression());
        } else if (ifStatement.getExpression() instanceof VariableExpression) {
            var((VariableExpression) ifStatement.getExpression());
        }
        code.append("\tpop eax\r\n");
        code.append("\tcmp eax, 0\r\n");
        if (ifStatement.getElseStatement() != null) {
            code.append(String.format("\tje else_%d\r\n", thisCondCount));
            body((BodyStatement) ifStatement.getIfStatement());
            code.append(String.format("\tjmp normal_%d\r\n", thisCondCount));
            code.append(String.format("else_%d:\r\n", thisCondCount));
            body((BodyStatement) ifStatement.getElseStatement());
        } else {
            code.append(String.format("\tje normal_%d\r\n", thisCondCount));
            body((BodyStatement) ifStatement.getIfStatement());
        }
        code.append(String.format("normal_%d:\r\n", thisCondCount));
    }

    private void ret(ReturnStatement ret) {
        if (ret.getExpression() instanceof BinaryExpression) {
            binary((BinaryExpression) ret.getExpression());
        } else if (ret.getExpression() instanceof UnaryExpression) {
            unary((UnaryExpression) ret.getExpression());
        } else if (ret.getExpression() instanceof NumberExpression) {
            num((NumberExpression) ret.getExpression());
        } else if (ret.getExpression() instanceof VariableExpression) {
            var((VariableExpression) ret.getExpression());
        }
        code.append("\tpop eax\r\n");
        code.append("\tpop ebp\r\n");
        code.append("\tpop ebx\r\n".repeat(Variables.getVariables().size()));
        code.append("\tfn MessageBoxA,0,str$(eax),\"1_6-2-Java-IO-83-Ananenko\",MB_OK\r\n");
        code.append("\tret\r\n");
    }

    private void binary(BinaryExpression bin) {
        if (bin.getExpr1() instanceof BinaryExpression) {
            binary((BinaryExpression) bin.getExpr1());
        }
        if (bin.getExpr1() instanceof UnaryExpression) {
            unary((UnaryExpression) bin.getExpr1());
        }
        if (bin.getExpr1() instanceof NumberExpression) {
            num((NumberExpression) bin.getExpr1());
        }
        if (bin.getExpr1() instanceof VariableExpression) {
            var((VariableExpression) bin.getExpr1());
        }
        if (bin.getExpr2() instanceof BinaryExpression) {
            binary((BinaryExpression) bin.getExpr2());
        }
        if (bin.getExpr2() instanceof UnaryExpression) {
            unary((UnaryExpression) bin.getExpr2());
        }
        if (bin.getExpr2() instanceof NumberExpression) {
            num((NumberExpression) bin.getExpr2());
        }
        if (bin.getExpr2() instanceof VariableExpression) {
            var((VariableExpression) bin.getExpr2());
        }
        code.append("\tpop ebx\r\n");
        code.append("\tpop eax\r\n");
        if (bin.getOperation() == '-') {
            code.append("\tsub eax, ebx\r\n");
        }
        if (bin.getOperation() == '/') {
            code.append("\tcdq\r\n");
            code.append("\tidiv ebx\r\n");
        }
        if (bin.getOperation() == '*') {
            code.append("\timul ebx\r\n");
        }
        if (bin.getOperation() == 'o') {
            code.append("\tcall orp\r\n");
        }
        code.append("\tpush eax\r\n");
    }

    private void unary(UnaryExpression un) {
        if (un.getExpr() instanceof BinaryExpression) {
            binary((BinaryExpression) un.getExpr());
        }
        if (un.getExpr() instanceof NumberExpression) {
            num((NumberExpression) un.getExpr());
        }
        if (un.getExpr() instanceof VariableExpression) {
            var((VariableExpression) un.getExpr());
        }
        if (un.getExpr() instanceof UnaryExpression) {
            unary((UnaryExpression) un.getExpr());
        }
        code.append("\tpop eax\r\n");
        code.append("\tneg eax\r\n");
        code.append("\tpush eax\r\n");
    }

    private void num(NumberExpression num) {
        code.append(String.format("\tmov eax, %d\r\n", num.getValue()));
        code.append("\tpush eax\r\n");
    }

    private void var(VariableExpression var) {
        int index = 4 * (Variables.getIndex(var.getName()) + 1);
        code.append(String.format("\tmov eax, [ebp+%d]\r\n", index));
        code.append("\tpush eax\r\n");
    }
}