package expression;

public interface Simplifiable {
    Expression conjunctiveNormalForm();

    Expression disjunctiveNormalForm();
}