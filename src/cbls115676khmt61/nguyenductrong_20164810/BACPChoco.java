package cbls115676khmt61.nguyenductrong_20164810;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BACPChoco {

	Model model;
	int alpha = 2;
	int beta = 4;
	int gamma = 7;
	int lambda = 3;
	IntVar[][] X; // X[j][i]: mon i hoc vao ki j
	IntVar[] X2; // X2[i] = k: mon i hoc vao ki k
	int N = 9;
	int P = 4;
	int[] credit = {3,2,2,1,3,3,1,2,2};
	int[] I = {0,0,1,2,3,4,3};
	int[] J = {1,2,3,5,6,7,8};

	public void stateModel() {
		model = new Model("BACP");
		X = new IntVar[P][N];
		X2 = new IntVar[N];

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < P; j++) {
				X[j][i] = model.intVar("X[" + j + "][" + i + "]", 0, 1);
			}
			X2[i] = model.intVar("X2[" + i + "]", 0, P - 1);
		}
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < P; j++) {
				model.ifThen(model.arithm(X[j][i], "=", 1), model.arithm(X2[i], "=", j));
				model.ifThen(model.arithm(X2[i], "=", j), model.arithm(X[j][i], "=", 1));
				model.ifThen(model.arithm(X[j][i], "=", 0), model.arithm(X2[i], "!=", j));
				model.ifThen(model.arithm(X2[i], "!=", j), model.arithm(X[j][i], "=", 0));
			}
		}

		int M = I.length;
		for (int i = 0; i < M; i++) {
			model.arithm(X2[I[i]], "<", X2[J[i]]).post();
		}

		int[] oneN = new int[N];
		for (int i = 0; i < N; i++)
			oneN[i] = 1;

		for (int i = 0; i < P; i++) {
			model.scalar(X[i], oneN, ">=", alpha).post();
			model.scalar(X[i], oneN, "<=", beta).post();

			model.scalar(X[i], credit, ">=", lambda).post();
			model.scalar(X[i], credit, "<=", gamma).post();
		}
	}

	public void search() {
		model.getSolver().solve();
		for (int j = 0; j < P; j++) {
			System.out.print("- Semester " + j + ": \n");
			for (int i = 0; i < N; i++)
				if (X2[i].getValue() == j) {
					System.out.print("[course " + i + ", credit = " + credit[i] + "] ");
					System.out.println();
				}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BACPChoco bacp = new BACPChoco();
		bacp.stateModel();
		bacp.search();
	}
}
