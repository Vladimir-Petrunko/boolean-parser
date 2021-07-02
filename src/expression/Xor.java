package expression;

public class Xor extends BinaryFunction {
    public Xor(Expression left, Expression right) {
        super(new boolean[]{false, true, true, false}, "^", left, right);
    }
}