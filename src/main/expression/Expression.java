package expression;

import utils.Category;
import utils.Utils;

import java.util.*;
import java.util.stream.Stream;

public abstract class Expression implements Simplifiable {
    /**
     * Whether this expression was entirely enclosed in parentheses during parsing (and thus will not be simplified).
     * For example, <code>(x & y) & z</code> will not be simplified to <code>x & y & z</code> if <code>x & y</code>
     * is <code>fixed</code>.
     */
    private boolean fixed = false;

    public Expression fix() {
        fixed = true;
        return this;
    }

    public boolean isFixed() {
        return fixed;
    }

    /**
     * Evaluates a boolean expression.
     *
     * @param map a map of bindings between variables and their values
     * @return the value of this expression
     */
    public abstract boolean evaluate(Map<String, Boolean> map);

    /**
     * Evaluates a boolean expression.
     *
     * @param argList a list of arguments (in lexicographical order of variables)
     * @return the value of this expression
     */
    public boolean evaluate(boolean[] argList) {
        String[] varList = getVariableList();
        if (argList.length != varList.length) {
            throw new IllegalArgumentException("length of argList (" + argList.length +
                    ") does not match variable count (" + varList.length + ").");
        }
        Map<String, Boolean> map = new HashMap<>();
        for (int i = 0; i < argList.length; i++) {
            map.put(varList[i], argList[i]);
        }
        return evaluate(map);
    }

    public boolean evaluate(boolean arg1) {
        return evaluate(new boolean[]{arg1});
    }

    public boolean evaluate(boolean arg1, boolean arg2) {
        return evaluate(new boolean[]{arg1, arg2});
    }

    public boolean evaluate(boolean arg1, boolean arg2, boolean arg3) {
        return evaluate(new boolean[]{arg1, arg2, arg3});
    }

    public boolean evaluate(boolean arg1, boolean arg2, boolean arg3, boolean arg4) {
        return evaluate(new boolean[]{arg1, arg2, arg3, arg4});
    }

