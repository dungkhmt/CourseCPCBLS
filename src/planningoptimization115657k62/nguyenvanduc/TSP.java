package planningoptimization115657k62.nguyenvanduc;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

import java.util.HashSet;

public class TSP {
    static {
        System.loadLibrary("jniortools");
    }

    int N = 5;
    int[][] c = {
            {0,4,2,5,6},
            {2,0,5,2,7},
            {1,2,0,6,3},
            {7,5,8,0,3},
            {1,2,4,3,0}
    };



    double INF = Double.POSITIVE_INFINITY;
    MPSolver solver;
    MPVariable[][] x;

    public void solve() {
        if (N > 10) {
            return;
        }

        solver = new MPSolver("TSP solver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

        x = new MPVariable[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "x[" + i + ", " + j + "]");
            }
        }

        MPObjective obj = solver.objective();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j) {
                    obj.setCoefficient(x[i][j], c[i][j]);
                }
            }
        }
        obj.setMinimization();

        //constrain
        for (int i = 0; i < N; i++) {
            MPConstraint fc1 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++) {
                if (j != i) {
                    fc1.setCoefficient(x[i][j], 1);
                }
            }

            MPConstraint fc2 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++) {
                if (j != i) {
                    fc2.setCoefficient(x[j][i], 1);
                }
            }
        }

        //sub-tour
        SubSetGenerator generator = new SubSetGenerator(N);
        HashSet<Integer> S = generator.first();
        while (S != null) {

            if (S.size() > 1 && S.size() < N) {
                MPConstraint sc = solver.makeConstraint(0, S.size()-1);
                for (int i : S) {
                    for (int j : S) {
                        if (i != j) {
                            sc.setCoefficient(x[i][j], 1);
                        }
                    }
                }
            }
            S = generator.next();
        }

        final  MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
            System.err.println("does not have solution!");
            return;
        }



        System.out.println("time: " + solver.wallTime() + " milliseconds" );
        System.out.println("Optimal obj value: " + solver.objective().value());

        for (int i = 0; i < N; i++) {
            System.out.println();
            for (int j = 0; j < N; j++) {
                System.out.print(x[i][j].solutionValue() + ", ");
            }
        }

    }

    public static void main(String[] args) {
        TSP app = new TSP();
        app.solve();
    }
}
