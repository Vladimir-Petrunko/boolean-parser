package expression;

import java.util.*;

public class Function extends Operation {
    protected final boolean[] truthTable;

    public Function(boolean[] truthTable, String operator, Expression... args) {
        super(operator, args);
        if ((1 << args.length) != truthTable.length) {
            throw new IllegalArgumentException(args.length + " args provided, wrong size of truth table: expected " +
                    (1 << args.length) + ", found " + truthTable.length);
        }
        this.truthTable = truthTable;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public Expression[] getArgs() {
        return args;
    }

    @Override
    public int argCount() {
        return args.length;
    }

    @Override
    protected boolean apply(Boolean[] args) {
        int index = 0;
        for (int i = 0; i < args.length; i++) {
            if (args[i]) {
                int k = args.length - i - 1;
                index += (1 << k);
            }
        }
        return truthTable[index];
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
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(operator);
        builder.append('(');
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            if (i != args.length - 1) {
                builder.append(", ");
            }
        }
        builder.append(')');
        return builder.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Function) {
            Function func = (Function) obj;
            if (!operator.equals(func.operator) || args.length != func.args.length || truthTable.length != func.truthTable.length) {
                return false;
            }
            return Arrays.equals(args, func.args) && Arrays.equals(truthTable, func.truthTable);
        } else {
            return false;
        }
    }
}
