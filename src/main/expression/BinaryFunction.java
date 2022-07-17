package expression;

public class BinaryFunction extends Function {
    protected final boolean[] truthTable;

    public BinaryFunction(boolean[] truthTable, String operator, Expression arg1, Expression arg2) {
        super(truthTable, operator, arg1, arg2);
        this.truthTable = truthTable;
    }

    @Override
    public boolean apply(Boolean... args) {
        int l = args[0] ? 1 : 0;
        int r = args[1] ? 1 : 0;
        return truthTable[l * 2 + r];
    }

    @Override
    public Expression conjunctiveNormalForm() {
        return fullConjunctiveNormalForm();
    }

    @Override
    public Expression disjunctiveNormalForm() {
        return fullDisjunctiveNormalForm();
    }
}