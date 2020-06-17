package planningoptimization115657k62.levanlinh.Exercise;

import java.util.ArrayList;
import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class LiquidChoco {
	
	int N = 20; // so loai chat long
	int M = 5; 	// so thung 
	
	int[] v = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[] V = {60, 70, 80, 90, 100};
	
	ArrayList<int[]> conflict = new ArrayList<>();
	IntVar[][] X;
	Model model;
	void initialData() {
		conflict.add(new int[] {0, 1});
		conflict.add(new int[] {7, 8});
		conflict.add(new int[] {12, 17});
		conflict.add(new int[] {8, 9});
		conflict.add(new int[] {1, 2, 9});
		conflict.add(new int[] {0, 9, 12});
	}
	
	void buildModel() {
		model = new Model("Liquid");
		X = model.intVarMatrix("X", N, M, new int[] {0,1}); // chat long i trong thung j
		
		int[] oneM = new int[M];
		Arrays.fill(oneM, 1);
		for (int i = 0; i < N; ++i) {
			model.scalar(X[i], oneM, "=", 1).post();
		}
		
		for (int j = 0; j < M; ++j) {
			IntVar[] y = new IntVar[N];
			for (int i = 0; i < N; ++i) y[i] = X[i][j];
			
			model.scalar(y, v, "<=", V[j]).post();
		}
		
		for (int index = 0; index < conflict.size(); ++index) {
			int[] mem = conflict.get(index);
			int len = mem.length;
			int[] ones = new int[len];
			Arrays.fill(ones, 1);
			IntVar[] y = new IntVar[len];
			for (int j = 0; j < M; ++j) {
				for (int i = 0; i < len; ++i) y[i] = X[mem[i]][j];
				
				model.scalar(y, ones, "<", len).post();
			}
		}
	}
	
	void solve() {
		Solver s = model.getSolver();
		
		s.solve();
		
		for (int j = 0; j < M; ++j) {
			int sum = 0;
			System.out.print("Thung " + (j+1) + ": ");
			for (int i = 0; i < N; ++i) if (X[i][j].getValue() == 1) {
				System.out.print((i) + "\t");
				sum += v[i];
			}
			System.out.println();
			System.out.println("Dung tich = " + V[j] + ", chua " + sum + " lits");
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LiquidChoco app = new LiquidChoco();
		app.initialData();
		app.buildModel();
		app.solve();

	}

}
