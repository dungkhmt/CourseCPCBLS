package planningoptimization115657k62.NguyenQuynhLoc;

import com.google.ortools.linearsolver.*;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

class BCA {
    static {
        System.load("C:\\Users\\nql\\Desktop\\20192\\tulkh\\tu\\lib\\jniortools.dll");
    }
    int M = 3; // so giao vien 0,1,...,M-1
    int N = 13; // so mon hoc 0,1,...,N-1
    int[][] teachClass = { { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0 }, { 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1 }, };
    int[] credits = { 3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4 };
    int[][] conflict = { { 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0 }, { 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
            { 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1 }, { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 } };

    public void solve() {
        MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        MPVariable[][] x = new MPVariable[N][M];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                x[i][j] = solver.makeIntVar(0, 1, "x[" + i + "," + j + "]");
        MPVariable[] load = new MPVariable[M];
        int totalCredits = 0;
        for (int i = 0; i < credits.length; i++)
            totalCredits += credits[i];
        for (int i = 0; i < M; i++)
            load[i] = solver.makeIntVar(0, totalCredits, "load[" + i + "]");

        MPVariable y = solver.makeIntVar(0, totalCredits, "y");
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                if (teachClass[j][i] == 0) {
                    MPConstraint c = solver.makeConstraint(0, 0);
                    c.setCoefficient(x[i][j], 1);
                }
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (conflict[i][j] == 1) {
                    for (int k = 0; k < M; k++) {
                        MPConstraint c = solver.makeConstraint(0, 1);
                        c.setCoefficient(x[i][k], 1);
                        c.setCoefficient(x[j][k], 1);
                    }
                }
        for (int i = 0; i < N; i++) {
            MPConstraint c = solver.makeConstraint(1, 1);
            for (int j = 0; j < M; j++)
                c.setCoefficient(x[i][j], 1);
        }
        for (int i = 0; i < M; i++) {
            MPConstraint c = solver.makeConstraint(0, 0);
            for (int j = 0; j < N; j++)
                c.setCoefficient(x[j][i], credits[j]);
            c.setCoefficient(load[i], -1);
        }
        for (int i = 0; i < M; i++) {
            MPConstraint c = solver.makeConstraint(0, totalCredits);
            c.setCoefficient(load[i], -1);
            c.setCoefficient(y, 1);
        }
        MPObjective obj = solver.objective();
        obj.setCoefficient(y, 1);
        obj.setMinimization();
        ResultStatus rs = solver.solve();
        if (rs != ResultStatus.OPTIMAL) {
            System.out.println("No solution");
        } else {
            System.out.println("obj= " + obj.value());
            for (int i = 0; i < M; i++) {
                System.out.print("teacher " + i + ": ");
                for (int j = 0; j < N; j++)
                    if (x[j][i].solutionValue() == 1)
                        System.out.print(j + " ");
                System.out.println(", load = " + load[i].solutionValue());
            }
        }
    }

    public static void main(String[] args) {
        BCA app = new BCA();
        app.solve();
    }

}
