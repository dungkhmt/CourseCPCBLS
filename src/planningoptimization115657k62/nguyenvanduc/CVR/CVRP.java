package planningoptimization115657k62.nguyenvanduc.CVR;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class CVRP {
	int N; //khach
	int K; // xe
	int[][] d = new int[N+1][N+1]; //quang duong
	int[] w = new int[N]; //khach hang i can w
	int[] c = new int[K]; //tai trong moi xe
	
	public void solve() {
		int MAX = 999999999;
		Model model = new Model();
		
		IntVar[] X = model.intVarArray(N+K+1, 1, N+2*K);
		IntVar[] R = model.intVarArray(N+2*K+1, 1, K);
		IntVar[] L = model.intVarArray(N+2*K+1, 0, MAX);
		IntVar[] W = model.intVarArray(N+2*K+1, 0, MAX);
		
		
		for (int start = N+1; start <= N+K; start++) {
			model.arithm(L[start], "=", 0).post();;
			model.arithm(W[start], "=", 0).post();;
		}
		
		for (int k = 1; k <= K; k++) {
			model.arithm(R[N+k], "=", R[N+K + k]).post();;
		}
		
		model.allDifferent(X).post();
		for (int i = 1; i <= N+K; i++) {
			model.arithm(X[i], "!=", i);
		}
		
	}
	
}
