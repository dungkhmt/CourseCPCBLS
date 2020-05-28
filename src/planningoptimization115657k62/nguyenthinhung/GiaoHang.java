package planningoptimization115657k62.nguyenthinhung;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class GiaoHang {
	
	int N = 6; 
	int K = 2;
	int capacity = 11;
    int[] demand = {0,4,2,5,2,3,5};
    int[][] c = {
            {0,3,2,1,4,3,7},
            {2,0,2,3,5,3,9},
            {1,3,0,2,4,2,4},
            {5,3,2,0,1,1,7},
            {3,1,5,1,0,3,6},
            {6,3,2,4,4,0,9},
            {2,3,2,1,2,8,0}
    };
	public void solve() {
		double INF = java.lang.Double.POSITIVE_INFINITY;
		Model model = new Model("GiaoHang");
		IntVar[] X = new IntVar[N+K];
		IntVar[] IR = new IntVar[N+K];
		IntVar[] L = new IntVar[N+2*K];
		IntVar[] W = new IntVar[K];
		
		for(int i = N+1;i <= N+K;i++) {
			L[0] = (IntVar) model.intVar("x[" + i  + "]", 0,0);
			W[0] =  (IntVar) model.intVar("x[" + i  + "]", 0,0);
		}
		
		for(int i = 1;i <= 2*K + N;i++) {
			for(int j = i+1;j <= 2*K + N;j++) {
				if(i != j) {
					model.arithm(X[i],"!=", X[j]).post();
				}
				model.ifThen(model.arithm(X[i],"=",i), model.arithm(IR[i],"=",IR[j]));
			}
			model.arithm(X[i],"!=", i).post();
		}
		
		
	}
}
