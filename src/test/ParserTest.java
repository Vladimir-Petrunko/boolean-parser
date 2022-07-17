import expression.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import parser.exception.BracketImbalanceException;
import parser.ExpressionParser;
import parser.exception.UnrecognizedOperandException;
import parser.exception.UnrecognizedOperatorException;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {

    private static final ExpressionParser parser = new ExpressionParser();

    @ParameterizedTest
    @MethodSource
    public void correctParsing(String initialString, Expression parsedExpression) {
        assertEquals(parser.parse(initialString), parsedExpression);
    }

    private static Stream<Arguments> correctParsing() {
        Variable x = new Variable("x");
        Variable y = new Variable("y");
        Variable z = new Variable("z");
        Literal T = Literal.TRUE;
        Literal F = Literal.FALSE;
        return Stream.of(
                Arguments.of("x", x),
                Arguments.of("x&y", new And(x, y)),
                Arguments.of("(x&y)", new And(x, y)),
                Arguments.of("(    x&     y  )", new And(x, y)),
                Arguments.of("(y|y )", new Or(y, y)),
                Arguments.of("x&y&z", new And(x, y, z)),
                Arguments.of("(x&y)&z", new And(new And(x, y), z)),
                Arguments.of("x&(y&z)", new And(x, new And(y, z))),
                Arguments.of("(x&y)&(y&z)", new And(new And(x, y), new And(y, z))),
                Arguments.of("x&y&z&0   &y", new And(x, y, z, F, y)),
                Arguments.of("x&y&z|z&y&x", new Or(new And(x, y, z), new And(z, y, x))),
                Arguments.of("~  x|y|~1", new Or(new Not(x), y, new Not(T))),
                Arguments.of("x^z&y|y&z", new Or(new Xor(x, new And(z, y)), new And(y, z))),
                Arguments.of("0", F),
                Arguments.of("(((( (1)) ))) ", T),
                Arguments.of("(x|0)&(1^y)", new And(new Or(x, F), new Xor(T, y)))
        );
    }

    @Test
    public void incorrectParsing() {
        assertThrows(BracketImbalanceException.class, () -> parser.parse("(x"));
        assertThrows(BracketImbalanceException.class, () -> parser.parse("x)"));
        assertThrows(UnrecognizedOperandException.class, () -> parser.parse("x & [y"));
        assertThrows(UnrecognizedOperandException.class, () -> parser.parse("2"));
        assertThrows(UnrecognizedOperatorException.class, () -> parser.parse("x ? y"));
    }

}
