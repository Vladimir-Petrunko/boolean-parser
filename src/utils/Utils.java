package utils;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import expression.Expression;
import expression.And;
import expression.Or;
import expression.Literal;

public class Utils {
    /**
     * Removes duplicate occurrences of expressions from an array. That is, leaves each expression at most once.
     *
     * @param args the input array
     * @return the processed array
     */
    public static Expression[] removeDuplicates(Expression[] args) {
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                if (args[i] != null && args[i].equals(args[j])) {
                    args[j] = null;
                }
            }
        }
        List<Expression> unique = new ArrayList<>();
        for (Expression arg : args) {
            if (arg != null) {
                unique.add(arg);
            }
        }
        Expression[] answer = new Expression[unique.size()];
        unique.toArray(answer);
        return answer;
    }

    /**
     * Simplify an AND clause by bringing together all other AND'ed expressions and simplifying literals.
     *
     * @param args an array that represents the arguments of the initial AND clause
     * @return an array that represents the arguments of the processed AND clause
     */
    public static Expression[] expandAnd(Expression[] args) {
        List<Expression> list = new ArrayList<>();
        for (Expression arg : args) {
            if (arg instanceof And) {
                // Recursively simplify nested expressions first
                Collections.addAll(list, expandAnd(arg.getArgs()));
            } else if (arg.equals(Literal.FALSE)) {
                // Short circuiting -- entire expression is false
                return new Expression[]{Literal.FALSE};
            } else {
                list.add(arg);
            }
        }
        Expression[] answer = new Expression[list.size()];
        list.toArray(answer);
        return answer.length == 0 ? new Expression[]{Literal.TRUE} : answer;
    }

    /**
     * Simplify an OR clause by bringing together all other OR'ed expressions and simplifying literals.
     *
     * @param args an array that represents the arguments of the initial OR clause
     * @return an array that represents the arguments of the processed OR clause
     */
    public static Expression[] expandOr(Expression[] args) {
        List<Expression> list = new ArrayList<>();
        for (Expression arg : args) {
            if (arg instanceof Or) {
                // Recursively simplify nested expressions first
                Collections.addAll(list, expandOr(arg.getArgs()));
            } else if (arg.equals(Literal.TRUE)) {
                // Short circuiting -- entire expression is false
                return new Expression[]{Literal.TRUE};
            } else {
                list.add(arg);
            }
        }
        Expression[] answer = new Expression[list.size()];
        list.toArray(answer);
        return answer.length == 0 ? new Expression[]{Literal.FALSE} : answer;
    }
}
