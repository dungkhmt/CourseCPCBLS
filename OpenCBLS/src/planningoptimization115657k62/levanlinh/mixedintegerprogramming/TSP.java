package mixedintegerprogramming;

import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPObjective;

public class TSP {
	static {
		System.loadLibrary("jniortools");
	}
	
	int N = 11;
	int[][] c = { { 0, 29, 20, 21, 16, 31, 100, 12, 4, 31, 18 }, { 29, 0, 15, 29, 28, 40, 72, 21, 29, 41, 12 },
			{ 20, 15, 0, 15, 14, 25, 81, 9, 23, 27, 13 }, { 21, 29, 15, 0, 4, 12, 92, 12, 25, 13, 25 },
			{ 16, 28, 14, 4, 0, 16, 94, 9, 20, 16, 22 }, { 31, 40, 25, 12, 16, 0, 95, 24, 36, 3, 37 },
			{ 100, 72, 81, 92, 94, 95, 0, 90, 101, 99, 84 }, { 12, 21, 9, 12, 9, 24, 90, 0, 15, 25, 13 },
			{ 4, 29, 23, 25, 20, 36, 101, 15, 0, 35, 18 }, { 31, 41, 27, 13, 16, 3, 99, 25, 35, 0, 38 },
			{ 18, 12, 13, 25, 22, 37, 84, 13, 18, 38, 0 } };
	double inf = java.lang.Double.POSITIVE_INFINITY;
	MPSolver solver;
	MPVariable[][] X;
	
	public void solve() {
		solver = new MPSolver("TSP Solver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		
		X = new MPVariable[N][N];
		for (int i = 0; i < N; ++i)
			for (int j = 0;j < N; ++j)
				if (i != j) X[i][j] = solver.makeIntVar(0, 1, "X["+i+","+j+"]");
		
		MPObjective obj = solver.objective();
		for (int i = 0; i < N; ++i)
			for (int j = 0;j < N; ++j)
				if (i != j) obj.setCoefficient(X[i][j], c[i][j]);
		obj.setMinimization();
		
		for (int i = 0; i < N; ++i) {
			MPConstraint c1 = solver.makeConstraint(1,1);
			for (int j = 0;j < N; ++j)
				if (i != j)
					c1.setCoefficient(X[i][j], 1);
			MPConstraint c2 = solver.makeConstraint(1,1);
			for (int j = 0;j < N; ++j)
				if (i != j)
					c2.setCoefficient(X[j][i], 1);
		}
		
		
		
		  // sub-tour elimination constraints 
		
		SubSetGenerator generator = new SubSetGenerator(N);
		HashSet<Integer> S = generator.first();
		while (S != null) {
			if (S.size() > 1 && S.size() < N) {
				MPConstraint sc = solver.makeConstraint(0, S.size() - 1);
				for (int i : S) {
					for (int j : S)
						if (i != j) {
							sc.setCoefficient(X[i][j], 1);
						}
				}
			}
			S = generator.next();
		}
		 
		
		final MPSolver.ResultStatus resultStatus = solver.solve();
		if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
		System.err.println("The problem does not have an optimal solution!");
		return;
		}
		System.out.println("Problem solved in " + solver.wallTime() + "milliseconds");
		// The objective value of the solution.
		System.out.println("Optimal objective value = " + solver.objective().value());
	}

	public static void main(String[] args) {
		TSP app = new TSP();
		app.solve();
	}

}
