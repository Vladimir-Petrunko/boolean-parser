package expression;

import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class And extends Operation {
    public And(Expression... args) {
        super("&", args);
    }

    private static Expression[] merge(Expression... args) {
        List<Expression> list = new ArrayList<>();
        for (Expression arg : args) {
            arg = arg.conjunctiveNormalForm();
            if (arg instanceof And) {
                Collections.addAll(list, And.merge(arg.getArgs()));
            } else if (arg.equals(Literal.FALSE)) {
                list.clear();
                list.add(Literal.FALSE);
                break;
            } else {
                list.add(arg);
            }
        }
        Expression[] arr = new Expression[list.size()];
        list.toArray(arr);
        arr = Utils.removeDuplicates(arr, And.class);
        return arr;
    }

    @Override
    public boolean apply(Boolean... args) {
        return Arrays.stream(args).reduce(true, Boolean::logicalAnd);
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
            Expression[] leftArgs = (left instanceof Or) ? left.getArgs() : new Expression[]{left};
            Expression[] rightArgs = (right instanceof Or) ? right.getArgs() : new Expression[]{right};
            Expression[] newArgs = new Expression[leftArgs.length * rightArgs.length];
            // Expand parentheses
            for (int i = 0; i < leftArgs.length; i++) {
                for (int j = 0; j < rightArgs.length; j++) {
                    Expression[] arr = And.merge(leftArgs[i], rightArgs[j]);
                    newArgs[i * rightArgs.length + j] = (arr.length == 1) ? arr[0] : new And(arr);
                }
            }
            newArgs = Utils.removeDuplicates(newArgs, Or.class);
            return newArgs.length == 1 ? newArgs[0] : new Or(newArgs);
        }
    }

    @Override
    public Expression conjunctiveNormalForm() {
        Expression[] args = merge(this.args);
        return args.length == 1 ? args[0] : new And(args);
    }

    @Override
    public Expression disjunctiveNormalForm() {
        Expression conj = conjunctiveNormalForm();
        if (conj instanceof And) {
            And and = (And) conj;
            Expression current = And.disjunctiveTwo(and.getArg(0), and.getArg(1));
            for (int i = 2; i < and.argCount(); i++) {
                current = And.disjunctiveTwo(current, and.getArg(i));
            }
            return current;
        } else {
            return conj;
        }
    }
}