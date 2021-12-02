package lang.token;

public class IdentifierToken extends Token {
    private final String identifier;

    public IdentifierToken(String identifier, TokenInfo info) {
        super(info);
        this.identifier = identifier;
    }

    public String identifier() {
        return identifier;
    }
}
