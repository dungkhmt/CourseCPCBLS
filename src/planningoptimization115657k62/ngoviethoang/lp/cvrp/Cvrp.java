package planningoptimization115657k62.ngoviethoang.lp.cvrp;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.limits.SolutionCounter;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.criteria.Criterion;

public class Cvrp {
	long startTime;
	long endTime;
	
	int N = 6; // number of clients
	int K = 2; // number of truck
	int[] w = new int[] {0,4,2,5,2,3,5}; // package size, w[0] for depot
	int[] c = new int[] {11, 11}; // truck capacity
	int[][] d = new int[][]  { // proximity matrix
        {0,3,2,1,4,3,7},
        {2,0,2,3,5,3,9},
        {1,3,0,2,4,2,4},
        {5,3,2,0,1,1,7},
        {3,1,5,1,0,3,6},
        {6,3,2,4,4,0,9},
        {2,3,2,1,2,8,0}
	};
//	int[] w = {0, 1, 1};
//	int[] c = {10};
//	int[][] d = new int[][] {
//		{0, 1, 2},
//		{3, 0, 5},
//		{4, 2, 0}
//	};
	int[][] dd;
	
	IntVar[] X;
	IntVar[] L;
	IntVar[] W;
	IntVar[] R;
 	Model model;
	
	public int distance(int i, int j) {
		i = i + 1;
		j = j + 1;
		if (i > N) i = 0;
		if (j > N) j = 0;
		return d[i][j];
	}
	public int weight(int i) {
		i = i + 1;
		if (i > N) {
			i = 0;
		}
		return w[i];
	}
	
	public void cvrp() {
		dd = new int[N+2*K][N+2*K];
		for (int i = 0; i < N+2*K; i++) {
			for (int j = 0; j < N+2*K; j++) {
				int x, y;
				if (i < N) {
					if (j < N) {
						dd[i][j] = d[i+1][j+1];
					} else {
						dd[i][j] = d[i+1][0];
					}
				} else {
					if (j < N) {
						dd[i][j] = d[0][j+1];
					} else {
						dd[i][j] = d[0][0];
					}
				}
			}
		}
		startTime = System.currentTimeMillis();
		model = new Model("cvrp");
		X = model.intVarArray(N+2*K, 0, N+2*K-1);
		L = model.intVarArray(N+2*K, 0, 100);
		W = model.intVarArray(N+2*K, 0, 100);
		R = model.intVarArray(N+2*K, 0, K-1);
		
		//Constraint
		for (int i = N; i < N+K; i++) {
			model.arithm(L[i], "=", 0).post();
			model.arithm(W[i], "=", 0).post();
		}
		
		for (int i = 0; i < K; i++) {
			model.arithm(R[N+i], "=", i).post();
			model.arithm(R[N+K+i], "=", i).post();
		}
		
		for (int i = 0; i < N+2*K-1; i++) {
			for (int j = i+1; j < N+2*K; j++) {
				model.arithm(X[i], "!=", X[j]).post();
			}
		}
		
		for (int i = 0; i < N+2*K; i++) 
			model.arithm(X[i], "!=", i).post();		
		for (int i = N; i < N+K; i++) {
			for (int j = N+K; j < N+2*K; j++) {
				model.arithm(X[i], "!=", j).post();
			}
		}
		for (int i = 0; i < N; i++) {
			for (int j = N; j < N+K; j++) {
				model.arithm(X[i], "!=", j).post();
			}
		}
		for (int i = 0; i < N+K; i++) {
			for (int j = 0; j < N+2*K; j++) {
				if (i != j) {
					Constraint c1 = model.arithm(R[i], "=", R[j]);
					Constraint c2 = model.arithm(L[j], "=", model.intOffsetView(L[i], distance(i,j)));
					Constraint c3 = model.arithm(W[j], "=", model.intOffsetView(W[i], weight(j)));
					model.ifThen(model.arithm(X[i], "=", j), 
							model.and(c1, model.and(c2,c3)));
				}
			}
		}
		
		for (int i = 0; i < K; i++) {
			model.arithm(W[N+K+i], "<=", c[i]).post();
		}
		
		int[] ones = new int[K];		
		for (int i = 0; i < K; i++) {
			ones[i] = 1;
		}
		IntVar[] y = new IntVar[K];
		for (int i = 0; i < K; i++) {
			y[i] = L[i+N+K];
		}
		IntVar f = model.intVar(0, 1000);
		model.scalar(y, ones, "=", f).post();
		
		// Objective
		model.setObjective(true, f);
		System.out.println("Solving");
		//Criterion solcpt = new SolutionCounter(model, 5);
		//List res = model.getSolver().findAllOptimalSolutions(f, false, solcpt);
		boolean res = model.getSolver().solve();
		if (!res) {
			System.out.println("No solution");
		} else {
			System.out.println("Founded");
			for (int i = 0; i < K; i++) {
				int x = N+i;
				System.out.print("Route " + (i+1) + ": 0 ");
				while (x != N+K+i) {
					x = X[x].getValue();
					if (x >= N) {
						x = 0;
						break;
					}		
					System.out.print("-> " + (x+1) + " ");
				}
				System.out.println("-> 0");
			}
			System.out.println("Objective: " + f.getValue());
//			for (int i = 0; i < N+2*K; i++) {
//				System.out.println("X["+i+"] = " + X[i].getValue());
//			}
//			for (int i = 0; i < N+2*K; i++) {
//				System.out.println("L["+i+"] = " + L[i].getValue());
//			}
//			for (int i = 0; i < N+2*K; i++) {
//				System.out.println("W["+i+"] = " + W[i].getValue());
//			}
//			for (int i = 0; i < N+2*K; i++) {
//				for (int j = 0; j < N+2*K; j++) {
//					System.out.print(dd[i][j] + " ");
//				}
//				System.out.println();
//			}
		}
		endTime = System.currentTimeMillis();
		System.out.print("Run time: " + (endTime - startTime)/1000.0 +  "s");
	}
	
	public static void main(String[] args) {
		Cvrp app = new Cvrp();
		app.cvrp();
	}
}
