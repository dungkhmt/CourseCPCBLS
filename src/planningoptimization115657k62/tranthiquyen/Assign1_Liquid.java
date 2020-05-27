package example;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.*;

public class Assign1_Liquid {
	
	//data
	int N = 20;
	int P = 5;
	int[] d = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[] c = {60, 70, 90, 80, 100};
	int[] I = {0, 7, 12, 8};
	int[] J = {1, 8, 17, 9};
	
	public void solve(){
		Model model = new Model("Liquid");
		IntVar[][] X = model.intVarMatrix(N, P, 0, 1);
		
		//cac rang buoc:
		
		//rang buoc hai chat khong the hoa chung:
		int n = I.length;
		for (int k = 0; k < n; k++) {
			int i1 = I[k];
			int i2 = J[k];
			for (int j = 0; j<P; j++) {
					model.ifThen(
							model.arithm(X[i1][j], "=", 1),
							model.arithm(X[i2][j], "=", 0));
					model.ifThen(
							model.arithm(X[i2][j], "=", 1),
							model.arithm(X[i1][j], "=", 0));
			}
		}
		
		//rang buoc 3 chat khong the hoa chung:
		for(int j = 0; j<P; j++) {
			model.ifThen(model.arithm(X[1][j], "=", 1), model.arithm(X[2][j], "+", X[9][j], "<=", 0));
			model.ifThen(model.arithm(X[2][j], "=", 1), model.arithm(X[1][j], "+", X[9][j], "<=", 0));
			model.ifThen(model.arithm(X[9][j], "=", 1), model.arithm(X[2][j], "+", X[1][j], "<=", 0));
			
			model.ifThen(model.arithm(X[0][j], "=", 1), model.arithm(X[12][j], "+", X[9][j], "<=", 0));
			model.ifThen(model.arithm(X[9][j], "=", 1), model.arithm(X[12][j], "+", X[0][j], "<=", 0));
			model.ifThen(model.arithm(X[12][j], "=", 1), model.arithm(X[0][j], "+", X[9][j], "<=", 0));
		}
		
		//rang buoc ve suc chua:

		for(int j = 0; j<P; j++) {
			IntVar[] y = new IntVar[N];
			for (int i = 0; i<N; i++) {
				y[i] = X[i][j];
			}
			model.scalar(y, d, "<=", c[j]).post();
		}
		
		//moi chat long o mot thung:
		int[] oneP = new int[P];
		for(int i = 0; i<P; i++) oneP[i] = 1;
		for(int i = 0; i<N; i++) {
			IntVar[] y = new IntVar[P];
			for (int j = 0; j<P; j++) {
				y[j] = X[i][j];
			}
			model.scalar(y, oneP, "=", 1).post();
		}
		
		model.getSolver().solve();
		
		for(int j = 0; j<P; j++) {
			System.out.print("Thung " + j + ": ");
			for(int i = 0; i<N; i++) if (X[i][j].getValue() == 1) {
				System.out.print(i + " ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		Assign1_Liquid app = new Assign1_Liquid();
		app.solve();
	}
}
