package toiuu;

import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class TSP {
	static {
		System.loadLibrary("jniortools");
	}
	int N = 5;
	int [][] c = {
			{0, 4, 2, 5, 6},
			{2, 0, 5, 2, 7},
			{1, 2, 0, 6, 3},
			{7, 5, 8, 0, 3},
			{1, 2, 4, 3, 0}
	};
	double inf = java.lang.Double.POSITIVE_INFINITY;
	MPSolver solver;
	MPVariable[][] X;
	
	public class SubSetGenerator {
		int N;
		int[] X;
		public SubSetGenerator(int N) {
			this.N = N;
		}
		public HashSet<Integer> first(){
			X = new int[N];
			for (int i = 0; i < N; i++)
				X[i] = 0;
			HashSet<Integer> S = new HashSet<Integer>();
			for (int i = 0; i < N; i++)
				if (X[i] == 1)
					S.add(i);
			return S;
		}
		
		public HashSet<Integer> next(){
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
	
	
	public void solve() {
		if(N > 10) {
			System.out.println("N = 10 is too high to apply this solve method, use solveDynamicAddSubTourConstraint");
			return;
		}
		solver = new MPSolver("TSP solver", MPSolver.OptimizationProblemType.valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
		
		X = new MPVariable[N][N];
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if(i != j)
					X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "," + j + "]");
		
		MPObjective obj = solver.objective();
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				obj.setCoefficient(X[i][j], c[i][j]);
		
		
		for (int i = 0; i < N; i++) {
			
			MPConstraint fc1 = solver.makeConstraint(1, 1);
			for (int j = 0; j < N; j++)
				if (j != i) {
					fc1.setCoefficient(X[i][j], 1);
				}
			
			
			MPConstraint fc2 = solver.makeConstraint(1,1);
			for (int j = 0; j < N; j++)
				if (j != i) {
					fc2.setCoefficient(X[j][i], 1);
				}
		}
		
		
		SubSetGenerator generator = new SubSetGenerator(N);
		HashSet<Integer> S = generator.first();
		while(S != null) {
			if(S.size() > 1 && S.size() < N) {
				MPConstraint sc = solver.makeConstraint(0, S.size() - 1);
				for (int i: S) {
					for (int j: S)
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
		System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
		// The objective value of the solution.
		System.out. println("Optimal objective value = " + solver.objective().value());
		
	}
		
	public static void main(String[] args) {
		TSP app = new TSP();
		app.solve();
	}
}
