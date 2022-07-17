package parser;

import expression.Expression;

public interface Parser {
    /**
     * Parses a string into an {@code Expression}
     *
     * @param expression the string representation of an expression
     * @return the parsed expression
     */
    Expression parse(String expression);
}
