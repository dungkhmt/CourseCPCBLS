package planningoptimization115657k62.damtrongtuyen;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.util.List;

public class BinPacking2D {
    public static void main(String[] args) {
        int N=3, W=4, H=6;
        int[] w = {3, 3, 1};
        int[] h = {2, 4, 6};
        Model model = new Model("bin packing 2d");
        IntVar[] x = model.intVarArray(N, 0, W);
        IntVar[] y = model.intVarArray(N, 0, H);
        IntVar[] o = model.intVarArray(N, 0, 1); // rotate 90 or not
        for (int i = 0; i < N; i++) {
            model.ifThen(model.arithm(o[i], "=", 0), model.and(model.arithm(model.intOffsetView(x[i], w[i]), "<=", W),
                    model.arithm(model.intOffsetView(y[i], h[i]), "<=", H)));
            model.ifThen(model.arithm(o[i], "=", 1), model.and(model.arithm(model.intOffsetView(x[i], h[i]), "<=", W),
                    model.arithm(model.intOffsetView(y[i], w[i]), "<=", H)));
        };

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N  ; j++) {
                model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0)),
                        model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
                                model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
                                model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]),
                                model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i])));

                model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1)),
                        model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
                                model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]),
                                model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]),
                                model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i])));

                model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0)),
                        model.or(model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]),
                                model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
                                model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]),
                                model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i])));

                model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1)),
                        model.or(model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]),
                                model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]),
                                model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]),
                                model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i])));
            }
        }
        System.out.println("hello man");
        List<Solution> solutions = model.getSolver().findAllSolutions();
        for(Solution s: solutions)
        {
            System.out.println(s.toString());
        }
//        for (int i = 0; i < N; i++) {
//            System.out.println('x'+i+": "+x[i].getValue());
//            System.out.println('y'+i+": "+y[i].getValue());
//            System.out.println('o'+i+": "+o[i].getValue());
//        }
    }
}
