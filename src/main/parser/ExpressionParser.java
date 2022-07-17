package parser;

import expression.*;
import parser.exception.BracketImbalanceException;
import parser.exception.ParsingException;
import parser.exception.UnrecognizedOperandException;
import parser.exception.UnrecognizedOperatorException;

import java.util.Arrays;
import java.util.Stack;
import java.util.stream.Stream;

public class ExpressionParser extends BaseParser implements Parser {
    private int bracketBalance = 0;

    public ExpressionParser() {
        super();
    }

    /**
     * Returns an expression obtained by applying a binary operator to two operands.
     *
     * @param first the first operand
     * @param operator the symbol of the operator to be applied
     * @param second the second operand
     * @return the resulting expression
     */
    private Expression apply(Expression first, String operator, Expression second) {
        String leftOperator = Operation.class.isAssignableFrom(first.getClass()) ? ((Operation) first).getOperator() : null;
        String rightOperator = Operation.class.isAssignableFrom(second.getClass()) ? ((Operation) second).getOperator() : null;
        Stream<Expression> leftStream = (operator.equals(leftOperator) && !first.isFixed()) ? Arrays.stream(first.getArgs()) : Stream.of(first);
        Stream<Expression> rightStream = (operator.equals(rightOperator) && !second.isFixed()) ? Arrays.stream(second.getArgs()) : Stream.of(second);
        Expression[] argList = Stream.concat(leftStream, rightStream).toArray(Expression[]::new);
        return switch (operator) {
            case "&" -> new And(argList);
            case "|" -> new Or(argList);
            case "^" -> new Xor(argList);
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
            throw new UnrecognizedOperatorException(getPosition(), "unrecognized operator starting with " + getCurrentChar());
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
            bracketBalance++;
            nextChar();
            return parseExpression().fix();
        } else if (test('~')) {
            nextChar();
            return new Not(parseOperand());
        } else if (isLetter()) {
            return parseVariable();
        } else {
            throw new UnrecognizedOperandException(getPosition(), "unable to parse operand starting with " + getCurrentChar());
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
                bracketBalance--;
                if (bracketBalance < 0) {
                    throw new BracketImbalanceException(getPosition(), "bracket balance < 0");
                }
                nextChar();
                break;
            }
            if (eof()) {
                break;
            }
            String op = parseOperator();
            int currentPriority = PriorityManager.getPriority(op);
            while (!operators.empty() && PriorityManager.getPriority(operators.peek()) >= currentPriority) {
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
        bracketBalance = 0;
        Expression result = parseExpression();
        if (bracketBalance != 0) {
            throw new BracketImbalanceException(getPosition(), "total bracket balance != 0");
        }
        return result;
    }

}
