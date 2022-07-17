package expression;

import java.util.Arrays;

public class Xor extends Operation {
    public Xor(Expression... args) {
        super("^", args);
    }

    @Override
    public boolean apply(Boolean... args) {
        return Arrays.stream(args).reduce(false, Boolean::logicalXor);
    }
}