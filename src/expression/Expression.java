package expression;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

public abstract class Expression implements Simplifiable {
    public abstract boolean evaluate(Map<String, Boolean> map);

    @Override
    public abstract String toString();

    @Override
    public abstract boolean equals(Object obj);

    public abstract int argCount();

    public abstract Expression[] getArgs();

    public Expression get(int index) {
        return getArgs()[index];
    }

    public boolean[] truthTable(String[] variables) {
        boolean[] table = new boolean[1 << variables.length];
        for (int i = 0; i < table.length; i++) {
            Map<String, Boolean> map = new HashMap<>();
            for (int k = 0; k < variables.length; k++) {
                if ((i & (1 << k)) == 0) {
                    map.put(variables[k], false);
                } else {
                    map.put(variables[k], true);
                }
            }
            table[i] = evaluate(map);
        }
        return table;
    }

    public boolean isIdenticalTo(Expression other, String[] variables) {
        boolean[] thisTable = truthTable(variables);
        boolean[] otherTable = other.truthTable(variables);
        return Arrays.equals(thisTable, otherTable);
    }
}