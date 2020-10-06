package generator;

import ast.*;

import java.util.List;

public class CodeGenerator {
    private final List<Statement> ast;
    private final StringBuilder code = new StringBuilder();
    private final String regs = "abcd";

    public CodeGenerator(List<Statement> ast, int mode) {
        this.ast = ast;

        code.
                append(".386\r\n").
                append(".model flat, stdcall\r\n").
                append("option casemap :none\r\n\r\n").
                append("include     C:\\masm32\\include\\masm32rt.inc\r\n\r\n").
                append("includelib  C:\\masm32\\lib\\masm32rt.lib\r\n\r\n").
                append(".data\r\n").
                append(".code\r\n");
        generate();
        code.
                append("end start");
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
        code.
                append(def.getName()).
                append(" proc\r\n").
                append("\tmov edx,0\r\n");
        ret((ReturnStatement) def.getReturnStatement());
        code.
                append(def.getName()).
                append(" endp\r\n").
                append("start:\r\n").
                append("\tinvoke ").
                append(def.getName()).
                append("\r\n").
                append("\tinvoke ExitProcess,0\r\n");
    }

    private void ret(ReturnStatement ret) {
        if (ret.getExpression() instanceof BinaryExpression) {
            ((BinaryExpression) ret.getExpression()).setReg('a');
            binary((BinaryExpression) ret.getExpression());
        } else if (ret.getExpression() instanceof UnaryExpression) {
            ((UnaryExpression) ret.getExpression()).setReg('a');
            unary((UnaryExpression) ret.getExpression());
        } else if (ret.getExpression() instanceof NumberExpression) {
            ((NumberExpression) ret.getExpression()).setReg('a');
            num((NumberExpression) ret.getExpression());
        }
        code.
                append("\tfn MessageBoxA,0,str$(eax),\"1_6-2-Java-IO-83-Ananenko\",MB_OK\r\n").
                append("\tret\r\n");
    }

    private void binary(BinaryExpression bin) {
        char curReg = bin.getReg();
        char child1 = regs.charAt(regs.indexOf(curReg));
        char child2 = regs.charAt(regs.indexOf(curReg) + 1);
        if (bin.getExpr1() instanceof BinaryExpression) {
            ((BinaryExpression) bin.getExpr1()).setReg(child1);
            binary((BinaryExpression) bin.getExpr1());
        }
        if (bin.getExpr2() instanceof BinaryExpression) {
            ((BinaryExpression) bin.getExpr2()).setReg(child2);
            binary((BinaryExpression) bin.getExpr2());
        }
        if (bin.getExpr1() instanceof UnaryExpression) {
            ((UnaryExpression) bin.getExpr1()).setReg(child1);
            unary((UnaryExpression) bin.getExpr1());
        }
        if (bin.getExpr2() instanceof UnaryExpression) {
            ((UnaryExpression) bin.getExpr2()).setReg(child2);
            unary((UnaryExpression) bin.getExpr2());
        }
        if (bin.getExpr1() instanceof NumberExpression) {
            ((NumberExpression) bin.getExpr1()).setReg(child1);
            num((NumberExpression) bin.getExpr1());
        }
        if (bin.getExpr2() instanceof NumberExpression) {
            ((NumberExpression) bin.getExpr2()).setReg(child2);
            num((NumberExpression) bin.getExpr2());
        }
        if (bin.getOperation() == '-') {
            code.
                    append("\tsub ").
                    append("e" + child1 + "x,").
                    append("e" + child2 + "x\r\n");
        }
        if (bin.getOperation() == '/') {
            code.
                    append("\tidiv " + "e" + child2 + "x\r\n").
                    append("\tmov edx,0\r\n");
        }
    }

    private void unary(UnaryExpression un) {
        if (un.getExpr() instanceof BinaryExpression) {
            ((BinaryExpression) un.getExpr()).setReg(un.getReg());
            binary((BinaryExpression) un.getExpr());
        }
        if (un.getExpr() instanceof NumberExpression) {
            ((NumberExpression) un.getExpr()).setReg(un.getReg());
            num((NumberExpression) un.getExpr());
        }
        code.
                append("\tnot edx\r\n").
                append("\tneg " + "e" + un.getReg() + "x\r\n");
    }

    private void num(NumberExpression num) {
        code.append("\tmov " + "e" + num.getReg() + "x," + num.getValue() + "\r\n");
    }
}
