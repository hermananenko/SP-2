package generator;

import ast.*;

import java.util.ArrayList;
import java.util.List;

public class CodeGenerator {
    private final List<Statement> ast;
    private final StringBuilder code = new StringBuilder();
    private List<String> Variables;
    private List<String> Parameters;

    private int conditionalCount = 0;
    private int loopCount = 0;
    private int currentLoopCount = 0;

    public CodeGenerator(List<Statement> ast, boolean orIsExist, boolean ltIsExist, boolean gtIsExist, boolean eqIsExist) {
        this.ast = ast;

        DefExpression callFunction = null;
        for (Statement st : ast) {
            if (st instanceof DefExpression) {
                callFunction = (DefExpression) st;
                break;
            }
        }

        code.append(".386\r\n");
        code.append(".model flat, stdcall\r\n");
        code.append("option casemap :none\r\n\r\n");
        code.append("include     C:\\masm32\\include\\masm32rt.inc\r\n\r\n");
        code.append("includelib  C:\\masm32\\lib\\masm32rt.lib\r\n\r\n");
        code.append(".data\r\n");
        code.append(".code\r\n\r\n");

        if (orIsExist) {
            code.append("orp proc\r\n");
            code.append("\tcmp eax, 0\r\n");
            code.append("\tje _there\r\n");
            code.append("\tret\r\n");
            code.append("_there:\r\n");
            code.append("\tmov eax, ebx\r\n");
            code.append("\tret\r\n");
            code.append("orp endp\r\n\r\n");
        }

        if (eqIsExist) {
            code.append("eqp proc\r\n");
            code.append("\tcmp eax, ebx\r\n");
            code.append("\tje _there\r\n");
            code.append("\tmov eax, 0\r\n");
            code.append("\tret\r\n");
            code.append("_there:\r\n");
            code.append("\tmov eax, 1\r\n");
            code.append("\tret\r\n");
            code.append("eqp endp\r\n\r\n");
        }


        if (ltIsExist) {
            code.append("ltp proc\r\n");
            code.append("\tcmp eax, ebx\r\n");
            code.append("\tjl _there\r\n");
            code.append("\tmov eax, 0\r\n");
            code.append("\tret\r\n");
            code.append("_there:\r\n");
            code.append("\tmov eax, 1\r\n");
            code.append("\tret\r\n");
            code.append("ltp endp\r\n\r\n");
        }

        if (gtIsExist) {
            code.append("gtp proc\r\n");
            code.append("\tcmp eax, ebx\r\n");
            code.append("\tjg _there\r\n");
            code.append("\tmov eax, 0\r\n");
            code.append("\tret\r\n");
            code.append("_there:\r\n");
            code.append("\tmov eax, 1\r\n");
            code.append("\tret\r\n");
            code.append("gtp endp\r\n\r\n");
        }

        generate();
        code.append("start:\r\n");
        defCall(callFunction);
        code.append("\tpop eax\r\n");
        code.append("\tfn MessageBoxA,0,str$(eax),\"1_6-2-Java-IO-83-Ananenko\",MB_OK\r\n");
        code.append("\tinvoke ExitProcess,0\r\n");
        code.append("end start");
    }

    public String getCode() {
        return code.toString();
    }

    private void generate() {
        List<String> releasedDefs = new ArrayList<>();
        for (int i = 0; i < ast.size(); i++) {
            if (ast.get(i) instanceof DefStatement) {
                DefStatement defStatement = (DefStatement) ast.get(i);
                String name = defStatement.getName();
                if (releasedDefs.contains(name)) {
                    continue;
                }
                for (int j = i + 1; j < ast.size(); j++) {
                    if (ast.get(j) instanceof DefStatement) {
                        if (((DefStatement) ast.get(j)).getName().equals(name)) {
                            defStatement = (DefStatement) ast.get(j);
                        }
                    }
                }
                releasedDefs.add(name);

                def(defStatement);
            }
        }
    }

