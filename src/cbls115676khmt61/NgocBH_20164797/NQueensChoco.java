package cbls115676khmt61.ngocbh_20164797;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;


public class NQueensChoco {
    private int N;
    private Model model;
    private IntVar[] X, X1, X2;
    private int[] x;

    public NQueensChoco(int n) {
        N = n;
        model = new Model("N-Queen");
        X = model.intVarArray(N, 1, N);
        X1 = model.intVarArray(N, 1, 2 * N - 1);
        X2 = model.intVarArray(N, -N + 1, N);
        x = new int[N];

        solver();
    }

    public int[] getSol() {
        return x;
    }

    private void solver() {
        for (int i = 0; i < N; i++) {
            X1[i] = model.intOffsetView(X[i], i);
            X2[i] = model.intOffsetView(X[i], -i);
        }

        model.allDifferent(X).post();
        model.allDifferent(X1).post();
        model.allDifferent(X2).post();
        model.getSolver().solve();
        for (int i = 0; i < N; i++) {
            x[i] = X[i].getValue();
        }
    }

    public static void main(String[] args) {
        long s = System.currentTimeMillis();
        NQueensChoco app = new NQueensChoco(10);
        System.out.println("My Solution");
        for (int x : app.getSol()) {
            System.out.print(x + " ");
        }
        System.out.println("\nTime " + (System.currentTimeMillis() - s) / 1000.0 + "(s)");
    }
}

