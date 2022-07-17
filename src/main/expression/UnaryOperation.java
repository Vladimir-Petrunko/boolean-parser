package expression;

public abstract class UnaryOperation extends Operation {
    public UnaryOperation(String operator, Expression operand) {
        super(operator, operand);
    }

    @Override
    public String toString() {
        String pattern = (args[0] instanceof Operation) ? "%s(%s)" : "%s%s";
        return String.format(pattern, operator, args[0].toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnaryOperation) {
            UnaryOperation op = (UnaryOperation) obj;
            return args[0].equals(op.args[0]) && operator.equals(op.operator);
        } else {
            return false;
        }
    }
}
