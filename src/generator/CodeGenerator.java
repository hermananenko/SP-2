package generator;

import ast.DefStatement;
import ast.NumberExpression;
import ast.ReturnStatement;
import ast.Statement;

import java.util.List;

public class CodeGenerator {
    private final List<Statement> ast;
    private final StringBuilder code = new StringBuilder();

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

        generate(mode);
        code.
                append("end start");
    }

    public String getCode() {
        return code.toString();
    }

    private void generate(int mode) {
        for (Statement statement : ast) {
            if (statement instanceof DefStatement) {
                final DefStatement def = (DefStatement) statement;
                code.append(def.getName());
                code.append(" proc\r\n");

                if (def.getReturnStatement() != null) {
                    final ReturnStatement ret = (ReturnStatement) def.getReturnStatement();

                    if (ret.getExpression() != null) {
                        final NumberExpression num = (NumberExpression) ret.getExpression();
                        code.append("\tmov eax,");
                        code.append(num.getValue());

                        if (mode > 0) {
                            code.append("\r\n\tfn MessageBoxA,0,str$(eax),\"1_6-2-Java-IO-83-Ananenko\",MB_OK");
                        }

                        code.append("\r\n\tret\r\n");
                    }
                }
                code.
                        append(def.getName())
                        .append(" endp\r\n\r\n")
                        .append("start:\r\n")
                        .append("\tinvoke ")
                        .append(def.getName())
                        .append("\r\n\tinvoke ExitProcess,0\r\n");
            }
        }
    }
}
