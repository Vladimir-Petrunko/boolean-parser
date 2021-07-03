package expression;

import utils.Utils;

public class And extends BinaryOperation {
    public And(Expression... args) {
        super("&", args);
    }

    @Override
    public boolean apply(boolean... args) {
        for (boolean arg : args) {
            if (!arg) {
                return false;
            }
        }
        return true;
    }

    /**
     * Private method that converts an expression of the type {@code (... & ...)} to DNF, where the left-hand side is
     * already converted to DNF.
     *
     * @param left the left side of the AND clause, already converted to DNF
     * @param right the right side of the AND clause
     * @return the resulting DNF of the expression {@code (left & right)}
     * @see expression.Or#conjunctiveTwo(Expression, Expression)
     */
    private static Expression disjunctiveTwo(Expression left, Expression right) {
        right = right.disjunctiveNormalForm();
        if (left instanceof Literal) {
            return left.equals(Literal.TRUE) ? right : Literal.FALSE;
        } else if (right instanceof Literal) {
            return right.equals(Literal.TRUE) ? left : Literal.FALSE;
        } else {
            Expression[] newArgs = new Expression[left.argCount() * right.argCount()];
            if (left instanceof And || left instanceof Not) left = new Or(left);
            if (right instanceof And || right instanceof Not) right = new Or(right);
            // Expand parentheses
            for (int i = 0; i < left.argCount(); i++) {
                for (int j = 0; j < right.argCount(); j++) {
                    newArgs[i * right.argCount() + j] = new And(left.get(i), right.get(j));
                }
            }
            return newArgs.length == 1 ? newArgs[0] : new Or(newArgs);
        }
    }

    @Override
    public Expression conjunctiveNormalForm() {
        Expression[] args = Utils.expandAnd(this.args);
        args = Utils.removeDuplicates(args);
        return args.length == 1 ? args[0] : new And(args);
    }

    @Override
    public Expression disjunctiveNormalForm() {
        Expression conj = conjunctiveNormalForm();
        if (conj instanceof And) {
            And and = (And) conj;
            Expression current = And.disjunctiveTwo(and.get(0), and.get(1));
            for (int i = 2; i < and.argCount(); i++) {
                current = And.disjunctiveTwo(current, and.get(i));
            }
            return current;
        } else {
            return conj;
        }
    }
}