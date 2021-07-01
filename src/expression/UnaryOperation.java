package expression;

import java.util.Map;
import utils.*;

public abstract class UnaryOperation extends Operation {
    protected final String operator;
    protected final Expression operand;

    public UnaryOperation(String operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    protected abstract boolean apply(boolean bool);

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> map) {
        return apply(operand.evaluate(map));
    }

    @Override
    public String toString() {
        return String.format("%s(%s)", operator, operand.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof UnaryOperation) {
            UnaryOperation op = (UnaryOperation) obj;
            return operand.equals(op.operand) && operator.equals(op.operator);
        } else {
            return false;
        }
    }
}
