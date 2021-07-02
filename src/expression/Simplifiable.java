package expression;

public interface Simplifiable {
    /**
     * Returns an expression with the same truth table as this expression, but in conjunctive normal form (CNF). Note
     * that the expression will not necessarily be in <b>full</b> conjunctive normal form.<br><br>
     *
     * This method has an exponential <b>worst-case</b> time complexity.
     *
     * @return the CNF of this expression
     * @see #disjunctiveNormalForm()
     * @see #fullConjunctiveNormalForm()
     */
    Expression conjunctiveNormalForm();

    /**
     * Returns an expression with the same truth table as this expression, but in disjunctive normal form (DNF). Note
     * that the expression will not necessarily be in <b>full</b> disjunctive normal form.<br><br>
     *
     * This method has an exponential <b>worst-case</b> time complexity.
     *
     * @return the DNF of this expression
     * @see #conjunctiveNormalForm()
     * @see #fullDisjunctiveNormalForm()
     */
    Expression disjunctiveNormalForm();

    /**
     * Returns the full conjunctive normal form of this expression.<br><br>
     *
     * This method has an exponential <b>best-case</b> time complexity.
     *
     * @return the full CNF of this expression
     * @see #conjunctiveNormalForm()
     * @see #fullDisjunctiveNormalForm()
     */
    Expression fullConjunctiveNormalForm();

    /**
     * Returns the full disjunctive normal form of this expression.<br><br>
     *
     * This method has an exponential <b>best-case</b> time complexity.
     *
     * @return the full CNF of this expression
     * @see #disjunctiveNormalForm()
     * @see #fullConjunctiveNormalForm()
     */
    Expression fullDisjunctiveNormalForm();
}