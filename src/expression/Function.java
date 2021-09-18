package expression;

import java.util.*;

public class Function extends Operation {
    protected final boolean[] truthTable;
    protected final Expression[] args;
    protected final String operator;

    public Function(boolean[] truthTable, String operator, Expression... args) {
        this.truthTable = truthTable;
        this.operator = operator;
        this.args = args;
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
    public boolean evaluate(Map<String, Boolean> map) {
        boolean[] result = new boolean[args.length];
        for (int i = 0; i < args.length; i++) {
            result[i] = args[i].evaluate(map);
        }
        return evaluate(result);
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
        builder.append(operator);
        builder.append('(');
        for (int i = 0; i < args.length; i++) {
            builder.append(args[i]);
            if (i != args.length - 1) {
                builder.append(", ");
            }
        }
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

    @Override
    public Expression conjunctiveNormalForm() {
        return fullConjunctiveNormalForm();
    }

    @Override
    public Expression disjunctiveNormalForm() {
        return fullDisjunctiveNormalForm();
    }
}
