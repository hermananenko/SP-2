package generator;

import ast.*;
import lib.Variables;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    private final List<Statement> ast;
    private final StringBuilder code = new StringBuilder();
    private int varCount = 0;
    private List<String> localVars = new ArrayList<>();
    private List<String> blockVars;

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
        ret((ReturnStatement) def.getReturnStatement());
        code.append(String.format("%s endp\r\n", def.getName()));
        code.append("start:\r\n");
        code.append(String.format("\tinvoke %s\r\n", def.getName()));
        code.append("\tinvoke ExitProcess,0\r\n");
    }

    private void body(BodyStatement bodyStatement) {
        List<Statement> statements = bodyStatement.getStatements();
        blockVars = bodyStatement.getVariables();
        for (Statement st : statements) {
            if (st instanceof AssignmentStatement) {
                assignStatement((AssignmentStatement) st);
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

//        if (localVars.contains(assignStatement.getVariable())) {
        int index = 4 * (Variables.getIndex(assignStatement.getVariable()) + 1);
        code.append("\tpop eax\r\n");
        code.append(String.format("\tmov dword ptr[ebp + %d], eax\r\n", index));
//        } else {
//            localVars.add(assignStatement.getVariable());
//            varCount++;
//            code.append("\tpop eax\r\n");
//            code.append("\tpop ebp\r\n");
//            code.append("\tpush eax\r\n");
//            code.append("\tpush ebp\r\n");
//            code.append("\tmov ebp, esp\r\n");
//        }
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
        code.append("\tpop ebx\r\n".repeat(varCount));
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