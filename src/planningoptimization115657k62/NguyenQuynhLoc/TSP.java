package planningoptimization115657k62.NguyenQuynhLoc;
import java.util.HashSet;
import com.google.ortools.linearsolver.*;

public class TSP {
    static {
        System.load("C:\\Users\\nql\\Desktop\\20192\\tulkh\\tu\\lib\\jniortools.dll");
    }
    int N = 5; // so thanh pho
    int[][] c = { { 0, 4, 2, 5, 6 }, 
                  { 2, 0, 5, 2, 7 }, 
                  { 1, 2, 0, 6, 3 }, 
                  { 7, 5, 8, 0, 3 }, 
                  { 1, 2, 4, 3, 0 } };
    double inf = java.lang.Double.POSITIVE_INFINITY;
    MPSolver solver;
    MPVariable[][] x;
    
    public void solve() {
        solver = new MPSolver("TSP solver", MPSolver.OptimizationProblemType.valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
        x = new MPVariable[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j)
                    x[i][j] = solver.makeIntVar(0, 1, "x[" + i + "," + j + "]");
            }
        }

        MPObjective obj = solver.objective();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++)
                if (i != j) {
                    obj.setCoefficient(x[i][j], c[i][j]);
                }
        }

        for (int i = 0; i < N; i++) {
            MPConstraint fc1 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++){
                if (i != j) 
                    fc1.setCoefficient(x[i][j], 1);
            }                    
            MPConstraint fc2 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++){
                if (i != j) {
                    fc2.setCoefficient(x[j][i], 1);
                }
            }                
        }

        SubSetGenerator generator = new SubSetGenerator(N);
        HashSet<Integer> S = generator.first();
        while (S != null) {
            if (S.size() > 1 && S.size() < N) {
                MPConstraint sc = solver.makeConstraint(0, S.size() - 1);
                for (int i : S) {
                    for (int j : S)
                        if (i != j) {
                            sc.setCoefficient(x[i][j], 1);
                        }
                }
            }
        }
        S = generator.next();
        MPSolver.ResultStatus resultStatus = solver.solve();
        if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
            System.err.println("No solution!");
        }else{
            System.out.println("best = " + solver.objective().value());
        }
    }

    public static void main(String[] args) {
        TSP app = new TSP();
        app.solve();
    }

}

class SubSetGenerator {
    int N;
    int[] X;// represents binary sequence

    public SubSetGenerator(int N) {
        this.N = N;
    }

    public HashSet<Integer> first() {
        X = new int[N];
        for (int i = 0; i < N; i++)
            X[i] = 0;
        HashSet<Integer> S = new HashSet<Integer>();
        for (int i = 0; i < N; i++)
            if (X[i] == 1)
                S.add(i);
        return S;
    }

    public HashSet<Integer> next() {
        int j = N - 1;
        while (j >= 0 && X[j] == 1) {
            X[j] = 0;
            j--;
        }
        if (j >= 0) {
            X[j] = 1;
            HashSet<Integer> S = new HashSet<Integer>();
            for (int i = 0; i < N; i++)
                if (X[i] == 1)
                    S.add(i);
            return S;
        } else {
            return null;
        }
    }
}
