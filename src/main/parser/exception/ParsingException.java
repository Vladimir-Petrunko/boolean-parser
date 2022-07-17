package parser.exception;

public class ParsingException extends RuntimeException {
    public ParsingException(int index, String message) {
        super("index " + index + ": " + message);
    }
}
