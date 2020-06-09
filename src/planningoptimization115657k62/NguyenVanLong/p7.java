package planningoptimization115657k62.NguyenVanLong;

import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class p7 {
	
	int N = 3;
	int[] d = {10, 20, 30};
	int[] s = {1, 5, 3};
	int[] e = {5, 7, 8};
	int m = 1;
	int M = 14;
	int time = 10;
	
	Model model;
	IntVar[][] X;
	IntVar f, min, max;
	
	public void buildModel() {
		model = new Model("Project7");
		X = model.intVarMatrix("X", N, time, 0, M);
		f = model.intVar("Optimal", 0, M);
		min = model.intVar("min", 0, M);
		max = model.intVar("max", 0, M);
		
		int[] oneN = new int[N];
		int[] oneT = new int[time];
		Arrays.fill(oneN, 1);
		Arrays.fill(oneT, 1);
		
		for (int i = 0; i < N; ++i) for (int j = 0; j < time; ++j) if (j < s[i] || j > e[i]) model.arithm(X[i][j], "=", 0).post();
		
		for (int i = 0; i < N; ++i) {
			model.scalar(X[i], oneT, "=", d[i]).post();
		}
		
		for (int j = 0; j < time; ++j) {
			IntVar[] y = new IntVar[N];
			for (int i = 0; i < N; ++i) y[i] = X[i][j];
			
			model.or(
					model.scalar(y, oneN, "=", 0), 
					model.and(model.scalar(y, oneN, ">=", m), model.scalar(y, oneN, "<=", M))
					).post();
			model.scalar(y, oneN, "<=", max).post();
			model.ifThen(model.scalar(y, oneN, ">", 0), model.scalar(y, oneN, ">=", min));
			model.arithm(f, "=", max, "-", min).post();
		}
		model.setObjective(model.MAXIMIZE, min);
		model.setObjective(model.MINIMIZE, f);
		
	}
	
	public void solve() {
		Solver s = model.getSolver();
		
		// Print solution
		if (s.solve()) {
			System.out.println ("opt = " + f.getValue());
			for (int i = 0; i < N; ++i) {
				System.out.print("Canh dong " + (i + 1) + " :");
				//for (int j = s[i]; j <= e[i]; ++j) {
				for (int j = 0; j < time; ++j) {
					System.out.print("  " + X[i][j].getValue());
				}
				System.out.println();
			}
		} else {
			System.out.println("No solution found!");
		}
		
		while (s.solve()) {
			System.out.println ("--------------------------------------");
			System.out.println ("opt = " + f.getValue());
			for (int i = 0; i < N; ++i) {
				System.out.print("Canh dong " + (i + 1) + " :");
				//for (int j = s[i]; j <= e[i]; ++j) {
				for (int j = 0; j < time; ++j) {
					System.out.print("  " + X[i][j].getValue());
				}
				System.out.println();
			}
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		p7 app = new p7();
		app.buildModel();
		app.solve();
	}

}
