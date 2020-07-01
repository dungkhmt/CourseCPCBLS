package Choco;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class BinPacking {
    int n = 5;
    int H = 9;
    int W = 4;
    int[] w = {3, 1, 3, 3, 1};
    int[] h = {3, 5, 3, 3, 4};

    IntVar[] x = new IntVar[n];
    IntVar[] y = new IntVar[n];
    IntVar[] r = new IntVar[n];
    Model model;

    public static void main(String[] args) {
        BinPacking test = new BinPacking();
        test.solve();
    }

    public void solve() {
        model = new Model("BinPacking");
        r = model.intVarArray("r", n, 0, 1);
        for (int i = 0; i < n; i++) {
            x[i] = model.intVar("x" + i, 0, W - 1);
            y[i] = model.intVar("y" + i, 0, H - 1);
        }

        //Each pack inside
        for (int i = 0; i < n; i++) {
            Constraint c1 = model.and(model.arithm(model.intOffsetView(x[i], w[i]), "<=", W),
                    model.arithm(model.intOffsetView(y[i], h[i]), "<=", H));
            Constraint c2 = model.and(model.arithm(model.intOffsetView(x[i], h[i]), "<=", W),
                    model.arithm(model.intOffsetView(y[i], w[i]), "<=", H));
            model.ifThenElse(model.arithm(r[i], "=", 0), c1, c2);
        }

        //Not overlap
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Constraint c1 = model.and(model.arithm(r[i], "=", 0), model.arithm(r[j], "=", 0));
                Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
                Constraint c3 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
                Constraint c4 = model.arithm(model.intOffsetView(x[j], w[i]), "<=", x[i]);
                Constraint c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
                model.ifThen(c1, model.or(c2, c3, c4, c5));

                Constraint c6 = model.and(model.arithm(r[i], "=", 0), model.arithm(r[j], "=", 1));
                Constraint c7 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
                Constraint c8 = model.arithm(model.intOffsetView(y[i], h[j]), "<=", y[j]);
                Constraint c9 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
                Constraint c10 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
                model.ifThen(c6, model.or(c7, c8, c9, c10));

                Constraint c11 = model.and(model.arithm(r[i], "=", 1), model.arithm(r[j], "=", 0));
                Constraint c12 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
                Constraint c13 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
                Constraint c14 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
                Constraint c15 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
                model.ifThen(c11, model.or(c12, c13, c14, c15));

                Constraint c16 = model.and(model.arithm(r[i], "=", 1), model.arithm(r[j], "=", 1));
                Constraint c17 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
                Constraint c18 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
                Constraint c19 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
                Constraint c20 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
                model.ifThen(c16, model.or(c17, c18, c19, c20));

            }
        }
        Solver s = model.getSolver();
        while (s.solve()) {
            int[][] map = new int[W][H];
            for (int i = 0; i < n; i++) {
                int X = x[i].getValue();
                int Y = y[i].getValue();
                int R = r[i].getValue();
                map[X][Y] = i;
                if (R == 0) {
                    for (int j = X; j < X + w[i]; j++)
                        for (int k = Y; k < Y + h[i]; k++)
                            map[j][k] = i;
                } else {
                    for (int j = X; j < X + h[i]; j++)
                        for (int k = Y; k < Y + w[i]; k++)
                            map[j][k] = i;
                }
            }

            for (int i = 0; i < H; i++) {
                for (int j = 0; j < W; j++) {
                    System.out.print(map[j][i] + "  ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
