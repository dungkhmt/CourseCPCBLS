package planningoptimization115657k62.NguyenVanLong;

import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Liquid_chocosolver {

	int N = 20; //số chất lỏng
	int M = 5; //số thùng chứa
	int[] v = {
			20, 15, 10, 20, 20, 25, 30, 15, 10, 10,
			20, 25, 20, 10, 30, 40, 25, 35, 10, 10
	};
	int[] capacity = {60 , 70, 90, 80, 100};
	int n_conflict = 6;
	int[][] conflict = {
			{2, 0, 1, 0},
			{2, 7, 8, 0},
			{2, 12, 17, 0},
			{2, 8, 9, 0},
			{3, 1, 2, 9},
			{3, 0, 9, 12},
	};//conflict[k][0] conflict thứ k có phần tử đầu là số chất lỏng trong conflict
	Model model = new Model("Liquid");
	IntVar[][] X;
	
	public Liquid_chocosolver() {};
	
	public Liquid_chocosolver(int N, int M, int n_conflict)
	{
		this.N = N;
		this.M = M;
		this.n_conflict = n_conflict;
		v = new int[N]; 
		capacity = new int[M];
	}
	
	public void buildModel()
	{
		X = model.intVarMatrix("X", M, N, 0, 1);
		
		for(int i = 0; i < M; ++i) {
			model.scalar(X[i], v, "<=", capacity[i]).post();
		}
		
		int[] oneM = new int[M];
		Arrays.fill(oneM, 1);
		for(int j = 0; j < N; ++j) {
			IntVar[] y = new IntVar[M];
			for(int i = 0; i < M; ++i)
				y[i] = X[i][j];
			model.scalar(y, oneM, "=", 1).post();
		}
		
		for(int k = 0; k < n_conflict; ++k) {
			int n_liquid = conflict[k][0];
			int[] oneK = new int[n_liquid];
			Arrays.fill(oneK, 1);
			for(int i = 0; i < M; ++i) {
				IntVar[] z = new IntVar[n_liquid];
				for(int j = 1; j <= n_liquid; j++) {
					z[j-1] = X[i][conflict[k][j]];
				}
				model.scalar(z, oneK, "!=", n_liquid).post();
			}
		}
	}
	
	public void solve() {
		Solver solve = model.getSolver();
		solve.solve();
		for(int i = 0; i < M; ++i) {
			System.out.print("Thung " + (i+1) + ": ");
			for(int j = 0; j < N; ++j) {
				if(X[i][j].getValue() == 1)
					System.out.print(j + " ");
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		Liquid_chocosolver app = new Liquid_chocosolver();
		app.buildModel();
		app.solve();
	}
}
