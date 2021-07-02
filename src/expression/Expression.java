package expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

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
     * number of arguments is said to be equal to 1.
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
     * Returns the truth table of this expression.<br><br>
     *
     * In this truth table, the {@code i}-th element denotes the following. Suppose we convert {@code i} to binary.
     * Then the variables corresponding to the "on" bits in {@code i} are set to {@code true}, the other variables -
     * to {@code false}. The corresponding entry contains the value of this expression with this binding.
     *
     * @param variables the list of variables in some order
     * @return the truth table of this expression, as described above
     */
    public boolean[] truthTable(String[] variables) {
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
     * Note that all the variables (both in this expression and in the expression being tested) should occur in the
     * variable list passed as a parameter.
     *
     * @param other a {@code Expression}
     * @param variables the list of variables in some order
     * @return {@code true} if this expression has the same truth table as {@code other}, or {@code false} otherwise
     */
    public boolean isIdenticalTo(Expression other, String[] variables) {
        boolean[] thisTable = truthTable(variables);
        boolean[] otherTable = other.truthTable(variables);
        return Arrays.equals(thisTable, otherTable);
    }
}