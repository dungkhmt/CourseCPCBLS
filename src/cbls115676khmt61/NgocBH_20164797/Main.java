package cbls115676khmt61.ngocbh_20164797;

import localsearch.model.VarIntLS;
import localsearch.model.IFunction;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.LocalSearchManager;

public class Main {
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		int n = 5;
		int[] w = new int[]{3,2,5,4,6};
		VarIntLS[] X = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			X[i] = new VarIntLS(mgr,1,5);
		}
		X[0].setValue(2);
		X[1].setValue(1);
		X[2].setValue(5);
		X[3].setValue(2);
		X[4].setValue(3);
		IFunction s = new ConditionalSum(X, w, 3);
		mgr.close();
		System.out.println("S = " + s.getValue());
	}
}
