import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class TeachingSchedule {
    static{
        System.loadLibrary("jniortools");
    }
    int M = 3;
    int N = 13;
    int[][] teachClass = { { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0 },
                            { 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
                            { 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1 }, };

    int[] credits = { 3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4 };

    int[][] conflicts = { { 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
                        { 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 },
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

    public void solve()
    {
        MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

        MPVariable[][] x = new MPVariable[N][M];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "x["+i+","+j+"]");
            }
        }

        MPVariable[] load = new MPVariable[M];
        int totalCredits = 0;

        for (int i = 0; i < credits.length; i++) {
            totalCredits += credits[i];
        }

        for (int i = 0; i < M; i++) {
            load[i] = solver.makeIntVar(0, totalCredits, "load["+i+"]");
        }

        MPVariable y = solver.makeIntVar(0, totalCredits, "y"); // số tín chỉ gd của gv dạy nhiều nhất

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < M; j++) {
                if(teachClass[j][i]==0)
                {
                    MPConstraint c = solver.makeConstraint(0, 0);
                    c.setCoefficient(x[i][j], 1);
                }
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if(conflicts[i][j] == 1)
                {
                    for (int k = 0; k < M; k++) {
                        MPConstraint c = solver.makeConstraint(0, 1);
                        c.setCoefficient(x[i][k], 1);
                        c.setCoefficient(x[j][k], 1);
                    }
                }
            }
        }

        for (int i = 0; i < N; i++) { // 1 môn chỉ 1 giáo viên dạy
            MPConstraint c = solver.makeConstraint(1, 1);
            for (int j = 0; j < M; j++) {
                c.setCoefficient(x[i][j], 1);
            }
        }

        for (int i = 0; i < M; i++) { // load[i] là số tín chỉ giáo viên i dạy
            MPConstraint c = solver.makeConstraint(0, 0);
            for (int j = 0; j < N; j++) {
                c.setCoefficient(x[j][i], credits[j]);
            }
            c.setCoefficient(load[i], -1);
        }

        for (int i = 0; i < M; i++) {
            MPConstraint c = solver.makeConstraint(0, totalCredits);  // nhỏ hơn tổng số tín chỉ
            c.setCoefficient(load[i], -1);
            c.setCoefficient(y, 1);
        }
        MPObjective objective = solver.objective();
        objective.setCoefficient(y, 1);
        objective.setMinimization();
        MPSolver.ResultStatus rs = solver.solve();

        if(rs != MPSolver.ResultStatus.OPTIMAL)
        {
            System.out.println("can't not find optimal solution");
        }
        else
        {
            System.out.println("obj = " + objective.value());
            for (int i = 0; i < M; i++) {
                System.out.println("teacher "+i+":" );
                for (int j = 0; j < N; j++) {
                    if(x[j][i].solutionValue() ==1)
                    {
                        System.out.println(j + " ");
                    }
                }
                System.out.println("load = "+load[i].solutionValue());
            }
        }

    }

    public static void main(String[] args) {
        TeachingSchedule ts = new TeachingSchedule();
        ts.solve();
    }

}

