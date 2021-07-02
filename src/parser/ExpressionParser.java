package parser;

import expression.Expression;
import expression.And;
import expression.Or;
import expression.Not;
import expression.Variable;
import expression.Literal;

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
    private Expression apply(Expression first, Character operator, Expression second) {
        if (operator == '&') {
            return new And(first, second);
        } else if (operator == '|') {
            return new Or(first, second);
        } else {
            throw new UnsupportedOperationException("unrecognized operator " + operator);
        }
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
     */
    private Literal parseLiteral() {
        if (test('1')) {
            nextChar();
            return Literal.TRUE;
        } else {
            nextChar();
            return Literal.FALSE;
        }
    }

    /**
     * Parses an operator symbol starting from the current position and returns it.
     *
     * @return the next operator symbol in the source
     */
    private char parseOperator() {
        skipWhitespace();
        char op = getCurrentChar();
        nextChar();
        return op;
    }

    /**
     * Parses an operand starting from the current position and returns it.
     *
     * @return the next operand in the source
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
        } else {
            return parseVariable();
        }
    }

    /**
     * Parses an expression starting from the source and returns it.
     *
     * @return the next expression in the source
     */
    private Expression parseExpression() {
        Stack<Expression> operands = new Stack<>();
        Stack<Character> operators = new Stack<>();
        operands.push(parseOperand());
        while (!eof()) {
            skipWhitespace();
            if (test(')')) {
                nextChar();
                break;
            }
            char op = parseOperator();
            while (!operators.empty() && operators.peek() == '&' && op == '|') {
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
