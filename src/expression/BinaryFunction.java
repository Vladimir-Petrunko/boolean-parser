package expression;

public class BinaryFunction extends BinaryOperation {
    protected boolean[] truthTable;

    public BinaryFunction(boolean[] truthTable, String operator, Expression first, Expression second) {
        super(operator, first, second);
        if (truthTable.length != 4) {
            throw new IllegalArgumentException("truth table should have length 4");
        }
        this.truthTable = truthTable;
    }

    private boolean applyTwo(boolean left, boolean right) {
        int l = left ? 1 : 0;
        int r = right ? 1 : 0;
        return truthTable[l * 2 + r];
    }

    @Override
    public boolean apply(boolean... args) {
        if (args.length == 1) {
            return args[0];
        }
        boolean current = applyTwo(args[0], args[1]);
        for (int i = 2; i < args.length; i++) {
            current = applyTwo(current, args[i]);
        }
        return current;
    }

    private Expression joinTwo(Expression left, Expression right) {
        return new BinaryFunction(truthTable, operator, left, right).fullConjunctiveNormalForm();
    }

    public Expression canonical() {
        if (args.length == 1) {
            return args[0];
        }
        Expression current = joinTwo(args[0], args[1]);
        for (int i = 2; i < args.length; i++) {
            current = joinTwo(current, args[i]);
        }
        return current;
    }

    @Override
    public Expression conjunctiveNormalForm() {
        return canonical().conjunctiveNormalForm();
    }

    @Override
    public Expression disjunctiveNormalForm() {
        return canonical().disjunctiveNormalForm();
    }
}