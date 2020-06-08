//package planningoptimization115657k62.NguyenLC;

import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class MiniProject_ortool {

	static {
		System.loadLibrary("jniortools");
	}

	int k = 4; // so nhan vien
	int n = 7; // so khach hang
	int[] d = { 0, 5, 6, 8, 2, 4, 6, 9 }; // thoi gian bao tri
	
	int[][] t = { { 0, 3, 6, 6, 4, 3, 5, 6 }, // thoi gian di chuyen
			      { 3, 0, 5, 4, 2, 6, 2, 1 }, 
			      { 6, 5, 0, 4, 9, 1, 9, 4 }, 
			      { 6, 4, 4, 0, 7, 2, 6, 4 },
			      { 4, 2, 9, 7, 0, 9, 4, 25 }, 
			      { 3, 6, 1, 2, 9, 0, 6, 4 }, 
			      { 5, 2, 9, 6, 4, 6, 0, 3 },
			      { 6, 1, 4, 4, 25, 4, 3, 0 } };

	public void solve() {

		MPSolver solver = new MPSolver("Project", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		MPVariable[][][] x = new MPVariable[n + 1][n + 1][k + 1];

		for (int i = 0; i <= n; i++)
			for (int j = 1; j <= n; j++)
				for (int u = 1; u <= k; u++)
					x[i][j][u] = solver.makeIntVar(0, 1, "x[" + i + "," + j + "," + u + "]");

		// Moi khach hanh chi duoc bao tri boi 1 nhan vien
		for (int i = 1; i <= n; i++) {
			MPConstraint c = solver.makeConstraint(1, 1);
			for (int j = 0; j <= n; j++) {
				for (int u = 1; u <= k; u++)
					if (i != j)
						c.setCoefficient(x[j][i][u], 1);
			}
		}

		// Nhan vien co the di bao tri tiep hoac khong
		for (int i = 1; i <= n; i++) {
			MPConstraint c = solver.makeConstraint(0, 1);
			for (int j = 1; j <= n; j++) {
				for (int u = 1; u <= k; u++)
					if (i != j)
						c.setCoefficient(x[i][j][u], 1);
			}
		}

		// Moi nhan vien bat dau di lam tu cong ty
		for (int u = 1; u <= k; u++) {
			MPConstraint c = solver.makeConstraint(1, 1);
			for (int j = 1; j <= n; j++) {
				c.setCoefficient(x[0][j][u], 1);
			}
		}

		// Khong tao chu trinh con cho moi nhan vien
		for (int u = 1; u <= k; u++) {
			SubSetGenerator generator = new SubSetGenerator(n + 1);
			HashSet<Integer> S = generator.first();
			while (S != null) {
				if (S.size() > 1 && S.size() <= n) {
					MPConstraint s = solver.makeConstraint(0, S.size() - 1);
					for (int i : S) {
						for (int j : S)
							if (i != j) {
								s.setCoefficient(x[i][j][u], 1);
							}
					}
				}
				S = generator.next();
			}
		}

		// Moi nhan vien co lo trinh di lam la lien tiep
		for (int u = 1; u <= k; u++)
			for (int i = 1; i <= n; i++)
				for (int j = 1; j <= n; j++)
					if (i != j) {
						MPConstraint c = solver.makeConstraint(-1, 0);
						for (int v = 0; v <= n; v++)
							c.setCoefficient(x[v][i][u], -1);

						c.setCoefficient(x[i][j][u], 1);
					}

		int totalTimes = 0;

		for (int i = 0; i <= n; i++)
			for (int j = 0; j <= n; j++)
				totalTimes += t[i][j];

		totalTimes /= 2;

		for (int i = 1; i <= n; i++)
			totalTimes += d[i];

		MPVariable[] y = new MPVariable[k + 1];
		for (int i = 0; i <= k; i++)
			y[i] = solver.makeIntVar(0, totalTimes, "y[" + i + "]");

		MPVariable Y = solver.makeIntVar(0, totalTimes, "Y");

		for (int u = 1; u <= k; u++) {
			MPConstraint c = solver.makeConstraint(0, 0);
			for (int i = 0; i <= n; i++)
				for (int j = 1; j <= n; j++) {
					c.setCoefficient(x[i][j][u], (t[i][j] + d[j]));

				}
			c.setCoefficient(y[u], -1);
		}

		for (int i = 1; i <= k; i++) {
			MPConstraint c = solver.makeConstraint(0, totalTimes);
			c.setCoefficient(y[i], -1);
			c.setCoefficient(Y, 1);
		}

		MPObjective obj = solver.objective();
		obj.setCoefficient(Y, 1);
		obj.setMinimization();
		ResultStatus rs = solver.solve();

		for (int u = 1; u <= k; u++) {
			System.out.print("Nhan vien " + u + " bao tri khach hang: ");
			for (int i = 0; i <= n; i++) {
				for (int j = 1; j <= n; j++)
					if (x[i][j][u].solutionValue() == 1)
						System.out.print("( " + i + ", " + j + " )");
			}
			System.out.println();
			System.out.println("Tong thoi gian lam viec = " + y[u].solutionValue());

		}
		System.out.print("Thoi lam lam viec lon nhat cua 1 nhan vien la: " + Y.solutionValue());
	}

	public static void main(String[] args) {
		MiniProject_ortool app = new MiniProject_ortool();
		app.solve();
	}
}
