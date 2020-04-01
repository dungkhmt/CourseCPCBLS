package Baitap;

import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class test {
	LocalSearchManager mgr = new LocalSearchManager();
	int n = 5;
	int[] w = new int[] {3 , 2 , 5 , 4 , 6};
	VarIntLS[] X  = new VarIntLS[n];
	void init() {
		for(int i = 0 ; i < n ; i ++) {
			X[i] = new VarIntLS(mgr, 1 , 5);
		}
		X[0].setValuePropagate(2);
		X[1].setValuePropagate(1);
		X[2].setValuePropagate(5);
		X[3].setValuePropagate(2);
		X[4].setValuePropagate(3);
		IFunction s = new Conditional Sum(X , w , 2); 
		// tong trong so w[i] tai i sao cho X[i] = 2
		mgr.close();
		System.out.println("s = " + s.getValue());
	}
	public static void main(String[] args) {
		test t = new test();
		t.init();
	}
}
