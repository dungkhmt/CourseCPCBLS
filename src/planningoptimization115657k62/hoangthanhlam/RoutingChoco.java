package planningoptimization115657k62.hoangthanhlam;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class RoutingChoco {
	
	int K = 2;// number of routes
    int N = 6;// number of clients
    int capacity = 11;
    int[] demand = {4,2,5,2,3,5,0,0,0,0};
    int[][] c = {
            {0,3,2,1,4,3,7},
            {2,0,2,3,5,3,9},
            {1,3,0,2,4,2,4},
            {5,3,2,0,1,1,7},
            {3,1,5,1,0,3,6},
            {6,3,2,4,4,0,9},
            {2,3,2,1,2,8,0}
    };
	
	Model model;
	IntVar[] X;
	IntVar[] Route;
	IntVar[] L;
	IntVar[] W;
	int totalC = 0;
	int totalW = 0;
	int[][] d = new int[N+2*K][N+2*K];
	
	public void extendArray() {
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				d[i][j] = c[i+1][j+1];
			}
		}
		
		for (int i = N; i < N+K; i++) {
			for (int j = 0; j < N; j++) {
				d[i][j] = c[0][j+1];
			}
		}
		
		for (int i = N+K; i < N+2*K; i++) {
			for (int j = 0; j < N; j++) {
				d[i][j] = c[0][j+1];
			}
		}
		
		for (int j = N; j < N+K; j++) {
			for (int i = 0; i < N; i++) {
				d[i][j] = c[i+1][0];
			}
		}
		
		for (int j = N+K; j < N+2*K; j++) {
			for (int i = 0; i < N; i++) {
				d[i][j] = c[i+1][0];
			}
		}
	}
	
	public void createModel() {
		model = new Model("Routing");
		X = model.intVarArray(N+K, 0, N+2*K-1);
		for (int i = 0; i < N+K; i++) {
			for (int j = N; j < N+K; j++) {
				model.arithm(X[i], "!=", j).post();
			}
		}
		
		Route = model.intVarArray(N+2*K, 0, K-1);
		
		for (int i = 0; i < N; i++) {
			totalW += demand[i];
			for (int j = 0; j < N; j++) {
				totalC += c[i][j];
			}
		}
		L = model.intVarArray(N+2*K, 0, totalC);
		W = model.intVarArray(N+2*K, 0, totalW);
	}
	
	public void createConstraint() {
		extendArray();
		model.allDifferent(X).post();
		
		for (int i = N; i < N+K; i++) {
			model.arithm(L[i], "=", 0).post();
			model.arithm(W[i], "=", 0).post();
		}
		
		for (int i = 0; i < K; i++) {
			model.arithm(Route[N+i], "=", i).post();
			model.arithm(Route[N+K+i], "=", i).post();
		}
		
		for (int i = 0; i < N+K; i++) {
			// X[i] != i
			model.arithm(X[i], "!=", i).post();
		}
		
		for (int i = 0; i < N+K; i++) {
			for (int j = 0; j < N+2*K; j++) {
				
				model.ifThen(model.arithm(X[i], "=", j), model.arithm(Route[i], "=", Route[j]));
				model.ifThen(model.arithm(X[i], "=", j), model.arithm(L[j], "=", model.intOffsetView(L[i], d[i][j])));
				model.ifThen(model.arithm(X[i], "=", j), model.arithm(W[j], "=", model.intOffsetView(W[i], demand[j])));
			}
		}
		
		for (int k = N+K; k < N+2*K; k++) {
			model.arithm(W[k], "<=", capacity).post();
		}
		
		// Ham muc tieu
		IntVar f = model.intVar(0, totalC);
		model.arithm(f, "=", L[N+K], "+", L[N+K+1]).post(); // sai
		
		model.setObjective(Model.MINIMIZE, f);
	}
	
	public void solve() {
		Solver s = model.getSolver();
		while(s.solve()) {
			for (int i = 0; i < K; i++) {
				System.out.print("Route " + (i+1) + ": 0 -> ");
				int next = X[N+i].getValue();
				while (next < N) {
					System.out.print((next + 1) + " -> ");
					next = X[next].getValue();
				}
				System.out.println(0);
			}
			System.out.println("---------------oOo--------------");
		}
	}

	public static void main(String[] args) {
		RoutingChoco routing = new RoutingChoco();
		routing.createModel();
		routing.createConstraint();
		routing.solve();

	}

}
