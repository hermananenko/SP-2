package parser;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Lexer {
    private final Hashtable reserved;

    private final String input;
    private final int length;

    private final List<Token> tokens;

    private int pos;

    private int line = 1;

    public Lexer(String input) {
        this.input = input;
        length = input.length();

        tokens = new ArrayList<>();
        reserved = new Hashtable();

        reserve(new Token(TokenType.DEF, "def"));
        reserve(new Token(TokenType.RETURN, "return"));
        reserve(new Token(TokenType.OR, "or"));
        reserve(new Token(TokenType.IF, "if"));
        reserve(new Token(TokenType.ELSE, "else"));
    }

    private void reserve(Token t) {
        reserved.put(t.getText(), t);
    }

    public List<Token> tokenize() {
        List<Integer> indents = new ArrayList<>();
        int prevIndent = 0;
        int currIndent = 0;
        while (pos < length) {
            final char current = peek(0);
            if (Character.isDigit(current)) tokenizeNumber();
            else if (Character.isLetter(current)) tokenizeWord();
            else {
                switch (current) {
                    case '(':
                        addToken(TokenType.OPEN_BRACKET);
                        next();
                        break;
                    case ')':
                        addToken(TokenType.CLOSE_BRACKET);
                        next();
                        break;
                    case ':':
                        addToken(TokenType.COLON);
                        next();
                        break;
                    case ' ':
                        next();
                        break;
                    case '\n':
                        char temp;
                        if ((temp = next()) == ' ') {
                            while (temp == ' ') {
                                currIndent++;
                                temp = next();
                            }
                            if (currIndent > prevIndent) {
                                indents.add(currIndent);
                            }
                            for (int i = 0; i < indents.indexOf(currIndent) + 1; i++) {
                                addToken(TokenType.INDENT);
                            }
                            prevIndent = indents.get(indents.indexOf(currIndent));
                            currIndent = 0;
                        }
                        line++;
                        break;
                    case '\'':
                        addToken(TokenType.CHARACTER, Character.toString(next()));
                        if (peek(1) != '\'') {
                            throw new RuntimeException();
                        }
                        next();
                        next();
                        break;
                    case '\"':
                        char temp2 = next();
                        StringBuffer buffer = new StringBuffer();
                        while (temp2 != '\"') {
                            buffer.append(temp2);
                            temp2 = next();
                        }
                        next();
                        addToken(TokenType.STRING, buffer.toString());
                        break;
                    case '-':
                        addToken(TokenType.MINUS);
                        next();
                        break;
                    case '/':
                        addToken(TokenType.DIVISION);
                        next();
                        break;
                    case '=':
                        addToken(TokenType.EQ);
                        next();
                        break;
                    case '*':
                        if (peek(1) == '=') {
                            addToken(TokenType.MUL_EQ);
                            next();
                        } else {
                            addToken(TokenType.MUL);
                        }
                        next();
                        break;
                    case ',':
                        addToken(TokenType.COMMA);
                        next();
                        break;
                    case '#':
                        char tempCom = next();
                        while (tempCom != '\n') {
                            tempCom = next();
                        }
                        break;
                    default:
                        throw new SyntaxException(String.format("Рядок %d : невідомий символ '%c'. Компіляцію зупинено.", line, current));
                }
            }
        }
        return tokens;
    }

    private void tokenizeWord() {
        final StringBuffer buffer = new StringBuffer();
        char current = peek(0);
        while (Character.isLetterOrDigit(current)) {
            buffer.append(current);
            current = next();
        }
        Token token = (Token) reserved.get(buffer.toString());
        if (token != null) {
            token.setLine(line);
            addToken(token);
        }
        else addToken(TokenType.ID, buffer.toString());
    }

    private void tokenizeNumber() {
        boolean isReal = false;
        boolean isBin = false;
        final StringBuffer buffer = new StringBuffer();
        char current = peek(0);
        while (Character.isDigit(current)) {
            buffer.append(current);
            current = next();
        }
        if (current == '.') {
            isReal = true;
            buffer.append(current);
            current = next();
            while (Character.isDigit(current)) {
                buffer.append(current);
                current = next();
            }
        }
        if (current == 'b') {
            isBin = true;
            buffer.deleteCharAt(0);
            current = next();
            while (Character.isDigit(current)) {
                buffer.append(current);
                current = next();
            }
        }
        if (Character.isLetter(current)) {
            throw new SyntaxException(String.format("Рядок %d : ім'я ідентифікатора не може починатися з цифри. Компіляцію зупинено.", line));
        }

        if (isReal) {
            addToken(TokenType.REAL, buffer.toString());
        } else if (isBin) {
            addToken(TokenType.BIN_NUMBER, buffer.toString());
        } else {
            addToken(TokenType.NUM, buffer.toString());
        }
    }

    private char next() {
        pos++;
        return peek(0);
    }

    private char peek(int relativePosition) {
        final int position = pos + relativePosition;
        if(position >= length) {
            return '\0';
        }
        return input.charAt(position);
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(type, "", line));
    }

    private void addToken(TokenType type, String text) {
        tokens.add(new Token(type, text, line));
    }

    private void addToken(Token token) {
        tokens.add(token);
    }
}