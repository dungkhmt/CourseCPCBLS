package CBLS;

import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

//Note
/*
 * ConditionalSum(VarIntLS[] X, int[] w, int v)
 * Ham nay giup ich mo hinh hoa nhieu bai toan phan cong
 * Y nghia: Tong trong so w[i] tai cac chi so i sao cho:
 * X[i] co gia tri bang v ???
 * --->>> gan cac item co trong so w vao cac category Y
 * --->>> ConditionalSum(X, w, Y) = 
 * --->>> Tong trong so cua category Y 
 */

public class ExampleConditionalSum {
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		int M = 7; //item
		int N = 3; // bins (categories)
		int[] w = {3,4,2,5,7,1,2}; //weight of items
		
		VarIntLS[] X = new VarIntLS[M];
		for(int i = 0; i < M; i++)
			X[i] = new VarIntLS(mgr, 0, N-1);
		IFunction f = new ConditionalSum(X, w, 1); // consider bin 1
		mgr.close();
		
		X[0].setValuePropagate(0);
		X[1].setValuePropagate(2);
		X[2].setValuePropagate(2);
		X[3].setValuePropagate(1);
		X[4].setValuePropagate(0);
		X[5].setValuePropagate(2);
		X[6].setValuePropagate(1);
		
		System.out.println("f = " + f.getValue());
		int d = f.getAssignDelta(X[6], 0);
		System.out.println("d = " + d + ", f = " + f.getValue());
		X[4].setValuePropagate(1);
		System.out.println("f = " + f.getValue());

	}
}
