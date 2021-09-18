package expression;

import utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Or extends BinaryOperation {
    public Or(Expression... args) {
        super("|", args);
    }

    private static Expression[] merge(Expression... args) {
        List<Expression> list = new ArrayList<>();
        for (Expression arg : args) {
            arg = arg.disjunctiveNormalForm();
            if (arg instanceof Or) {
                Collections.addAll(list, Or.merge(arg.getArgs()));
            } else if (arg.equals(Literal.TRUE)) {
                list.clear();
                list.add(Literal.TRUE);
                break;
            } else {
                list.add(arg);
            }
        }
        Expression[] arr = new Expression[list.size()];
        list.toArray(arr);
        arr = Utils.removeDuplicates(arr, Or.class);
        return arr;
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
        } else {
            Expression[] leftArgs = (left instanceof And) ? left.getArgs() : new Expression[]{left};
            Expression[] rightArgs = (right instanceof And) ? right.getArgs() : new Expression[]{right};
            Expression[] newArgs = new Expression[leftArgs.length * rightArgs.length];
            // Expand parentheses
            for (int i = 0; i < leftArgs.length; i++) {
                for (int j = 0; j < rightArgs.length; j++) {
                    Expression[] arr = Or.merge(leftArgs[i], rightArgs[j]);
                    newArgs[i * rightArgs.length + j] = (arr.length == 1) ? arr[0] : new Or(arr);
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
        Expression[] args = merge(this.args);
        return args.length == 1 ? args[0] : new Or(args);
    }
}