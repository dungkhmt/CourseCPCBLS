package planningoptimization115657k62.tranthidinh;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class BCA {
    static {
        System.loadLibrary("jniortools");
    }
    //input data
    int M = 3; //so giao vien
    int N = 13; //so mon hoc
    int[][] coTheDayLop = {{1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0},
            {1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0},
            {0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1},};
    int[] soTinChi = {3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4};
    int[][] trungTiet = {{0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
            {1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
            {1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
            {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
            {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}};
    public void solve()
    {
        MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        MPVariable [][] x = new MPVariable[N][M];
        for(int i =0; i<N; i++)
            for(int j = 0; j<M; j++)
                x[i][j] = solver.makeIntVar(0,1,"x["+ i+ ","+j+"]");
        MPVariable[] load = new MPVariable[M];
        int tongTinChi = 0;
        for (int i =0 ; i< soTinChi.length; i++)
            tongTinChi += soTinChi[i];

        for (int i = 0; i<M; i++)
            load[i] = solver.makeIntVar(0,tongTinChi, "load["+ i +"]");
        MPVariable y = solver.makeIntVar(0,tongTinChi, "y" );

        for(int i = 0; i<N; i++)
            for(int j=0; j<M; j++ )
                if (coTheDayLop[j][i] == 0)
                {
                    MPConstraint c = solver.makeConstraint(0,0);
                    c.setCoefficient(x[i][j],1);
                }
        for (int i =0; i<N; i++)
            for(int j = 0; j <N; j++)
                if (trungTiet[i][j] == 1)
                {
                    for (int k = 0; k<M; k++)
                    {
                        MPConstraint c = solver.makeConstraint(0, 1);
                        c.setCoefficient(x[i][k],1);
                        c.setCoefficient(x[j][k],1);

                    }
                }

        for(int i = 0; i<N; i++)
        {
            MPConstraint c = solver.makeConstraint(1,1);
            for( int j = 0; j<M; j++)
                c.setCoefficient(x[i][j], 1);
        }

        for(int i = 0; i <M; i++)
        {
            MPConstraint c = solver.makeConstraint(0,0);
            for(int j = 0; j < N; j++)
                c.setCoefficient(x[j][i], soTinChi[j]);
            c.setCoefficient(load[i], -1);
        }

        for (int i =0; i <M; i++)
        {
            MPConstraint c = solver.makeConstraint(0, tongTinChi);
            c.setCoefficient(load[i], -1);
            c.setCoefficient(y, 1);
        }

        MPObjective obj = solver.objective();
        obj.setCoefficient(y,1);
        obj.setMinimization();
        MPSolver.ResultStatus rs = solver.solve();
        if (rs != MPSolver.ResultStatus.OPTIMAL)
        {
            System.out.println("Khong tim thay giai phap toi uu cho bai toan");
        } else
        {
            System.out.println("obj =" + obj.value());
            for (int i = 0 ; i <= N; i++)
            {
                System.out.print("giao vien" + i + ":");
                for (int j = 0 ; j <N; j++)
                    if (x[j][i].solutionValue() == 1)
                        System.out.print(j + " ");
                System.out.println(", load = " + load[i].solutionValue());
            }
        }
    }
    public static void main(String[] args)
    {
        BCA BCA_problem = new BCA();
        BCA_problem.solve();
    }

}

