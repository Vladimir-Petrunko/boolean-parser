package parser;

import expression.*;

import java.util.Stack;

public class ExpressionParser extends BaseParser implements Parser {
    public ExpressionParser() {
        super();
    }

    /**
     * Returns an expression obtained by applying a binary operator to two arguments.
     *
     * @param first the first argument
     * @param operator the symbol of the operator to be applied
     * @param second the second argument
     * @return the resulting expression
     */
    private Expression apply(Expression first, String operator, Expression second) {
        return switch (operator) {
            case "&" -> new And(first, second);
            case "|" -> new Or(first, second);
            case "^" -> new Xor(first, second);
            default -> throw new UnsupportedOperationException("unrecognized operator " + operator);
        };
    }

    /**
     * Parses a variable name starting from the current position and returns a created {@code Variable} with this name
     *
     * @return the next variable in the source, as described above
     */
    private Variable parseVariable() {
        StringBuilder builder = new StringBuilder();
        while (isDigit() || isLetter()) {
            char ch = getCurrentChar();
            builder.append(ch);
            nextChar();
        }
        return new Variable(builder.toString());
    }

    /**
     * Parses a literal value starting from the current position and returns a created {@code Literal} with this value
     *
     * @return the next literal in the source, as described above
     * @throws ParsingException if unable to parse a valid literal
     */
    private Literal parseLiteral() {
        if (test('0') || test('1')) {
            int value = getCurrentChar() - '0';
            nextChar();
            return new Literal(value);
        } else {
            throw new ParsingException(getPosition(), "unrecognized literal");
        }
    }

    /**
     * Parses an operator symbol starting from the current position and returns it.
     *
     * @return the next operator symbol in the source
     * @throws ParsingException if unable to parse a valid operator
     */
    private String parseOperator() {
        skipWhitespace();
        if (test('&') || test('^') || test('|')) {
            char op = getCurrentChar();
            nextChar();
            return op + "";
        } else {
            throw new ParsingException(getPosition(), "unrecognized operator");
        }
    }

    /**
     * Parses an operand starting from the current position and returns it.
     *
     * @return the next operand in the source
     * @throws ParsingException if unable to parse a valid operand
     */
    private Expression parseOperand() {
        skipWhitespace();
        if (test('0') || test('1')) {
            return parseLiteral();
        } else if (test('(')) {
            nextChar();
            return parseExpression();
        } else if (test('~')) {
            nextChar();
            return new Not(parseOperand());
        } else if (isLetter()) {
            return parseVariable();
        } else {
            throw new ParsingException(getPosition(), "unable to parse operand");
        }
    }

    /**
     * Parses an expression starting from the source and returns it.
     *
     * @return the next expression in the source
     * @throws ParsingException if unable to parse a valid expression
     */
    private Expression parseExpression() {
        Stack<Expression> operands = new Stack<>();
        Stack<String> operators = new Stack<>();
        operands.push(parseOperand());
        while (!eof()) {
            skipWhitespace();
            if (test(')')) {
                nextChar();
                break;
            }
            String op = parseOperator();
            int currentPriority = PriorityManager.getPriority(op);
            while (!operators.empty() && PriorityManager.getPriority(operators.peek()) > currentPriority) {
                Expression second = operands.pop();
                Expression first = operands.pop();
                operands.push(apply(first, operators.pop(), second));
            }
            operators.push(op);
            operands.push(parseOperand());
        }
        while (!operators.empty()) {
            Expression second = operands.pop();
            Expression first = operands.pop();
            operands.push(apply(first, operators.pop(), second));
        }
        return operands.peek();
    }

    @Override
    public Expression parse(String expression) {
        setSource(new StringSource(expression));
        return parseExpression();
    }

}
