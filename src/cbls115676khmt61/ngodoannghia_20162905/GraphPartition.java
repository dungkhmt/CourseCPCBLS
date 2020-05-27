package cbls115676khmt61.ngodoannghia_20162905;


import com.sun.org.apache.xpath.internal.functions.FuncSum;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartition {
	
	private int N;
	private int V;
	
	public GraphPartition(int N, int V) {
		this.N = N;
		this.V = V;
	}
	private int[][] E = {
            {1,2,8},
            {1,3,2},
            {1,7,3},
            {2,3,8},
            {2,4,7},
            {2,7,4},
            {2,8,6},
            {0,2,5},
            {3,5,1},
            {3,8,5},
            {4,6,8},
            {4,7,9},
            {0,4,1},
            {5,9,5},
            {5,8,4},
            {6,9,4},
            {0,6,7},
            {0,8,2},
            {0,9,8}
    };
	LocalSearchManager mgr;
	VarIntLS[] X;
	VarIntLS[][] Z;
	ConstraintSystem S;
 	IFunction f;
	
	public void StateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		Z = new VarIntLS[N][N];
		for(int i = 0; i < N; i++) {
			X[i] = new VarIntLS(mgr, 0, 1);
			for(int j = 0; j < N; j++) {
				Z[i][j] = new VarIntLS(mgr, 0, 2);
			}
		}
		IFunction sum;
		
		for(int i = 0; i < N-1; i++) {
			for(int j = i+1; j < N; j++) {
				IConstraint c1 = new IsEqual(Z[i][j], 1);
				IConstraint c2 = new IsEqual( new FuncPlus(X[i], X[j]),1);
				
				S.post(new Implicate(c1, c2));
				
				c1 = new IsEqual( new FuncPlus(X[i], X[j]),1);
				c2 = new IsEqual(Z[i][j], 1);
				
				S.post(new Implicate(c1, c2));
				
				
							
			}
//			sum = new FuncSum(X[i]);
		}
		
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
