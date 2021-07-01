package expression;

import java.util.List;
import java.util.Map;

public class Literal extends Expression {
    public static final Literal TRUE = new Literal(true);
    public static final Literal FALSE = new Literal(false);

    private final boolean value;

    public Literal(boolean value) {
        this.value = value;
    }

    public Literal(int value) {
        if (value == 0 || value == 1) {
            this.value = (value == 1);
        } else {
            throw new IllegalArgumentException("value should be either 0 or 1");
        }
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public boolean evaluate(Map<String, Boolean> map) {
        return value;
    }

    @Override
    public String toString() {
        return value ? "1" : "0";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Literal) {
            Literal lit = (Literal) obj;
            return value == lit.value;
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return value ? 1 : 0;
    }

    @Override
    public Expression canonical() {
        return this;
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
