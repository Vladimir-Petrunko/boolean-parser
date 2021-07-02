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
            Expression cnf = expression.conjunctiveNormalForm();
            Expression dnf = expression.disjunctiveNormalForm();
            Expression f_cnf = expression.fullConjunctiveNormalForm();
            Expression f_dnf = expression.fullDisjunctiveNormalForm();
            System.out.println("Initial: " + expression);
            System.out.println("CNF: " + cnf);
            System.out.println("DNF: " + dnf);
            System.out.println("F CNF: " + f_cnf);
            System.out.println("F DNF: " + f_dnf);
            System.out.println(cnf.isIdenticalTo(expression));
            System.out.println(dnf.isIdenticalTo(expression));
            System.out.println(f_cnf.isIdenticalTo(expression));
            System.out.println(f_dnf.isIdenticalTo(expression));
        }
    }
}
