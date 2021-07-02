package parser;

/**
 * Basic parser that can read data from a {@code CharSource} equipped with several useful methods.
 */

public class BaseParser {
    public final static char END = '\0';
    private CharSource source = null;
    private char curr;

    public BaseParser() {

    }

    /**
     * Sets a {@code CharSource} as the source from which data is to be read. If a source has already been set prior to
     * this method being called, the new source will replace it.
     *
     * @param source the new {@code CharSource} to be used for reading data
     */
    protected void setSource(final CharSource source) {
        this.source = source;
        nextChar();
    }

    /**
     * Returns the position of the current character in the source (numbering from 1).
     *
     * @return the index of the current character in source.
     */
    protected int getPosition() {
        return source.getPosition();
    }

    /**
     * Reads the next symbol from source and stores it in the one-symbol buffer.
     */
    protected void nextChar() {
        curr = source.hasNext() ? source.next() : END;
    }

    /**
     * Returns the last read character so far.
     *
     * @return the last read character in the source
     */
    protected char getCurrentChar() {
        return curr;
    }

    /**
     * Returns whether the current character in buffer is a whitespace symbol (space, tab, carriage return, newline).
     *
     * @return {@code true} if the current character is a whitespace symbol, {@code false} otherwise
     */
    protected boolean isCurrWhitespace() {
        return curr == ' ' || curr == '\t' || curr == '\r' || curr == '\n';
    }

    /**
     * Skips all whitespace symbols up to the nearest non-whitespace character. If no such character exists, reads up to
     * end of file. After the execution of this method, the current character is either EOF or a non-whitespace
     * character.
     */
    protected void skipWhitespace() {
        while (isCurrWhitespace()) {
            nextChar();
        }
    }

    /**
     * Determines whether the current character is equal to a character passed as argument.
     *
     * @param ch the character with which the current character should be compared
     * @return {@code true} if the current character and the argument are equal, {@code false} otherwise
     */
    protected boolean test(char ch) {
        return curr == ch;
    }

    /**
     * Determines whether the current character is equal to a character passed as argument. If it is equal, the method
     * terminates normally, otherwise, it throws an exception.
     *
     * @param ch the character that is expected to be the current one
     * @throws AssertionError if the current character and the argument are not equal
     */
    protected void expect(char ch) throws AssertionError {
        if (!test(ch)) {
            throw new AssertionError("Error during parsing: expected " + ch + ", found " + curr + ".");
        }
        nextChar();
    }

    /**
     * Determines if the next {@code |s|} characters in source form a given string.
     *
     * @param s an ordered string of characters that are to expected to appear in source
     * @throws AssertionError if the next {@code |s|} characters in source do not form argument
     */
    protected void expect(String s) throws AssertionError {
        for (char c : s.toCharArray()) {
            expect(c);
        }
    }

    /**
     * Determines whether current character is a digit from 0 to 9.
     *
     * @return {@code true} if the current character is a digit, {@code false} otherwise
     */
    protected boolean isDigit() {
        return curr >= '0' && curr <= '9';
    }

    /**
     * Determines whether current character is a letter A-Z, a-z.
     *
     * @return {@code true} if the current character is a letter, {@code false} otherwise
     */
    protected boolean isLetter() {
        return ((curr >= 'A' && curr <= 'Z') || (curr >= 'a' && curr <= 'z'));
    }

    /**
     * Determines whether there are any more characters in source
     *
     * @return {@code true} if end of file is reached, {@code false} otherwise.
     */
    protected boolean eof() {
        return curr == END;
    }
}
