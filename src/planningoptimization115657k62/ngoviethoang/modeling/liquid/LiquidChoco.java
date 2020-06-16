package planningoptimization115657k62.ngoviethoang.modeling.liquid;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class LiquidChoco {
	
	long startTime;
	long endTime;
	
	int K = 5;
	int N = 20;
	int[] cap = new int[] {60, 70, 80, 90, 100};
	int[] vol = new int[] {20, 15, 10, 20, 20, 25, 30, 15, 10, 10,
					   20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[][] conf;
	Model model;
	IntVar[][] X;
	int[] oneN;
	int[] oneK;
	public void process( ) {
		int [][] t = new int[][] {{0,1},{7,8},{12,17},{8,9}, {1,2},{1,9},{2,9},{0,9},{0,12},{9,12}};
		conf = new int[N][N];
		for (int i = 0; i < t.length; i++) {
			conf[t[i][0]][t[i][1]] = 1;
			conf[t[i][1]][t[i][0]] = 1;
		}
		oneN = new int[N];
		for (int i = 0; i < N; i++) {
			oneN[i] = 1;
		}
		oneK = new int[K];
		for (int i = 0; i < K; i++) {
			oneK[i] = 1;
		}
	}
	public void solve() {
		process();
		startTime = System.currentTimeMillis();
		model = new Model("LiquidChoco");
		X = new IntVar[K][N];
		for (int k = 0; k < K; k++) {
			X[k] = model.intVarArray(N, 0, 1);
		}
		for (int k = 0; k < K; k++) {
			for (int i = 0; i < N; i++) {
				for (int j = i+1; j < N; j++) {
					if (conf[i][j] == 1) {
						model.arithm(X[k][i], "+", X[k][j], "<=", 1).post();			
					}			
				}				
			}
		}
		for (int i = 0; i < N; i++) {
			IntVar[] Y = new IntVar[K];
			for (int k = 0; k < K; k++) {
				Y[k] = X[k][i];
			}
			model.scalar(Y, oneK, "=", 1).post();
		}
//		Z = new IntVar[K];
//		for (int k = 0; k < K; k++) {
//			model.arithm(Z[k], "<=", cap[k]).post();
//		}
		for (int k = 0; k < K; k++) {
			IntVar[] Y = new IntVar[N];
			for (int i = 0; i < N; i++) {
				Y[i] = X[k][i];
			}
			model.scalar(Y, vol, "=", cap[k]).post();			
		}
		boolean res = model.getSolver().solve();
		if (!res) {
			System.out.println("No solution");
		} else {
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int k = 0; k < K; k++ ) {
				a.clear();
				int sum = 0;
				for (int i = 0; i < N; i++) {
					if (X[k][i].getValue() == 1) {
						a.add(i);
						sum += vol[i];
					}
				}	
				System.out.print("Thung " + k + ": ");
				for (int i = 0; i < a.size(); i++) {
					System.out.print(a.get(i) + " ");
				}
				System.out.println("-> sum = " + sum);
			}
		}
		endTime = System.currentTimeMillis();
		System.out.print("Run time: " + (endTime - startTime)/1000.0 +  "s");
	}
					   
	public static void main(String[] args) {
		LiquidChoco app = new LiquidChoco();
		app.solve();
	}
}
