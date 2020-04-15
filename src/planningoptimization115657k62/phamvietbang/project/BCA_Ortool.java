package planningoptimization115657k62.phamvietbang.project;



import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public class BCA_Ortool {
	static {
	    System.loadLibrary("jniortools");
	}


	public static void main(String[] args) {
		double infinity = java.lang.Double.POSITIVE_INFINITY;
		MPSolver solver = new MPSolver("Phanconggiangday", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		int N = 13, M = 3;
		int[][] teachClass = { { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0 },
				{ 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
				{ 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1 }, };
		int[] credits =  { 3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4 };
		int[][] conflict = { { 0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
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
		
		
		MPVariable[][] X = new MPVariable[M][N];
		for (int i = 0; i < M; i++)
			for (int j = 0; j < N; j++)
				X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "][" + j + "]");
		
		MPVariable[] loads = new MPVariable[M];
		int totalsCredits = 0;
		for (int i = 0; i < N; i++)
			totalsCredits+= credits[i];
		
		for (int i = 0; i < loads.length; i++) {
			loads[i] = solver.makeIntVar(0, totalsCredits, "loads" + i);
		}
			
		MPVariable y = solver.makeIntVar(0, totalsCredits, "y");
		
		// X[i,j] = 0 voi moi i = 0,...,N voi moi j khong thuoc T(i)
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++)
				if(teachClass[i][j] == 0) {
					MPConstraint c = solver.makeConstraint(0,0);
					c.setCoefficient(X[i][j], 1);
				}
		}
		
		// X[i1, j) + X[i2, j] <= 1 voi moi (i1, i2) thuoc conflict
		for(int j1 = 0; j1 < N; j1++)
			for(int j2 = 0; j2 < N; j2++)
				if(conflict[j1][j2] == 1) {
					for(int i = 0; i< M; i++) {
						MPConstraint c0 = solver.makeConstraint(0, 1);
						c0.setCoefficient(X[i][j1], 1);
						c0.setCoefficient(X[i][j2], 1);
					}
				}
		
		// Tong (j = 0, M-1) X[i, j] = 1
		for (int j = 0; j < N; j++) {
			MPConstraint c1 = solver.makeConstraint(1, 1);
			for (int i = 0; i < M; i++) {
				c1.setCoefficient(X[i][j], 1);				
			}
		}
		
		//y = max X[i,k]*c(i), k = 0,..M-1
		for (int i = 0; i < M; i++) {
			MPConstraint c2 = solver.makeConstraint(0, 0);
			for (int j = 0; j < N; j++)
				c2.setCoefficient(X[i][j], credits[j]);
			c2.setCoefficient(loads[i], -1);
			//c2.setCoefficient(y, -1);
		}
		for (int i = 0; i < M; i++) {
			MPConstraint c = solver.makeConstraint(0, totalsCredits);
			c.setCoefficient(loads[i], -1);
			c.setCoefficient(y, 1);
			
		}
		
		MPObjective Obj = solver.objective();
		Obj.setCoefficient(y, 1);
		Obj.setMinimization();
		
		ResultStatus rs = solver.solve();
		
		if(rs == ResultStatus.OPTIMAL) {
			System.out.println("Gia tri ham muc tieu: " + Obj.value());
			for (int i = 0; i < M; i++) {
				System.out.print("Giao vien " + i + " day: ");
				for(int j=0; j< N; j++)
					if(X[i][j].solutionValue() == 1)
						System.out.print(j + " ");
				System.out.println("\tSo tiet: " + loads[i].solutionValue());
			}
		}
		else
			System.out.println("Can not solver");
	}
}
