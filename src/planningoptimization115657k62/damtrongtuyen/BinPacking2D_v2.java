import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.util.List;
import java.util.Scanner;

public class BinPacking2D {
    int n;
    public int W, H;
    public int w[] = new int[199];
    public int h[] = new int[199];

    private void load_data(String file_path) {
        try {
            File file = new File(file_path);
            Scanner scanner = new Scanner(file);

            W = scanner.nextInt();
            H = scanner.nextInt();
            int count = 0;
            while (true) {
                int tmp;
                tmp = scanner.nextInt();
                if (tmp == -1) break;
                w[count] = tmp;
                h[count] = scanner.nextInt();
                count++;
            }
            n = count;

        } catch (Exception e) {
            e.printStackTrace();
        }
        }
    public static void main(String[] args) {
        int N=3, W=4, H=6;
        int[] w = {3, 3, 1};
        int[] h = {2, 4, 6};
        Model model = new Model("bin packing 2d");
        IntVar[] x = model.intVarArray(N, 0, W);
        IntVar[] y = model.intVarArray(N, 0, H);
        IntVar[] o = model.intVarArray(N, 0, 1); // rotate 90 or not
//        for (int i = 0; i < N; i++) {
//            model.ifThen(model.arithm(o[i], "=", 0), model.and(model.arithm(model.intOffsetView(x[i], w[i]), "<=", W),
//                    model.arithm(model.intOffsetView(y[i], h[i]), "<=", H)));
//            model.ifThen(model.arithm(o[i], "=", 1), model.and(model.arithm(model.intOffsetView(x[i], h[i]), "<=", W),
//                    model.arithm(model.intOffsetView(y[i], w[i]), "<=", H)));
//        };
//
//        for (int i = 0; i < N; i++) {
//            for (int j = 0; j < N  ; j++) {
//                model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0)),
//                        model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
//                                model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
//                                model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]),
//                                model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i])));
//
//                model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1)),
//                        model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
//                                model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]),
//                                model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]),
//                                model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i])));
//
//                model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0)),
//                        model.or(model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]),
//                                model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
//                                model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]),
//                                model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i])));
//
//                model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1)),
//                        model.or(model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]),
//                                model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]),
//                                model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]),
//                                model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i])));
//            }
//        }

        for (int i = 0; i < N; i++) { // better code, todo: read again, try this way
            model.scalar(new IntVar[] {x[i], o[i]}, new int[] {1, h[i] - w[i]}, "<=", W - w[i]).post();
            model.scalar(new IntVar[] {y[i], o[i]}, new int[] {1, -h[i] + w[i]}, "<=", H - h[i]).post();

        }

        for (int i = 0; i < N; i++)
            for (int j = i + 1; j < N; j++) {
                Constraint c1 = model.scalar(new IntVar[]{x[i], o[i], x[j]}, new int[]{1, h[i] - w[i], -1}, "<=", -w[i]);
                Constraint c2 = model.scalar(new IntVar[]{x[j], o[j], x[i]}, new int[]{1, h[j] - w[j], -1}, "<=", -w[j]);

                Constraint c3 = model.scalar(new IntVar[]{y[i], o[i], y[j]}, new int[]{1, -h[i] + w[i], -1}, "<=", -h[i]);
                Constraint c4 = model.scalar(new IntVar[]{y[j], o[j], y[i]}, new int[]{1, -h[j] + w[j], -1}, "<=", -h[j]);
                model.or(model.or(c1, c2), model.or(c3, c4)).post();
            }

        System.out.println("hello man");
//        List<Solution> solutions = model.getSolver().findAllSolutions();
//        for(Solution s: solutions)
//        {
//            System.out.println(s.toString());
//        }
        Solver s = model.getSolver();
        while(s.solve())
        {
            for (int i = 0; i < N; i++) {
                System.out.println('x'+i+": "+x[i].getValue());
                System.out.println('y'+i+": "+y[i].getValue());
                System.out.println('o'+i+": "+o[i].getValue());
            }
        }

    }
}
