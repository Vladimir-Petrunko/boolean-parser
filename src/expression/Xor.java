package expression;

public class Xor extends BinaryFunction {
    public Xor(Expression... args) {
        super(new boolean[]{false, true, true, false}, "^", args);
    }
}