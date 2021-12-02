package lang.token;

public class NumberToken extends Token {
    private double number;

    public NumberToken(double number, TokenInfo info) {
        super(info);
        this.number = number;
    }

    public double number() {
        return number;
    }
}
