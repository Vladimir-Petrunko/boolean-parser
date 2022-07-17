package parser.exception;

public class UnrecognizedOperatorException extends ParsingException {
    public UnrecognizedOperatorException(int index, String message) {
        super(index, message);
    }
}
