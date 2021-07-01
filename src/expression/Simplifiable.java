package expression;

public interface Simplifiable {
    Expression canonical();

    Expression conjunctiveNormalForm();

    Expression disjunctiveNormalForm();
}