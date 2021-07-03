package expression;

import java.util.*;

public abstract class Expression implements Simplifiable {
    /**
     * Evaluates a boolean expression.
     *
     * @param map a map of bindings between variables and their values
     * @return the value of this expression
     */
    public abstract boolean evaluate(Map<String, Boolean> map);

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    /**
     * Returns the arity (i.e. the number of arguments) of this expression. If it is a literal or variable, then the
     * number of arguments is considered to be equal to 1.
     *
     * @return the arity of this expression
     */
    public abstract int argCount();

    /**
     * Returns the list of arguments of this expression as an array.
     *
     * @return the list of arguments of this expression
     */
    public abstract Expression[] getArgs();

    /**
     * Returns the argument of this expression with a particular index.
     *
     * @param index the index of the argument that should be returned
     * @return the argument at that index
     */
    public Expression get(int index) {
        return getArgs()[index];
    }

    /**
     * Returns the list of lexicographically sorted variable names of this expression as an array.
     *
     * @return an array of variable names that occur in this expression, in lexicographical order
     */
    public abstract String[] getVariableList();

    /**
     * Returns the truth table of this expression.<br><br>
     *
     * In this truth table, the {@code i}-th element denotes the following. Suppose we convert {@code i} to binary and
     * we have a list of lexicographically sorted variable names that occur in this expression. Then the variables
     * located at the indices corresponding to the "on" bits in {@code i} are set to {@code true}, the other variables -
     * to {@code false}. The corresponding entry contains the value of this expression with these bindings.
     *
     * @return the truth table of this expression, as described above
     */
    public boolean[] truthTable() {
        String[] variables = getVariableList();
        boolean[] table = new boolean[1 << variables.length];
        for (int i = 0; i < table.length; i++) {
            Map<String, Boolean> map = new HashMap<>();
            for (int k = 0; k < variables.length; k++) {
                if ((i & (1 << k)) == 0) {
                    // The k-th bit is turned off
                    map.put(variables[k], false);
                } else {
                    // The k-th bit is turned on
                    map.put(variables[k], true);
                }
            }
            table[i] = evaluate(map);
        }
        return table;
    }

    /**
     * Determines whether two expressions have the same truth table.<br><br>
     *
     * @param other a {@code Expression}
     * @return {@code true} if this expression has the same truth table as {@code other}, or {@code false} otherwise
     */
    public boolean isEquivalentTo(Expression other) {
        boolean[] thisTable = truthTable();
        boolean[] otherTable = other.truthTable();
        return Arrays.equals(thisTable, otherTable);
    }

    @Override
    public Expression fullConjunctiveNormalForm() {
        String[] variables = getVariableList();
        if (variables.length == 0) {
            return new Literal(evaluate(new HashMap<>()));
        }
        boolean[] table = truthTable();
        List<Expression> args = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            if (!table[i]) {
                List<Expression> list = new ArrayList<>();
                for (int k = 0; k < variables.length; k++) {
                    if ((i & (1 << k)) == 0) {
                        // The k-th bit is turned off
                        list.add(new Variable(variables[k]));
                    } else {
                        // The k-th bit is turned on
                        list.add(new Not(new Variable(variables[k])));
                    }
                }
                Expression[] arr  = new Expression[list.size()];
                list.toArray(arr);
                if (arr.length == 1) {
                    args.add(arr[0]);
                } else {
                    args.add(new Or(arr));
                }
            }
        }
        Expression[] arr = new Expression[args.size()];
        args.toArray(arr);
        if (arr.length == 0) {
            return Literal.TRUE;
        } else if (arr.length == 1) {
            return arr[0];
        } else {
            return new And(arr);
        }
    }

    // TODO: maybe remove code duplication
    @Override
    public Expression fullDisjunctiveNormalForm() {
        String[] variables = getVariableList();
        if (variables.length == 0) {
            return new Literal(evaluate(new HashMap<>()));
        }
        boolean[] table = truthTable();
        List<Expression> args = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            if (table[i]) {
                List<Expression> list = new ArrayList<>();
                for (int k = 0; k < variables.length; k++) {
                    if ((i & (1 << k)) == 0) {
                        // The k-th bit is turned off
                        list.add(new Not(new Variable(variables[k])));
                    } else {
                        // The k-th bit is turned on
                        list.add(new Variable(variables[k]));
                    }
                }
                Expression[] arr  = new Expression[list.size()];
                list.toArray(arr);
                if (arr.length == 1) {
                    args.add(arr[0]);
                } else {
                    args.add(new And(arr));
                }
            }
        }
        Expression[] arr = new Expression[args.size()];
        args.toArray(arr);
        if (arr.length == 0) {
            return Literal.FALSE;
        } else if (arr.length == 1) {
            return arr[0];
        } else {
            return new Or(arr);
        }
    }
}