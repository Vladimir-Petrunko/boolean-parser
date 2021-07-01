package expression;

import java.util.Map;
import java.util.Arrays;

public abstract class BinaryOperation extends Operation {
    protected final Expression[] args;
    protected final String operator;

    public BinaryOperation(String operator, Expression... args) {
        this.args = args;
        this.operator = operator;
    }

    protected abstract boolean apply(boolean... args);

    public Expression[] getArgs() {
        return args;
    }

    public int argCount() {
        return args.length;
    }

    public Expression get(int index) {
        return args[index];
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> map) {
        boolean[] result = new boolean[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = args[i].evaluate(map);
        }
        return apply(result);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            if (i != args.length - 1) {
                builder.append(' ');
                builder.append(operator);
                builder.append(' ');
            }
        }
        builder.append(')');
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof BinaryOperation) {
            BinaryOperation op = (BinaryOperation) obj;
            if (!operator.equals(op.operator) || args.length != op.args.length) {
                return false;
            }
            return Arrays.equals(args, op.args);
        } else {
            return false;
        }
    }
}
