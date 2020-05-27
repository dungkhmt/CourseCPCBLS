
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;

public class Liquid_27_05 {
	int N = 20;
	int M = 5;
	int[] n = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10 };
	int[] m = { 60, 70, 90, 80, 100 };
	int[] h = { 1, 1, 1, 1, 1 };
	int[][] conflict = { { 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0 },
			{ 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			{ 1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 } };

	Model model;
	IntVar[][] x;

	public void buildModel() {
		model = new Model("Liquid");
		x = new IntVar[M][N];

		for (int i = 0; i < M; i++) {
			for (int j = 0; j < N; j++) {
				x[i][j] = model.intVar("x[" + i + "][" + j + "]", 0, 1);
			}
		}

		for (int i = 0; i < M; i++) {
			model.scalar(x[i], n, "<=", m[i]).post();
		}

		for (int i = 0; i < M; i++) {
			for (int k = 0; k < 6; k++) {
				model.scalar(x[i], conflict[k], "<=", 1).post();
			}
		}

		for (int i = 0; i < N; i++) {
			IntVar[] y = new IntVar[M];
			for (int j = 0; j < M; j++) {
				y[j] = x[j][i];
			}
			model.scalar(y, h, "=", 1).post();
		}
	}

	public void solve() {
		Solver s = model.getSolver();

		if (s.solve()) {
			for (int i = 0; i < M; i++) {
				System.out.print("Thung " + i + " chua: ");
				for (int j = 0; j < N; j++) {
					if (x[i][j].getValue() == 1) {
						System.out.print(j + "  ");
					}
				}
				System.out.println();
			}
		}
	}

	public static void main(String[] args) {
		Liquid_27_05 app = new Liquid_27_05();
		app.buildModel();
		app.solve();
	}
}