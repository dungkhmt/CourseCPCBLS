package teachingassignment;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class TeachingAssignment {
	static {
		System.loadLibrary("jniortools");
	}
	int n = 3; // number of teacher
	int m = 13; // number of class
	int[][] teachClass = { { 1, 0, 1, 0, 1, 0, 0, 0, 1, 0, 1, 0, 0 },
							{ 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
							{ 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1 }, };
	int[] credits = new int[] {3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4};
	int[][] conflict = { { 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
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
	public void solveTA() {
		double INF = java.lang.Double.POSITIVE_INFINITY;
		MPSolver solver = new MPSolver("TeachingAssignment", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		MPVariable[][] x = new MPVariable[n][m];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				x[i][j] = solver.makeIntVar(0, 1, "x[" + i + "," + j + "]");
			}
		}
		MPVariable[] load = new MPVariable[n];
		int totalCredits = 0;
		for (int i = 0; i < credits.length; i++) {
			totalCredits += credits[i];
		}
		for (int i = 0; i < n; i++) {
			load[i] = solver.makeIntVar(0, totalCredits, "load["+i+"]");
		}
		
		MPVariable y = solver.makeIntVar(0, totalCredits,  "y");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (teachClass[i][j] == 0) {
					MPConstraint c = solver.makeConstraint(0,0);
					c.setCoefficient(x[i][j], 1);
				}
			}
		}
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < m; j++) {
				if (conflict[i][j] == 1) {
					for (int k = 0; k < n; k++) {
						MPConstraint c = solver.makeConstraint(0, 1);
						c.setCoefficient(x[k][i], 1);
						c.setCoefficient(x[k][j], 1);
					}
				}
			}
		}
		for (int i = 0; i < m; i++) {
			MPConstraint c = solver.makeConstraint(1, 1);
			for (int j = 0; j < n; j++) {
				c.setCoefficient(x[j][i], 1);
			}
		}
		for (int i = 0; i < n; i++) {
			MPConstraint c = solver.makeConstraint(0, 0);
			for (int j = 0; j < m; j++) {
				c.setCoefficient(x[i][j], credits[j]);
			}
			c.setCoefficient(load[i], -1);
		}
		
		for (int i = 0; i < n; i++) {
			MPConstraint c = solver.makeConstraint(-INF, 0);
			c.setCoefficient(load[i], 1);
			c.setCoefficient(y, -1);
		}
		MPObjective obj = solver.objective();
		obj.setCoefficient(y, 1);
		obj.setMinimization();
		ResultStatus rs = solver.solve();
		if (rs != ResultStatus.OPTIMAL) {
			System.out.println("solution not found");
		} else {
			System.out.println("obj = " + obj.value());
			for (int i = 0; i < n; i++) {
				System.out.println("Teacher " + i + ":");
				for (int j = 0; j < m; j++) {
					if (x[i][j].solutionValue() == 1) {
						System.out.print(j + " ");
					}
				}
				System.out.println("Load = " + load[i].solutionValue());
			}
		}
	}
	public static void main(String[] args) {
		TeachingAssignment app = new TeachingAssignment();
		app.solveTA();
	}
}