    public boolean evaluate(boolean arg1, boolean arg2, boolean arg3, boolean arg4, boolean arg5) {
        return evaluate(new boolean[]{arg1, arg2, arg3, arg4, arg5});
    }

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    /**
     * Returns the arity (i.e. the number of arguments) of this expression. If it is a literal, then the number of
     * arguments is considered to be 0.
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
    public Expression getArg(int index) {
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
     * Determines whether two expressions are equivalent. That is, on the same set of arguments, both expressions
     * should evaluate to the same value.<br><br>
     *
     * @param other a {@code Expression}
     * @return {@code true} if this expression is equivalent to {@code other}, or {@code false} otherwise
     */
    public boolean isEquivalentTo(Expression other) {
        Object[] variables = Stream.concat(Arrays.stream(getVariableList()), Arrays.stream(other.getVariableList())).toArray();
        Object[] uniqueVariables = Utils.removeDuplicates(variables);
        Boolean[] values = new Boolean[uniqueVariables.length];
        for (int i = 0; i < (1 << uniqueVariables.length); i++) {
            for (int k = 0; k < uniqueVariables.length; k++) {
                values[k] = (i & (1 << k)) != 0;
            }
            Map<String, Boolean> argList = new HashMap<>();
            for (int k = 0; k < uniqueVariables.length; k++) {
                argList.put((String) uniqueVariables[k], values[k]);
            }
            if (evaluate(argList) != other.evaluate(argList)) {
                return false;
            }
        }
        return true;
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
                args.add(arr.length == 1 ? arr[0] : new And(arr));
            }
        }
        if (args.isEmpty()) {
            args.add(Literal.FALSE);
        }
        Expression[] arr = new Expression[args.size()];
        args.toArray(arr);
        return arr.length == 1 ? arr[0] : new Or(arr);
    }

    @Override
    public Expression zhegalkinPolynomial() {
        boolean[] table = truthTable();
        boolean[] coeff = new boolean[table.length];
        String[] variables = getVariableList();
        List<Expression> args = new ArrayList<>();
        for (int i = 0; i < table.length; i++) {
            boolean tot = false;
            for (int j = 0; j < i; j++) {
                if ((j & i) == j) {
                    tot ^= coeff[j];
                }
            }
            coeff[i] = tot ^ table[i];
            if (coeff[i]) {
                List<Expression> list = new ArrayList<>();
                for (int k = 0; k < variables.length; k++) {
                    if ((i & (1 << k)) != 0) {
                        list.add(new Variable(variables[k]));
                    }
                }
                if (list.isEmpty()) {
                    list.add(Literal.TRUE);
                }
                Expression[] arr = new Expression[list.size()];
                list.toArray(arr);
                args.add(arr.length == 1 ? arr[0] : new And(arr));
            }
        }
        if (args.isEmpty()) {
            args.add(Literal.FALSE);
        }
        Expression[] arr = new Expression[args.size()];
        args.toArray(arr);
        return arr.length == 1 ? arr[0] : new Xor(arr);
    }

    /**
     * Determines whether this operation satisfies one of the 5 Post completeness categories:<br>
     * <ul>
     *     <li>True-preserving</li>
     *     <li>False-preserving</li>
     *     <li>Monotonic</li>
     *     <li>Linear</li>
     *     <li>Self-dual</li>
     * </ul>
     *
     * @param category the Post completeness category that is to be tested.
     * @return {@code true} if this operation satisfies the {@code category}, {@code false} otherwise.
     */
    public boolean satisfies(Category category) {
        return switch (category) {
            case TRUE_PRESERVING -> preservesTrue();
            case FALSE_PRESERVING -> preservesFalse();
            case MONOTONIC -> monotonic();
            case LINEAR -> linear();
            default -> selfDual();
        };
    }

    /**
     * @return {@code true} if this operation is a true-preserving function, {@code false} otherwise.
     */
    public boolean preservesTrue() {
        boolean[] args = new boolean[argCount()];
        Arrays.fill(args, true);
        return evaluate(args);
    }

    /**
     * @return {@code true} if this operation is a false-preserving function, {@code false} otherwise.
     */
    public boolean preservesFalse() {
        boolean[] args = new boolean[argCount()];
        return !evaluate(args);
    }

    /**
     * @return {@code true} if this operation is a monotonic function, {@code false} otherwise.
     */
    public boolean monotonic() {
        boolean[] table = truthTable();
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < argCount(); j++) {
                int t;
                if ((i & (1 << j)) == 0) {
                    t = i + (1 << j);
                } else {
                    t = i - (1 << j);
                }
                if (table[Math.min(i, t)] && !table[Math.max(i, t)]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * @return {@code true} if this operation is a linear function, {@code false} otherwise.
     */
    public boolean linear() {
        Expression zh = zhegalkinPolynomial();
        if (zh instanceof And) {
            return false;
        }
        int cnt = zh.argCount();
        for (int i = 0; i < cnt; i++) {
            Expression exp = zh.getArg(i);
            if (exp instanceof And) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return {@code true} if this operation is a self-dual function, {@code false} otherwise.
     */
    public boolean selfDual() {
        boolean[] table = truthTable();
        for (int i = 0, j = table.length - 1; i <= j; i++, j--) {
            if (table[i] == table[j]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if a given set of expressions forms a functional basis (by using Post's completeness theorem).
     *
     * @param arr an array of {@code Expression}s
     * @return {@code true} if the given set of expressions represents a basis among the logical functions, {@code
     *         false} otherwise.
     */
    public static boolean isFunctionalBasis(Expression[] arr) {
        // Boolean variables that represent whether all of the functions met so far belong to a given category
        boolean truePreserving = true;
        boolean falsePreserving = true;
        boolean monotonic = true;
        boolean linear = true;
        boolean selfDual = true;
        for (Expression exp : arr) {
            truePreserving &= exp.preservesTrue();
            falsePreserving &= exp.preservesFalse();
            monotonic &= exp.monotonic();
            linear &= exp.linear();
            selfDual &= exp.selfDual();
        }
        return !truePreserving && !falsePreserving && !monotonic && !linear && !selfDual;
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