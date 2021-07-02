package expression;

public interface Simplifiable {
    /**
     * Returns an expression with the same truth table as this expression, but in conjunctive normal form (CNF). Note
     * that the expression will not necessarily be in <b>full</b> conjunctive normal form.
     *
     * @return the CNF of this expression
     */
    Expression conjunctiveNormalForm();

    /**
     * Returns an expression with the same truth table as this expression, but in disjunctive normal form (DNF). Note
     * that the expression will not necessarily be in <b>full</b> disjunctive normal form.
     *
     * @return the DNF of this expression
     */
    Expression disjunctiveNormalForm();
}