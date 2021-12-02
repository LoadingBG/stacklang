package lang.token;

public class TokenInfo {
    private String raw;
    private String filename;
    private int row;
    private int col;
    private String line;

    public TokenInfo(String raw, String filename, int row, int col, String line) {
        this.raw = raw;
        this.filename = filename;
        this.row = row;
        this.col = col;
        this.line = line;
    }
    
    public String raw() {
        return raw;
    }

    public String filename() {
        return filename;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    public String line() {
        return line;
    }
}
