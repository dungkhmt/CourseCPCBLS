package planningoptimization115657k62.hoangthanhlam;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Liquid_Chocosolver {
	int N = 20;
	int M = 5;
	int[] c = {20, 15, 10, 20, 20, 25, 30, 15,10, 10, 20, 25,20, 10, 30, 40, 25, 35, 10, 10};
	int[] v = {60, 70, 90, 80, 100};
	int n_constraint = 6;
	
	int [] oneM= {1,1,1,1,1};
	
	int [][]constraint = {{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						  {0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						  {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0},
						  {0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						  {0, 1, 1, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						  {1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0}};
	Model model;
	IntVar[][] X;
	
	public void createModel() {
		model = new Model("Liquid");
		X = new IntVar[M][N];

		for(int i = 0; i < M; i++) {
			for(int j = 0; j < N; j++) {
				X[i][j] = model.intVar("X["+i+"]["+j+"]", 0, 1);
			}
		}

		for(int j = 0; j < N; j++) {
			IntVar[] y = new IntVar[M];
			for(int i = 0; i < M; i++) {
				y[i] = X[i][j];
			}
			model.scalar(y, oneM, "=", 1).post();
		}

		for(int i = 0; i < M; i++) {
			model.scalar(X[i], c, "<=", v[i]).post();
		}

		for(int i = 0; i < M; i++) {
			for(int k = 0; k < n_constraint; k++) {
				model.scalar(X[i], constraint[k], "<=", 1).post();
			}
		}
	}
	
	public void solve() {
		model.getSolver().solve();
		for(int i = 0; i < M; i++) {
			int load = 0;
			System.out.print("Thùng " + i + " chứa chất lỏng: ");
			for(int j = 0; j < N; j++) {
				if(X[i][j].getValue()==1) {
					load += c[j];
					System.out.print(j + " ");
				}
			}
			System.out.println("\tvới dung tích " + load);
		}
	}
	public static void main(String[] args) {
		Liquid_Chocosolver app= new Liquid_Chocosolver();
		app.createModel();
		app.solve();
	}
}
