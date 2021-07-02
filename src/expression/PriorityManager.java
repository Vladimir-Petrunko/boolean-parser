package expression;

import java.util.List;

public class PriorityManager {
    private static final List<Class<?>> orderedPriorityList = List.of(Or.class, Xor.class, And.class);

    public static int getPriority(String operator) {
        return switch (operator) {
            case "&" -> getPriority(And.class);
            case "|" -> getPriority(Or.class);
            case "^" -> getPriority(Xor.class);
            default -> throw new IllegalArgumentException("cannot recognize operator " + operator);
        };
    }

    public static int getPriority(Class<?> clazz) {
        for (int i = 0; i < orderedPriorityList.size(); i++) {
            if (orderedPriorityList.get(i) == clazz) {
                return i;
            }
        }
        return -1;
    }
}