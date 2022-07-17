package expression;

import java.util.*;

public abstract class Operation extends Expression {
    protected final Expression[] args;
    protected final String operator;

    /**
     * Constructor of {@code BinaryOperation}.
     *
     * @param operator the string representation of the operator
     * @param args the argument list
     */
    public Operation(String operator, Expression... args) {
        this.args = args;
        this.operator = operator;
    }

    /**
     * Returns the result of this operation when applied on an arbitrary-length argument list.
     *
     * @param args the argument list
     * @return the result of this operation when applied on {@code args}.
     */
    protected abstract boolean apply(Boolean... args);

    @Override
    public Expression[] getArgs() {
        return args;
    }

    @Override
    public int argCount() {
        return args.length;
    }

    public String getOperator() {
        return operator;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> map) {
        Boolean[] result = new Boolean[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = args[i].evaluate(map);
        }
        return apply(result);
    }

    @Override
    public String[] getVariableList() {
        Set<String> list = new HashSet<>();
        for (Expression expr : args) {
            Collections.addAll(list, expr.getVariableList());
        }
        String[] arr = new String[list.size()];
        list.toArray(arr);
        Arrays.sort(arr);
        return arr;
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
        if (obj instanceof Operation) {
            Operation op = (Operation) obj;
            if (!operator.equals(op.operator) || args.length != op.args.length) {
                return false;
            }
            return Arrays.equals(args, op.args);
        } else {
            return false;
        }
    }
}
