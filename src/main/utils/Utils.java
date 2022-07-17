package utils;

import java.util.*;
import java.util.stream.Collectors;

import expression.*;

public class Utils {
    /**
     * Removes duplicate occurrences of expressions from an array. That is, leaves each expression at most once.
     * The parameter <code>mode</code> represents the relationship (either And or Or) between the input expressions.
     * Depending on the mode, some input arrays (such as [Literal.FALSE, Literal.TRUE]) will be simplified differently.
     *
     * @param args the input array
     * @param mode the relationship between the arguments in the input array
     * @return the processed array
     */
    public static Expression[] removeDuplicates(Expression[] args, Class<?> mode) {
        for (int i = 0; i < args.length; i++) {
            for (int j = 0; j < args.length; j++) {
                if (i == j || args[i] == null || args[j] == null) continue;
                if (args[i].isEquivalentTo(args[j])) {
                    args[j] = null;
                } else if (mode == And.class || mode == Or.class) {
                    if (args[i] instanceof Not && args[i].getArg(0).equals(args[j])) {
                        args[i] = null;
                        if (mode == And.class) {
                            args[j] = Literal.FALSE;
                        } else {
                            args[j] = Literal.TRUE;
                        }
                    } else if (args[i].equals(Literal.FALSE)) {
                        if (mode == And.class) {
                            return new Expression[]{Literal.FALSE};
                        } else {
                            args[i] = null;
                        }
                    } else if (args[i].equals(Literal.TRUE)) {
                        if (mode == And.class) {
                            args[i] = null;
                        } else {
                            return new Expression[]{Literal.TRUE};
                        }
                    }
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
     * Removes duplicate elements from an array.
     *
     * @param args the input array
     * @return the processed array
     */
    public static Object[] removeDuplicates(Object[] args) {
        Set<Object> set = Arrays.stream(args).collect(Collectors.toSet());
        Object[] answer = new Object[set.size()];
        set.toArray(answer);
        return answer;
    }
}
