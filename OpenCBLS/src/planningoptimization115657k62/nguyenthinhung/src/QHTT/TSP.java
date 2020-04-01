import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class TSP {
	static {
		System.loadLibrary("jniortools");
	}
	int n = 5;
	int [][] c = new int[][] {{0,4,2,5,6},
							  {2,0,5,2,7},
							  {1,2,0,6,3},
							  {7,5,8,0,3},
							  {1,2,4,3,0}};
	public void solve() {
		double INF = java.lang.Double.POSITIVE_INFINITY;
		MPSolver solver = new MPSolver("TSP", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		MPVariable[][] x = new MPVariable[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					x[i][j] = solver.makeIntVar(0, 1, "x["+i+"]["+j+"]");
				}
			}
		}
								
		for (int i = 0; i < n; i++) {
			MPConstraint c = solver.makeConstraint(1,1);
			for (int j = 0; j < n; j++) {
				if (i != j) {
					c.setCoefficient(x[i][j], 1);
				}
			}
		}
		
		SubSetGenerator gen = new SubSetGenerator(n);
		HashSet<Integer> s = gen.first();
		while (s != null) {
			if (s.size() > 1 && s.size() < n) {
				MPConstraint c = solver.makeConstraint(0,  s.size()-1);
				for (int i: s) {
					for (int j : s) {
						if (i != j) {
							c.setCoefficient(x[i][j], 1);
						}
					}
				}
			}
			s = gen.next();
		}
		
		MPObjective obj = solver.objective();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					obj.setCoefficient(x[i][j], c[i][j]);
				}				
			}			
		}
		
		obj.setMinimization();
		ResultStatus rs = solver.solve();
		if (rs != ResultStatus.OPTIMAL) {
			System.out.println("Not find optimal solution");
		} else {
			System.out.println("obj = " + obj.value());
			int j = 0;
			for (int k = 0; k < n; k++) {
				for (int i = 0; i < n; i++) {
					if (i != j) {
						if (x[j][i].solutionValue() == 1) {
							System.out.print(j + " ");
							j = i;
							break;
						}
					}
				}
				
			}
		}
	}
	public static void main(String[] args) {
		TSP app = new TSP();
		app.solve();
	}
}