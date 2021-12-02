package lang.token;

public abstract class Token {
    private TokenInfo info;

    protected Token(TokenInfo info) {
        this.info = info;
    }

    public TokenInfo info() {
        return info;
    }

    public static Token from(String word, TokenInfo info) {
        try {
            return new NumberToken(Double.parseDouble(word), info);
        } catch (NumberFormatException e) {
            switch (word) {
                case "do":
                    return new DoToken(info);
                case "end":
                    return new EndToken(info);
                case "if":
                    return new IfToken(info);
                case "else":
                    return new ElseToken(info);
                case "while":
                    return new WhileToken(info);
                default:
                    return new IdentifierToken(word, info);
            }
        }
    }
}
