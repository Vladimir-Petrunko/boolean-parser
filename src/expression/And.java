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

    private static Expression disjunctiveTwo(Expression left, Expression right) {
        left = left.disjunctiveNormalForm();
        right = right.disjunctiveNormalForm();
        if (left instanceof Literal) {
            return left.equals(Literal.TRUE) ? right : Literal.FALSE;
        } else if (right instanceof Literal) {
            return right.equals(Literal.TRUE) ? left : Literal.FALSE;
        } else if (left instanceof Variable && right instanceof Variable) {
            return new And(left, right);
        } else {
            int lc = left instanceof And ? 1 : left.argCount();
            int rc = right instanceof And ? 1 : right.argCount();
            Expression[] newArgs = new Expression[lc * rc];
            if (left instanceof And) left = new Or(left, Literal.FALSE);
            if (right instanceof And) right = new Or(right, Literal.FALSE);
            int current = 0;
            for (int i = 0; i < left.argCount(); i++) {
                for (int j = 0; j < right.argCount(); j++) {
                    And and = new And(left.get(i), right.get(j));
                    Expression exp = new And(Utils.removeDuplicates(Utils.expandAnd(and.getArgs()), Literal.TRUE));
                    if (!exp.get(0).equals(Literal.FALSE)) {
                        newArgs[current++] = exp;
                    }
                }
            }
            return newArgs.length == 1 ? newArgs[0] : new Or(newArgs);
        }
    }

    @Override
    public Expression conjunctiveNormalForm() {
        Expression[] args = Utils.expandAnd(this.args);
        args = Utils.removeDuplicates(args, Literal.TRUE);
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