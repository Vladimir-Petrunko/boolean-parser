import expression.*;
import parser.*;
import java.util.*;
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ExpressionParser parser = new ExpressionParser();
        while (true) {
            String s = in.nextLine();
            Expression expression = parser.parse(s);
            System.out.println("Initial: " + expression);
            System.out.println("CNF: " + expression.conjunctiveNormalForm());
            System.out.println("DNF: " + expression.disjunctiveNormalForm());
            System.out.println(expression.isIdenticalTo(expression.conjunctiveNormalForm(), new String[]{"x", "y", "z", "a", "b"}));
            System.out.println(expression.isIdenticalTo(expression.disjunctiveNormalForm(), new String[]{"x", "y", "z", "a", "b"}));
        }
    }
}
