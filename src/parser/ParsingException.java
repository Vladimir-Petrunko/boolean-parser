package parser;

public class ParsingException extends RuntimeException {
    public ParsingException(int index, String message) {
        super("index " + index + ": " + message);
    }
}
