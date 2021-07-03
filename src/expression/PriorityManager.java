package expression;

import java.util.List;

public class PriorityManager {
    private static final List<Class<?>> orderedPriorityList = List.of(Or.class, Xor.class, And.class);

    /**
     * Gets the relative priority of the given operator.
     *
     * @param operator the string representation of the operator
     * @return the relative priority of {@code operator}
     */
    public static int getPriority(String operator) {
        return switch (operator) {
            case "&" -> getPriority(And.class);
            case "|" -> getPriority(Or.class);
            case "^" -> getPriority(Xor.class);
            default -> throw new IllegalArgumentException("cannot recognize operator " + operator);
        };
    }

    /**
     * Gets the relative priority of the given operator.
     *
     * @param clazz the class of the operator
     * @return the relative priority of {@code operator}
     */
    public static int getPriority(Class<?> clazz) {
        for (int i = 0; i < orderedPriorityList.size(); i++) {
            if (orderedPriorityList.get(i) == clazz) {
                return i;
            }
        }
        return -1;
    }
}