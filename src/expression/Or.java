package expression;

import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private static Expression conjunctiveTwo(Expression left, Expression right) {
        left = left.conjunctiveNormalForm();
        right = right.conjunctiveNormalForm();
        if (left instanceof Literal) {
            return left.equals(Literal.FALSE) ? right : Literal.TRUE;
        } else if (right instanceof Literal) {
            return right.equals(Literal.FALSE) ? left : Literal.TRUE;
        } else if (left instanceof Variable && right instanceof Variable) {
            return new And(left, right);
        } else {
            if (left instanceof Variable) left = new And(left, Literal.TRUE);
            if (right instanceof Variable) right = new And(right, Literal.TRUE);
            And a = (And) left;
            And b = (And) right;
            Expression[] newArgs = new Expression[a.argCount() * b.argCount()];
            int current = 0;
            for (int i = 0; i < a.argCount(); i++) {
                for (int j = 0; j < b.argCount(); j++) {
                    if (a.get(i).equals(Literal.FALSE)) {
                        newArgs[current++] = b.get(j);
                    } else if (b.get(j).equals(Literal.FALSE)) {
                        newArgs[current++] = a.get(i);
                    } else {
                        Or or = new Or(a.get(i), b.get(j));
                        newArgs[current++] = new Or(Utils.expandOr(or.getArgs()));
                    }
                }
            }
            return new And(newArgs);
        }
    }

    @Override
    public Expression canonical() {
        return this;
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
        args = Utils.removeDuplicates(args, Literal.FALSE);
        return args.length == 1 ? args[0] : new Or(args);
    }
}