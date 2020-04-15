package planningoptimization115657k62.NguyenVanTien;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class phan_cong_giang_day {
	
	static {
		System.loadLibrary("jniortools");
	}
	int M = 3;    // #classes
	int N = 13;   // #teachers
	
	// teacher i teaches class j
	int[][] teachClass = {
		{1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0},
		{1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0},
		{0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1}
	};
	
	// #credits of each class
	int[] credits = {3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4};
	
	// Conflict classes
	int[][] conflict = {
		{0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
		{1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
		{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0},
		{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
		{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1},
		{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
		{0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0}
	};
	
	public void solve() {
		double INF = java.lang.Double.POSITIVE_INFINITY;
		MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		
		MPVariable[][] x = new MPVariable[M][N];   // teacher i teaches class j
		// range value for x[i][j]: 0 or 1 
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				x[i][j] = solver.makeIntVar(0, 1, "x[" + i + ","  + j + "]");
			}
		}
		
		// if teacher i does not teach class j => x[i][j] = 0
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				if (teachClass[i][j] == 0) {
					MPConstraint c = solver.makeConstraint(0, 0);
					c.setCoefficient(x[i][j], 1);
				}
			}
		}
		
		// one class can only be taught by 1 teacher: sum of values in one column is 1
		for (int j = 0 ; j < N; j++) {
			MPConstraint c = solver.makeConstraint(1, 1);
			for (int i = 0; i < M; i++) {
				c.setCoefficient(x[i][j], 1);
			}
		}
		
		// one teacher cannot teach 2 conflicting classes: x[i][j1] + x[i][j2] <= 1
		for (int j1 = 0; j1 < N; j1++) {
			for (int j2 = 0; j2 < N; j2++) {
				if (conflict[j1][j2] == 1) {
					for (int i = 0; i < M; i++) {
						MPConstraint c = solver.makeConstraint(0, 1);
						c.setCoefficient(x[i][j1], 1);
						c.setCoefficient(x[i][j2], 1);
					}
				}
			}
		}
		
		// total credits of all classes teacher i teaches
		MPVariable[] load = new MPVariable[M]; 
		int maxTotalCredits = 0;  // used for y
		for (int i = 0; i < M; i++) {
			int totalCredits = 0;
			for (int j = 0; j < N; j++) {
				if (teachClass[i][j] == 1) totalCredits += credits[j];
			}
			maxTotalCredits = Math.max(maxTotalCredits, totalCredits);
			load[i] = solver.makeIntVar(0, totalCredits, "load[" + i + "]");
			
		}
		
		for (int i = 0; i < M; i++) {
			MPConstraint c = solver.makeConstraint(0, 0);
			for (int j = 0; j < N; j++) {
				c.setCoefficient(x[i][j], credits[j]);
			}
			c.setCoefficient(load[i], -1);
		}
		
		// y: maximum value of total credits one teacher has
		MPVariable y = solver.makeIntVar(0,	maxTotalCredits, "y");
		for (int i = 0; i < M; i++) {
			MPConstraint c = solver.makeConstraint(-INF, 0);
			for (int j = 0; j < N; j++) {
				c.setCoefficient(x[i][j], credits[j]);
			}
			c.setCoefficient(y, -1);
		}
		
		MPObjective obj = solver.objective();
		obj.setCoefficient(y, 1);
		obj.setMinimization();
		ResultStatus res = solver.solve();
		if (res != ResultStatus.OPTIMAL) {
			System.out.println("Cannot find solution!");
		}
		else {
			System.out.println("min = " + obj.value());
			for (int i = 0; i < M; i++) {
				System.out.println("teacher " + i + ":");
				for (int j =0; j < N; j++) {
					if (x[i][j].solutionValue() == 1) {
						System.out.println(j + " ");
					}
				}
		//		System.out.println(",  load: " + load[i]);
			}
		}
		
	}
	
	public static void main(String[] args) {
		phan_cong_giang_day BCA = new phan_cong_giang_day();
		BCA.solve();
	}

}
