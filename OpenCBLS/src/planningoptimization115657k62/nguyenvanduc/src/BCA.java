import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class BCA {
    static {
        System.loadLibrary("jniortools");
    }

    //input data
    int M = 3; // so giao vien
    int N = 13; // so mon hoc
    int [][] teachClass =  {
            { 1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0 },
            { 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
            { 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1 }
    };

    int[] credits = {3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4};

    int [][] conflict =  {
            { 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
            { 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1 },
            { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1 },
            { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
            { 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 } };


    public void solve() {
        MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        MPVariable[][] x = new MPVariable[N][M]; //x[i, j] = 1 neu giao vien j day lop i
        for (int i = 0; i < N; i++)
            for (int j = 0; j < M; j++)
                x[i][j] = solver.makeIntVar(0, 1, "x[" + i + " ," + j + "]");

        MPVariable[] load = new MPVariable[M]; // so tin chi cua moi gv
        int totalCredits = 0;
        for (int i = 0; i < credits.length; i++) {
            totalCredits += credits[i];
        }
        for (int i = 0; i < M; i++) {
            load[i] = solver.makeIntVar(0, totalCredits, "load[" + i +"]");
        }

        MPVariable y = solver.makeIntVar(0, totalCredits, "y");

        //x[i,j] = 0 neu giao vien j khong day duoc lop i
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if (teachClass[j][i] == 0) {
                    MPConstraint c = solver.makeConstraint(0, 0);
                    c.setCoefficient(x[i][j], 1);
                }
            }
        }

        // x[i1, j] + x[i2, j] <= 1 if (i1, i2) conflict - lop i1, i2 khong the do 1 gv day vi trung thoi gian
        for (int i = 0; i < N - 1; i++) {
            for (int j = i + 1; j < N; j++) {
                if (conflict[i][j] == 1) {
                    for (int k = 0; k < M; k++) {
                        MPConstraint c = solver.makeConstraint(0, 1);
                        c.setCoefficient(x[i][k], 1);
                        c.setCoefficient(x[j][k], 1);
                    }
                }
            }
        }

        // tong moi hang trong x bang 1 : moi lop co duy nhat 1 gv
        for (int i = 0; i < N; i++) {
            MPConstraint c = solver.makeConstraint(1, 1);
            for (int j = 0; j < M; j++) {
                c.setCoefficient(x[i][j], 1);
            }
        }

        //tinh load
        for (int j = 0; j < M; j++) {
            MPConstraint c = solver.makeConstraint(0, 0);
            for (int i = 0; i < N; i++) {
                c.setCoefficient(x[i][j], credits[i]);
            }
            c.setCoefficient(load[j], -1);
        }



        // tong tin chi moi giao vien khong duoc vuot qua y
        for (int j = 0; j < M; j++) {
            MPConstraint c = solver.makeConstraint(0, totalCredits);
            c.setCoefficient(load[j], -1);
            c.setCoefficient(y, 1);

        }


        MPObjective obj = solver.objective();
        obj.setCoefficient(y, 1);
        obj.setMinimization();

        MPSolver.ResultStatus rs = solver.solve();
        if (rs != MPSolver.ResultStatus.OPTIMAL) {
            System.out.println("can not find optimal solution!");
        } else {
            System.out.println("optimal objective value = " + obj.value());
            for (int j = 0; j < M; j++) {
                System.out.println("teacher " + j + ": ");
                for (int i = 0; i < N; i++) {
                    if (x[i][j].solutionValue() == 1) {
                        System.out.println(i + " ,");
                    }
                }
                System.out.println(" load = " + load[j].solutionValue());
            }
        }

    }


    public static void main(String[] args) {
        BCA app = new BCA();
        app.solve();
    }

}
