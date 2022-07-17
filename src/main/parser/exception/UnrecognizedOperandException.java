package parser.exception;

public class UnrecognizedOperandException extends ParsingException {
    public UnrecognizedOperandException(int index, String message) {
        super(index, message);
    }
}
