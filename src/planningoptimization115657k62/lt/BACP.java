package planning_optimization_lam.lt;

import org.chocosolver.solver.*;
import org.chocosolver.solver.variables.IntVar;

public class BACP {
	public static void main(String[] args) {
// 1. Create a model
		Model model = new Model("Balanced Academic Curriculum Problem");
// 2. Create number of semesters and number of subjects  
		int P = 4;
		int N = 12;
// 3. Create credit of subjects
		int[] credits = { 3, 2, 2, 1, 3, 3, 1, 2, 2 };
// 4. Create alpha, beta, lambda, gamma
		int alpha = 2, beta = 4, lamda = 3, gamma = 7;
// 5. Prerequisites
		int[] I = { 0, 0, 1, 2, 3, 4, 3 };
		int[] J = { 1, 2, 3, 5, 6, 7, 8 };
// 6. Coefficients
		int[] oneN = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
		int[] oneP = { 1, 1, 1, 1 };
// 7. 
		IntVar[][] x = new IntVar[P][N];

		for (int j = 0; j < P; j++)
			for (int i = 0; i < N; i++)
				x[j][i] = model.intVar("x[" + j + "," + i + "]", 0, 1);
		for (int i = 0; i < N; i++) {
			IntVar[] y = new IntVar[P];
			for (int j = 0; j < P; j++)
				y[j] = x[j][i];
			model.scalar(y, oneP, "=", 1).post();

		}

		for (int k = 0; k < I.length; k++) {
			int i = I[k];
			int j = J[k];
			for (int q = 0; q < P; q++)
				for (int p = 0; p <= q; p++)
					model.ifThen(model.arithm(x[q][i], "=", 1), model.arithm(x[p][j], "=", 0));
		}
		model.getSolver().solve();
		for (int j = 0; j < P; j++) {
			System.out.print("semester " + j + ": ");
			for (int i = 0; i < N; i++)
				if (x[j][i].getValue() == 1) {
					System.out.print("[course " + i + ", credit " + credits[i] + "] ");
					System.out.println();
				}
		}
	}
}
