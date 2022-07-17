import expression.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ExpressionTest {

    @ParameterizedTest
    @MethodSource
    public void testBasicOperations(Expression expression, int variablesCnt, boolean[] truthTable) {
        assertEquals(expression.argCount(), variablesCnt);
        assertEquals(expression.getVariableList().length, variablesCnt);
        assertEquals(1 << variablesCnt, truthTable.length);
        boolean[] arguments = new boolean[variablesCnt];
        for (int i = 0; i < truthTable.length; i++) {
            for (int k = 0; k < variablesCnt; k++) {
                arguments[k] = (i & (1 << k)) != 0;
            }
            assertEquals(expression.evaluate(arguments), truthTable[i]);
        }
    }

    private static Stream<Arguments> testBasicOperations() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Variable z = new Variable("z");
        return Stream.of(
                Arguments.of(new And(x, y), 2, new boolean[]{false, false, false, true}),
                Arguments.of(new Or(x, y), 2, new boolean[]{false, true, true, true}),
                Arguments.of(new Xor(x, y), 2, new boolean[]{false, true, true, false}),
                Arguments.of(new Not(x), 1, new boolean[]{true, false}),
                Arguments.of(x, 1, new boolean[]{false, true}),
                Arguments.of(new And(x, y, z), 3, new boolean[]{false, false, false, false, false, false, false, true}),
                Arguments.of(new Or(x, y, z), 3, new boolean[]{false, true, true, true, true, true, true, true}),
                Arguments.of(new Xor(x, y, z), 3, new boolean[]{false, true, true, false, true, false, false, true})
        );
    }

    @Test
    public void testBinaryFunction() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        assertThrows(Exception.class, () -> {
            new BinaryFunction(new boolean[]{true, false}, ":", x, y);
        });
        assertThrows(Exception.class, () -> {
            new BinaryFunction(new boolean[]{true, true, true, true, false, false, false, false}, ":", x, y);
        });
        BinaryFunction peirce = new BinaryFunction(new boolean[]{true, false, false, false}, "v", x, y);
        assertTrue(peirce.evaluate(false, false));
        assertFalse(peirce.evaluate(false, true));
        assertFalse(peirce.evaluate(true, false));
        assertFalse(peirce.evaluate(true, true));
    }

    @Test
    public void testEquivalence() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Literal T = Literal.TRUE;
        Literal F = Literal.FALSE;
        assertTrue(x.isEquivalentTo(new And(x, T)));
        assertTrue(x.isEquivalentTo(x));
        assertFalse(x.isEquivalentTo(y));
        assertFalse(x.isEquivalentTo(new Or(x, T)));
        assertTrue(T.isEquivalentTo(new Or(x, new Not(x))));
        assertTrue(T.isEquivalentTo(new Or(new And(x, y), new And(x, new Not(y)), new And(new Not(x), y), new And(new Not(x), new Not(y)))));
        assertTrue(y.isEquivalentTo(new Or(y, F)));
        assertTrue(y.isEquivalentTo(new And(y, y)));
        assertTrue(y.isEquivalentTo(new Or(y, y)));
        assertTrue(new Xor(x, new Not(x)).isEquivalentTo(T));
    }

    @ParameterizedTest
    @MethodSource("simplifyingExamples")
    public void testConjunctiveNormalForm(Expression expression) {
        Expression cnf = expression.conjunctiveNormalForm();
        assertTrue(cnf.isEquivalentTo(expression));
        assertTrue(cnf instanceof And || cnf instanceof Or || isUnit(cnf));
        if (cnf instanceof And) {
            for (Expression arg : cnf.getArgs()) {
                assertTrue(arg instanceof Or || isUnit(arg));
                if (arg instanceof Or) {
                    for (Expression inner : arg.getArgs()) {
                        assertTrue(isUnit(inner));
                    }
                }
            }
        } else if (cnf instanceof Or) {
            for (Expression inner : cnf.getArgs()) {
                assertTrue(isUnit(inner));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("simplifyingExamples")
    public void testDisjunctiveNormalForm(Expression expression) {
        Expression cnf = expression.disjunctiveNormalForm();
        assertTrue(cnf.isEquivalentTo(expression));
        assertTrue(cnf instanceof And || cnf instanceof Or || isUnit(cnf));
        if (cnf instanceof Or) {
            for (Expression arg : cnf.getArgs()) {
                assertTrue(arg instanceof And || isUnit(arg));
                if (arg instanceof And) {
                    for (Expression inner : arg.getArgs()) {
                        assertTrue(isUnit(inner));
                    }
                }
            }
        } else if (cnf instanceof And) {
            for (Expression inner : cnf.getArgs()) {
                assertTrue(isUnit(inner));
            }
        }
    }

    @ParameterizedTest
    @MethodSource("simplifyingExamples")
    public void testFullConjunctiveNormalForm(Expression expression) {
        testConjunctiveNormalForm(expression);
        int variablesCount = expression.getVariableList().length;
        Expression cnf = expression.fullConjunctiveNormalForm();
        if (cnf instanceof And) {
            for (Expression arg : cnf.getArgs()) {
                assertEquals(arg.argCount(), variablesCount);
            }
        } else if (cnf instanceof Or) {
            assertEquals(cnf.argCount(), variablesCount);
        }
    }

    @ParameterizedTest
    @MethodSource("simplifyingExamples")
    public void testFullDisjunctiveNormalForm(Expression expression) {
        testDisjunctiveNormalForm(expression);
        int variablesCount = expression.getVariableList().length;
        Expression dnf = expression.fullDisjunctiveNormalForm();
        if (dnf instanceof Or) {
            for (Expression arg : dnf.getArgs()) {
                assertEquals(arg.argCount(), variablesCount);
            }
        } else if (dnf instanceof And) {
            assertEquals(dnf.argCount(), variablesCount);
        }
    }

    @ParameterizedTest
    @MethodSource("simplifyingExamples")
    public void testZhegalkinPolynomial(Expression expression) {
        Expression zhegalkin = expression.zhegalkinPolynomial();
        assertTrue(zhegalkin instanceof Xor || zhegalkin instanceof And || isUnit(zhegalkin));
        if (zhegalkin instanceof Xor) {
            for (Expression arg : zhegalkin.getArgs()) {
                assertTrue(arg instanceof And || isUnit(arg));
                if (arg instanceof And) {
                    for (Expression inner : arg.getArgs()) {
                        assertTrue(isUnit(inner));
                    }
                }
            }
        } else if (zhegalkin instanceof And) {
            for (Expression inner : zhegalkin.getArgs()) {
                assertTrue(isUnit(inner));
            }
        }
    }

    private static Stream<Arguments> simplifyingExamples() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Variable z = new Variable("z");
        return Stream.of(
                Arguments.of(x),
                Arguments.of(Literal.FALSE),
                Arguments.of(Literal.TRUE),
                Arguments.of(new And(x, y)),
                Arguments.of(new And(x, y, z)),
                Arguments.of(new Or(x, y)),
                Arguments.of(new Or(x, y, z)),
                Arguments.of(new Xor(x, y)),
                Arguments.of(new Xor(x, y, z)),
                Arguments.of(new Not(x)),
                Arguments.of(new And(x, Literal.FALSE)),
                Arguments.of(new And(x, Literal.TRUE)),
                Arguments.of(new Or(y, Literal.FALSE)),
                Arguments.of(new Or(y, Literal.TRUE)),
                Arguments.of(new Xor(z, Literal.FALSE)),
                Arguments.of(new Xor(z, Literal.TRUE)),
                Arguments.of(new Not(Literal.FALSE)),
                Arguments.of(new Not(Literal.TRUE)),
                Arguments.of(new And(new Xor(x, y, new Not(z))), new Or(new Not(x), y, z)),
                Arguments.of(new And(new Xor(x, y, new Or(x, z)), new Xor(new Not(x), new And(y, z)))),
                Arguments.of(new And(x, y, new Xor(x, y), new Or(new Not(x), y, z))),
                Arguments.of(new Or(new And(x, y), new And(new Xor(x, z), new Not(y)))),
                Arguments.of(new Xor(new And(x, z), new And(x, y), new Or(x, y, new Not(z))))
        );
    }


    private boolean isUnit(Expression expression) {
        if (expression instanceof Not) {
            return isUnit(expression.getArg(0));
        } else {
            return expression instanceof Literal || expression instanceof Variable;
        }
    }

}