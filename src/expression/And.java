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
            if (left instanceof Variable) left = new Or(left, Literal.FALSE);
            if (right instanceof Variable) right = new Or(right, Literal.FALSE);
            Or a = (Or) left;
            Or b = (Or) right;
            Expression[] newArgs = new Expression[a.argCount() * b.argCount()];
            int current = 0;
            for (int i = 0; i < a.argCount(); i++) {
                for (int j = 0; j < b.argCount(); j++) {
                    if (a.get(i).equals(Literal.FALSE)) {
                        newArgs[current++] = b.get(j);
                    } else if (b.get(j).equals(Literal.FALSE)) {
                        newArgs[current++] = a.get(i);
                    } else {
                        And and = new And(a.get(i), b.get(j));
                        newArgs[current++] = new And(Utils.expandAnd(and.getArgs()));
                    }
                }
            }
            return new Or(newArgs);
        }
    }

    @Override
    public Expression canonical() {
        return this;
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