package parser.exception;

public class BracketImbalanceException extends ParsingException {
    public BracketImbalanceException(int index, String message) {
        super(index, message);
    }
}
