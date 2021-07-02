package parser;

import expression.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Stack;

public class ExpressionParser extends BaseParser implements Parser {
    public ExpressionParser() {
        super();
    }

    private Expression apply(Expression first, Character operator, Expression second) {
        if (operator == '&') {
            return new And(first, second);
        } else if (operator == '|') {
            return new Or(first, second);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private Variable parseVariable() {
        StringBuilder builder = new StringBuilder();
        while (isDigit() || isLetter()) {
            char ch = getCurrentChar();
            builder.append(ch);
            nextChar();
        }
        return new Variable(builder.toString());
    }

    private Literal parseLiteral() {
        if (test('1')) {
            nextChar();
            return Literal.TRUE;
        } else {
            nextChar();
            return Literal.FALSE;
        }
    }

    private char parseOperator() {
        skipWhitespace();
        char op = getCurrentChar();
        nextChar();
        return op;
    }

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