    private void def(DefStatement def) {
        code.append(String.format("%s proc\r\n", def.getName()));
        Variables = ((BodyStatement) def.getBody()).getAllVariables();
        Parameters = def.getParameters();
        code.append("\tpush ebp\r\n");
        code.append("\tmov ebp, esp\r\n");
        code.append(String.format("\tsub esp, %d\r\n", Variables.size() * 4));
        body(((BodyStatement) def.getBody()));
        code.append(String.format("%s endp\r\n\r\n", def.getName()));
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
            if (st instanceof WhileStatement) {
                whileLoop((WhileStatement) st);
            }
            if (st instanceof BreakStatement) {
                code.append(String.format("\tjmp endloop_%d\r\n", currentLoopCount));
            }
            if (st instanceof ContinueStatement) {
                code.append(String.format("\tjmp startloop_%d\r\n", currentLoopCount));
            }
        }
    }

    private void generateExpression(Expression expression) {
        if (expression instanceof BinaryExpression) {
            binary((BinaryExpression) expression);
        } else if (expression instanceof UnaryExpression) {
            unary((UnaryExpression) expression);
        } else if (expression instanceof NumberExpression) {
            num((NumberExpression) expression);
        } else if (expression instanceof VariableExpression) {
            var((VariableExpression) expression);
        } else if (expression instanceof DefExpression) {
            defCall((DefExpression) expression);
        } else if (expression instanceof ParameterExpression) {
            parameter((ParameterExpression) expression);
        }
    }

    private void assignStatement(AssignmentStatement assignStatement) {
        generateExpression(assignStatement.getExpression());
        if (assignStatement.isParam()) {
            code.append("\tpop eax\r\n");
            int index = 4 * (Parameters.indexOf(assignStatement.getVariable()) + 1) + 4;
            code.append(String.format("\tmov dword ptr[ebp+%d], eax\r\n", index));
        } else {
            int index = 4 * (Variables.indexOf(assignStatement.getVariable()) + 1);
            if (assignStatement.getOption() == '*') {
                code.append(String.format("\tmov eax, [ebp-%d]\r\n", index));
                code.append("\tpop ebx\r\n");
                code.append("\timul eax, ebx\r\n");
                code.append("\tpush eax\r\n");
            }
            code.append("\tpop eax\r\n");
            code.append(String.format("\tmov dword ptr[ebp-%d], eax\r\n", index));
        }
    }

    private void ifStatement(IfStatement ifStatement) {
        int thisCondCount = conditionalCount++;
        generateExpression(ifStatement.getExpression());
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

    private void whileLoop(WhileStatement whileStatement) {
        int localCount = ++loopCount;
        currentLoopCount = localCount;
        code.append(String.format("startloop_%d:\r\n", localCount));
        generateExpression(whileStatement.getExpression());
        code.append("\tpop eax\r\n");
        code.append("\tcmp eax, 0\r\n");
        code.append(String.format("\tje endloop_%d\r\n", localCount));
        body((BodyStatement) whileStatement.getBody());
        code.append(String.format("\tjmp startloop_%d\r\n", localCount));
        code.append(String.format("endloop_%d:\r\n", localCount));
    }

    private void ret(ReturnStatement ret) {
        generateExpression(ret.getExpression());
        code.append("\tpop eax\r\n");
        code.append("\tleave\r\n");
        code.append(String.format("\tret %d\r\n", 4 * Parameters.size()));
    }

    private void binary(BinaryExpression bin) {
        generateExpression(bin.getExpr1());
        generateExpression(bin.getExpr2());
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
        if (bin.getOperation() == '+') {
            code.append("\tadd eax, ebx\r\n");
        }
        if (bin.getOperation() == '<') {
            code.append("\tcall ltp\r\n");
        }
        if (bin.getOperation() == '>') {
            code.append("\tcall gtp\r\n");
        }
        if (bin.getOperation() == '%') {
            code.append("\tcdq\r\n");
            code.append("\tidiv ebx\r\n");
            code.append("\tmov eax, edx\r\n");
        }
        if (bin.getOperation() == '=') {
            code.append("\tcall eqp\r\n");
        }
        code.append("\tpush eax\r\n");
    }

    private void unary(UnaryExpression un) {
        generateExpression(un.getExpr());
        code.append("\tpop eax\r\n");
        code.append("\tneg eax\r\n");
        code.append("\tpush eax\r\n");
    }

    private void num(NumberExpression num) {
        code.append(String.format("\tmov eax, %d\r\n", num.getValue()));
        code.append("\tpush eax\r\n");
    }

    private void var(VariableExpression var) {
        int index = 4 * (Variables.indexOf(var.getName()) + 1);
        code.append(String.format("\tmov eax, [ebp-%d]\r\n", index));
        code.append("\tpush eax\r\n");
    }

    private void defCall(DefExpression defExpression) {
        if (defExpression.getParameters() != null) {
            for (int i = defExpression.getParameters().size() - 1; i >= 0; i--) {
                generateExpression(defExpression.getParameters().get(i));
            }
        }
        code.append(String.format("\tcall %s\r\n", defExpression.getName()));
        code.append("\tpush eax\r\n");
    }

    private void parameter(ParameterExpression parameterExpression) {
        int index = 4 * (Parameters.indexOf(parameterExpression.getName()) + 1) + 4;
        code.append(String.format("\tmov eax, [ebp+%d]\r\n", index));
        code.append("\tpush eax\r\n");
    }
}