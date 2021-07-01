package utils;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import expression.Expression;
import expression.And;
import expression.Or;
import expression.Literal;

public class Utils {
    public static Expression[] removeDuplicates(Expression[] args, Expression neutral) {
        for (int i = 0; i < args.length; i++) {
            for (int j = i + 1; j < args.length; j++) {
                if (args[i].equals(args[j])) {
                    args[j] = neutral;
                }
            }
        }
        List<Expression> unique = new ArrayList<>();
        for (Expression arg : args) {
            if (!arg.equals(neutral)) {
                unique.add(arg);
            }
        }
        Expression[] answer = new Expression[unique.size()];
        unique.toArray(answer);
        return answer;
    }

    public static Expression[] expandAnd(Expression[] args) {
        List<Expression> list = new ArrayList<>();
        for (Expression arg : args) {
            if (arg instanceof And) {
                Collections.addAll(list, ((And) arg).getArgs());
            } else if (arg.equals(Literal.FALSE)) {
                return new Expression[]{Literal.FALSE};
            } else {
                list.add(arg);
            }
        }
        Expression[] answer = new Expression[list.size()];
        list.toArray(answer);
        return answer;
    }

    public static Expression[] expandOr(Expression[] args) {
        List<Expression> list = new ArrayList<>();
        for (Expression arg : args) {
            if (arg instanceof Or) {
                Collections.addAll(list, ((Or) arg).getArgs());
            } else if (arg.equals(Literal.TRUE)) {
                return new Expression[]{Literal.TRUE};
            } else {
                list.add(arg);
            }
        }
        Expression[] answer = new Expression[list.size()];
        list.toArray(answer);
        return answer;
    }
}
