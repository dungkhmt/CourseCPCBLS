package chocoSolver;

import org.chocosolver.solver.*;
import org.chocosolver.solver.variables.IntVar;

public class Bai1 {

	int N = 20;
	int P = 5;
	int[] capa = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30,
			40, 25, 35, 10, 10 };

	int[] C = { 60, 70, 90, 80, 100 };
	int[] I = { 0, 7, 12, 8, 1, 1, 2, 0, 0, 9 };
	int[] J = { 1, 8, 17, 9, 2, 9, 9, 9, 12, 12 };

	int[] oneN = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	int[] oneP = { 1, 1, 1, 1, 1 };

	public void solve() {
		Model model = new Model("TA");
		IntVar[][] x = new IntVar[P][N];

		for (int j = 0; j < P; j++) {
			for (int i = 0; i < N; i++) {
				x[j][i] = model.intVar("x[" + j + "],[" + i + "]", 0, 1);

			}
		}
		// for (int p = 0; p < P; p++) {
		// model.scalar(x[p], oneP, "<=", C[p]).post();
		// }
		for (int j = 0; j < P; j++) {
			model.scalar(x[j], capa, "<=", C[j]).post();
		}

		for (int i = 0; i < N; i++) {
			IntVar[] y = new IntVar[P];
			for (int j = 0; j < P; j++) {
				y[j] = x[j][i];
			}
			model.scalar(y, oneP, "=", 1).post();
		}
		for (int k = 0; k < I.length; k++) {
			int i = I[k];
			int j = J[k];
			for (int p = 0; p < P; p++) {
				model.ifThen(model.arithm(x[p][i], "=", 1),
						model.arithm(x[p][j], "=", 0));

			}
		}
		model.getSolver().solve();
		for (int j = 0; j < P; j++) {
			System.out.println("Thung " + j + ": ");
			int s = 0;
			for (int i = 0; i < N; i++) {
				if (x[j][i].getValue() == 1) {
					s += capa[i];
					System.out.println("[chat long " + i + ", the tich "
							+ capa[i] + "]");
				}
			}
			System.out.println("Tong suc chua: " + s);
			System.out.println();
		}

	}

	public static void main(String[] args) {
		Bai1 ba = new Bai1();
		ba.solve();
	}
}
