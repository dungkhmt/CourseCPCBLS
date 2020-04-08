package planningoptimization115657k62.nguyenvanduc;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class NQueens {
    public static void main(String[] args) {
        NQueens app = new NQueens();
        app.solve(150);
    }

    void solve(int n) {
        Model model = new Model();
        IntVar[] x = model.intVarArray(n, 0, n-1);
        IntVar[] d1 = new IntVar[n];
        IntVar[] d2 = new IntVar[n];

        for (int i = 0; i < n; i++) {
            d1[i] = model.intOffsetView(x[i], i);
            d2[i] = model.intOffsetView(x[i], -i);
        }

        model.allDifferent(x).post();
        model.allDifferent(d1).post();
        model.allDifferent(d2).post();

        model.getSolver().solve();

        System.out.println("solution: ");
        for (int i = 0; i < n; i++) {
            System.out.print(x[i].getValue() + " ");
        }
    }
}
