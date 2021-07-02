package expression;

import utils.Utils;

public class Or extends BinaryOperation {
    public Or(Expression... args) {
        super("|", args);
    }

    @Override
    public boolean apply(boolean... args) {
        for (boolean arg : args) {
            if (arg) {
                return true;
            }
        }
        return false;
    }

    /**
     * Private method that converts an expression of the type {@code (... | ...)} to CNF, where the left-hand side is
     * already converted to CNF.
     *
     * @param left the left side of the OR clause, already converted to CNF
     * @param right the right side of the OR clause
     * @return the resulting CNF of the expression {@code (left | right)}
     * @see expression.And#disjunctiveTwo(Expression, Expression)
     */
    private static Expression conjunctiveTwo(Expression left, Expression right) {
        right = right.conjunctiveNormalForm();
        if (left instanceof Literal) {
            return left.equals(Literal.FALSE) ? right : Literal.TRUE;
        } else if (right instanceof Literal) {
            return right.equals(Literal.FALSE) ? left : Literal.TRUE;
        } else if (left.argCount() == 1 && right.argCount() == 1) {
            return new Or(left, right);
        } else {
            int lc = left instanceof Or ? 1 : left.argCount();
            int rc = right instanceof Or ? 1 : right.argCount();
            Expression[] newArgs = new Expression[lc * rc];
            if (left instanceof Or || left instanceof Not) left = new And(left, Literal.TRUE);
            if (right instanceof Or || right instanceof Not) right = new And(right, Literal.TRUE);
            int current = 0;
            for (int i = 0; i < left.argCount(); i++) {
                for (int j = 0; j < right.argCount(); j++) {
                    Or or = new Or(left.get(i), right.get(j));
                    Expression exp = new Or(Utils.removeDuplicates(Utils.expandOr(or.getArgs())));
                    if (!exp.get(0).equals(Literal.TRUE)) {
                        newArgs[current++] = exp;
                    }
                }
            }
            return newArgs.length == 1 ? newArgs[0] : new And(newArgs);
        }
    }

    @Override
    public Expression conjunctiveNormalForm() {
        Expression dis = disjunctiveNormalForm();
        if (dis instanceof Or) {
            Or or = (Or) dis;
            Expression current = conjunctiveTwo(or.get(0), or.get(1));
            for (int i = 2; i < or.argCount(); i++) {
                current = conjunctiveTwo(current, or.get(i));
            }
            return current;
        } else {
            return dis;
        }
    }

    @Override
    public Expression disjunctiveNormalForm() {
        Expression[] args = Utils.expandOr(this.args);
        args = Utils.removeDuplicates(args);
        return args.length == 1 ? args[0] : new Or(args);
    }
}