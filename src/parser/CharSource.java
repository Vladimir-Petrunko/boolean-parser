package parser;

public interface CharSource {
    /**
     * @return {@code true} if there are unread symbols in this source, {@code false} otherwise
     */
    boolean hasNext();

    /**
     * @return the first unread symbol in this source, or {@code EOF} if the end of source is reached
     */
    char next();

    /**
     * @return returns the current position of the parser in the source
     */
    int getPosition();
}