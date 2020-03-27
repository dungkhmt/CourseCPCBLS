package planningoptimization115657k62.hoangthanhlam;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class BCA {
	
	static {
		System.loadLibrary("jniortools");
	}
	
	static int M = 3;
	static int N = 13;
	static int[][] teaches = {
			{1,0,1,1,0,1,0,0,0,1,0,1,0},
			{1,1,0,1,0,1,1,1,1,0,0,0,0},
			{0,1,1,1,0,0,0,1,0,1,0,1,1},
	};
	
	static int[][] flick = {
			{0,0,1,0,1,0,0,0,1,0,0,0,0},
			{0,0,0,0,1,0,0,0,0,0,1,0,0},
			{1,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,1,0,1,0,0,0},
			{1,1,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,0,0,0,0,0,0,1,1},
			{0,0,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,1,0,0,0,0,1,0,0,0,1},
			{1,0,0,0,0,0,1,0,0,0,0,0,0},
			{0,0,0,1,0,0,0,0,0,0,0,0,0},
			{0,1,0,0,0,0,0,0,0,0,0,0,0},
			{0,0,0,0,0,1,0,0,0,0,0,0,0},
			{0,0,0,0,0,1,1,0,0,0,0,0,0},
	};
	
	static int[] c = {3,3,4,3,4,3,3,3,4,3,3,4,4};
	
	public static void main (String[] args) throws Exception {

		int totalC = 0;
		for (int i = 0; i < N; i++) {
			totalC += c[i];
		}
		
		MPSolver solver = new MPSolver("BCTA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		
		MPVariable X[][] = new MPVariable[N][M];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < M; j++) {
				if (teaches[j][i] == 1) {
					X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "][" + j + "]");
				} else {
					X[i][j] = solver.makeIntVar(0, 0, "X[" + i + "][" + j + "]");
				}
			}
		}
		MPVariable y = solver.makeIntVar(0, totalC, "y");
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (flick[i][j] == 1) {
					for (int k = 0; k < M; k++) {
						MPConstraint c = solver.makeConstraint(0, 1);
						c.setCoefficient(X[i][k], 1);
						c.setCoefficient(X[j][k], 1);
					}
				}
			}
		}
		
		for (int i = 0; i < N; i++) {
			MPConstraint c = solver.makeConstraint(1, 1);
			for (int j = 0; i < N; j++) {
				c.setCoefficient(X[i][j], 1);
			}
		}
		
		for (int k = 0; k < M; k++) {
			MPConstraint ct = solver.makeConstraint(0, totalC);
			ct.setCoefficient(y, 1);
			for (int i = 0; i < N; i++) {
				int l = - c[i];
				ct.setCoefficient(X[i][k], l);
			}
		}
		
		MPObjective obj = solver.objective();
		obj.setCoefficient(y, 1);
		obj.setMinimization();
		
		ResultStatus rs = solver.solve();
		if (rs != ResultStatus.OPTIMAL) {
			System.out.println("No Solution !!!");
		} else {
			System.out.println("obj = " + obj.value());
			for (int i = 0; i < M; i++) {
				System.out.print("Teacher " + i + ": ");
				int load = 0;
				for (int j = 0; j < M; j++) {
					if (X[j][i].solutionValue() == 1) {
						System.out.print(j + " ");
						load += c[j];
					}
				}
				System.out.println(", load = " + load);
			}
		}
	}
}
