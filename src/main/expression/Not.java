package expression;

public class Not extends UnaryOperation {
    public Not(Expression operand) {
        super("~", operand);
    }

    @Override
    public boolean apply(Boolean... args) {
        return !args[0];
    }

    @Override
    public Expression conjunctiveNormalForm() {
        Expression simple = args[0].conjunctiveNormalForm();
        if (simple instanceof Literal) {
            Literal lit = (Literal) simple;
            return new Literal(!lit.getValue());
        } else if (simple instanceof And) {
            And and = (And) simple;
            Expression[] args = new Expression[and.argCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(and.getArg(i)).conjunctiveNormalForm();
            }
            return new Or(args);
        } else if (simple instanceof Or) {
            Or or = (Or) simple;
            Expression[] args = new Expression[or.argCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(or.getArg(i)).conjunctiveNormalForm();
            }
            return new And(args);
        } else if (simple instanceof Not) {
            Not not = (Not) simple;
            return not.args[0].conjunctiveNormalForm();
        } else {
            return new Not(simple);
        }
    }

    @Override
    public Expression disjunctiveNormalForm() {
        Expression simple = args[0].disjunctiveNormalForm();
        if (simple instanceof Literal) {
            Literal lit = (Literal) args[0];
            return new Literal(!lit.getValue());
        } else if (simple instanceof Or) {
            Or or = (Or) simple;
            Expression[] args = new Expression[or.argCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(or.getArg(i)).disjunctiveNormalForm();
            }
            return new And(args);
        } else if (simple instanceof And) {
            And and = (And) simple;
            Expression[] args = new Expression[and.argCount()];
            for (int i = 0; i < args.length; i++) {
                args[i] = new Not(and.getArg(i)).disjunctiveNormalForm();
            }
            return new Or(args);
        } else if (simple instanceof Not) {
            Not not = (Not) simple;
            return not.args[0].disjunctiveNormalForm();
        } else {
            return new Not(simple);
        }
    }
}
