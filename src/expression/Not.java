package expression;

import java.util.List;

public class Not extends UnaryOperation {
    public Not(Expression operand) {
        super("~", operand);
    }

    @Override
    public boolean apply(boolean bool) {
        return !bool;
    }

    @Override
    public Expression canonical() {
        return this;
    }

    @Override
    public Expression conjunctiveNormalForm() {
        if (operand instanceof Literal) {
            Literal lit = (Literal) operand;
            return new Literal(!lit.getValue());
        } else if (operand instanceof And) {
            And and = (And) operand;
            Expression[] args = new Expression[and.args.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(and.args[i]);
            }
            return new Or(args);
        } else if (operand instanceof Or) {
            Or or = (Or) operand;
            Expression[] args = new Expression[or.args.length];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(or.args[i]);
            }
            return new And(args);
        } else {
            return this;
        }
    }

    @Override
    public Expression disjunctiveNormalForm() {
        return conjunctiveNormalForm();
    }
}
