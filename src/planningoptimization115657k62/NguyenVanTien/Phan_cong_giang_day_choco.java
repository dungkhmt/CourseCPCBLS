package planningoptimization115657k62.NguyenVanTien;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Phan_cong_giang_day_choco {

	int N = 9;// number of courses: 0,1,2,...,8
	int P = 4;// number of semesters: 0,1,2,3
	int[] credits = { 3, 2, 2, 1, 3, 3, 1, 2, 2 };
	int alpha = 2;
	int beta = 4;
	int lamda = 3;
	int gamma = 7;
	int[] I = { 0, 0, 1, 2, 3, 4, 3 };
	int[] J = { 1, 2, 3, 5, 6, 7, 8 };// prerequisites

	int[] oneN = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	int[] oneP = { 1, 1, 1, 1 };

	public void solver() {
		Model model = new Model("BACP");
		IntVar[][] x = new IntVar[P][N];// x[j][i] = 1 indicates that course i is assigned
										// to semester j
		for (int j = 0; j < P; j++)
			for (int i = 0; i < N; i++)
				x[j][i] = model.intVar("x[" + j + "," + i + "]", 0, 1);
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

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Phan_cong_giang_day_choco app = new Phan_cong_giang_day_choco();
		app.solver();
	}

}
