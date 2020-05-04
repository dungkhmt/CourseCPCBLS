package cbls115676khmt61.nguyenductrong_20164810;

import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ExampleConditionSum {
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		int M = 7; // 7 items
		int N = 3; // 3 bins
		int[] w = {3,4,2,5,7,1,2}; // weight of items
		
		VarIntLS[] X = new VarIntLS[M];
		for(int i=0;i<M;i++) X[i] = new VarIntLS(mgr, 1, N-1);
		IFunction f = new ConditionalSum(X, w, 1);
		mgr.close();
		
		X[0].setValuePropagate(0);
		X[1].setValuePropagate(3);
		X[2].setValuePropagate(2);
		X[3].setValuePropagate(1);
		X[4].setValuePropagate(2);
		X[5].setValuePropagate(3);
		X[6].setValuePropagate(2);
		
		System.out.println("f = " + f.getValue());
		int d = f.getAssignDelta(X[6], 1);
		System.out.println("d = " + d + ", f = " + f.getValue());
		X[6].setValuePropagate(1);
		System.out.println("f = " + f.getValue());
	}

}
