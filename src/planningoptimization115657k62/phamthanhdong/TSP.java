package planningoptimization115657k62.phamthanhdong;

//có lẽ nên chú ý hơn !
//lưu ý nhớ cài đặt TSPDynSEC và SubGenerator !!!

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import java.util.*;

public class TSP {
	static {
		System.loadLibrary("jniortools");
	}
	int N = 5;
	int[][] c = { 
			{ 0, 4, 2, 5, 6 }, 
			{ 2, 0, 5, 2, 7 }, 
			{ 1, 2, 0, 6, 3 }, 
			{ 7, 5, 8, 0, 3 }, 
			{ 1, 2, 4, 3, 0 } 
			};
	double inf = Double.POSITIVE_INFINITY;
	MPSolver solver;
	MPVariable[][] x;

	public void solve() {
		if (N > 10) {
			System.out.println("N = 10 is too high, please use solveDynamicAddSubTourConstraint instead");
			System.out.println("N  = 10 quá lớn !!!");
			return;
		}
		solver = new MPSolver("TSP solver", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		x = new MPVariable[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i != j) {
					x[i][j] = solver.makeIntVar(0, 1, "x" + i + "," + j + "]");
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
				if (i != j) {
					c1.setCoefficient(x[i][j], 1);
				}
			}
			MPConstraint c2 = solver.makeConstraint(1, 1);
			for (int j = 0; j < N; j++) {
				c2.setCoefficient(x[j][i], 1);
			}
		}
		SubSetGenerator generator = new SubSetGenerator(N);
		HashSet<Integer> S = generator.first();
		while (S != null) {
			if (S.size() > 1 && S.size() < N) // don't care about = 1 and =n
			{
				MPConstraint sec = solver.makeConstraint(0, S.size() - 1);
				for (int i : S) {
					for (int j : S) {
						if (i != j) {
							sec.setCoefficient(x[i][j], 1);
						}
					}
				}
			}
			S = generator.next();
		}
		final MPSolver.ResultStatus resultStatus = solver.solve();
		if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
			System.out.println("do not find  optimal solution - khong the dua ra optimal ! ");
			return; 
		}
		System.out.println("time for this computation: " + solver.wallTime() + " miliseconds");
		System.out.println("optimal value: " + solver.objective().value());
//        SubSetGenerator generator
	}

	public int[][] genTestData(int N) {
		int[][] result = new int[N][N];
		Random rand = new Random();
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i == j) {
					result[i][j] = 0;
				} else {
					result[i][j] = rand.nextInt(1000);
				}
			}
		}
		return result;
	}

	public void setC(int N) {
		this.N = N;
		this.c = genTestData(N);
		System.out.println("c size: " + this.c.length);
		for (int i = 0; i < this.c.length; i++) {
			for (int j = 0; j < this.c.length; j++) {
				System.out.print(this.c[i][j] + "   ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		TSP app = new TSP();
		app.setC(7);
		app.solve();
	}
}
