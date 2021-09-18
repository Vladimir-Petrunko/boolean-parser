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
    public Expression conjunctiveNormalForm() {
        Expression simple = operand.conjunctiveNormalForm();
        if (simple instanceof Literal) {
            Literal lit = (Literal) simple;
            return new Literal(!lit.getValue());
        } else if (simple instanceof And) {
            And and = (And) simple;
            Expression[] args = new Expression[and.argCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(and.get(i)).conjunctiveNormalForm();
            }
            return new Or(args);
        } else if (simple instanceof Or) {
            Or or = (Or) simple;
            Expression[] args = new Expression[or.argCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(or.get(i)).conjunctiveNormalForm();
            }
            return new And(args);
        } else if (simple instanceof Not) {
            Not not = (Not) simple;
            return not.operand.conjunctiveNormalForm();
        } else {
            return new Not(simple);
        }
    }

    @Override
    public Expression disjunctiveNormalForm() {
        Expression simple = operand.disjunctiveNormalForm();
        if (simple instanceof Literal) {
            Literal lit = (Literal) operand;
            return new Literal(!lit.getValue());
        } else if (simple instanceof Or) {
            Or or = (Or) simple;
            Expression[] args = new Expression[or.argCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(or.get(i)).disjunctiveNormalForm();
            }
            return new And(args);
        } else if (simple instanceof And) {
            And and = (And) simple;
            Expression[] args = new Expression[and.argCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(and.get(i)).disjunctiveNormalForm();
            }
            return new Or(args);
        } else if (simple instanceof Not) {
            Not not = (Not) simple;
            return not.operand.disjunctiveNormalForm();
        } else {
            return new Not(simple);
        }
    }
}
