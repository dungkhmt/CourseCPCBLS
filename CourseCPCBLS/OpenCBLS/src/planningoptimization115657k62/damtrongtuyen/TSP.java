import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class TSP {
    static{
        System.loadLibrary("jniortools");
    }

    int N = 5;
    int [][] c = {{0,4,2,5,6},
                {2,0,5,2,7},
                {1,2,0,6,3},
                {7,5,8,0,3},
                {1,2,4,3,0}};
    double inf = Double.POSITIVE_INFINITY;
    MPSolver solver;
    MPVariable[][] x;
    public void solve()
    {
        if(N > 10)
        {
            System.out.println("N = 10 is too high, please use " +
                    "solveDynamicAddSubTourConstraint instead");
            return;
        }
        solver = new MPSolver("TSP solver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

        x = new MPVariable[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(i != j)
                {
                    x[i][j] = solver.makeIntVar(0, 1, "x"+i+","+j+"]");
                }
            }
        }

        MPObjective objective = solver.objective();
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                objective.setCoefficient(x[i][j], c[i][j]);
            }
        }

        for (int i = 0; i < N; i++) {
            MPConstraint c1 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++) {
                if(i != j)
                {
                    c1.setCoefficient(x[i][j], 1);
                }
            }
            MPConstraint c2 = solver.makeConstraint(1, 1);
            for (int j = 0; j < N; j++) {
                c2.setCoefficient(x[j][i], 1);
            }
        }

//        SubSetGenerator generator
    }
    public static void main(String[] args) {

    }
}
