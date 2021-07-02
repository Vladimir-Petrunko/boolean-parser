package expression;

import java.util.List;
import java.util.Map;

public class Variable extends Expression {
    private final String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int argCount() {
        return 1;
    }

    @Override
    public Expression[] getArgs() {
        return new Expression[]{this};
    }

    @Override
    public boolean evaluate(Map<String, Boolean> map) {
        Boolean result = map.get(name);
        if (result == null) {
            throw new IllegalArgumentException("map does not contain binding for variable " + name);
        }
        return result;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variable) {
            Variable var = (Variable) obj;
            return name.equals(var.name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public Expression conjunctiveNormalForm() {
        return this;
    }

    @Override
    public Expression disjunctiveNormalForm() {
        return this;
    }
}
