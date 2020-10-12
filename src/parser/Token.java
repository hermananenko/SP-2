package parser;

public class Token {
    private final TokenType type;
    private final String text;
    private int line;

    public Token(TokenType type, String text, int line) {
        this.type = type;
        this.text = text;
        this.line = line;
    }

    public Token(TokenType type, String text) {
        this.type = type;
        this.text = text;
    }

    public TokenType getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "type: " + type + " | " + "lexeme: " + text;
    }
}
