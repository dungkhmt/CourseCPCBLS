package BaiTap;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class xepChatLong {
	int N = 20; // so chat long
	int M = 5; // so thung
	int[] cl = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10 };
	int[] V = { 60, 70, 90, 80, 100 }; // the tich cac thung
	int[][] ct = { { 0, 1 }, { 7, 8 }, { 12, 17 }, { 8, 9 }, { 1, 2, 9 }, { 0, 9, 12 } };

	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] X;
	IFunction[] cksum;

	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			X[i] = new VarIntLS(mgr, 0, M - 1);
		}
		S = new ConstraintSystem(mgr);
		cksum = new IFunction[M];
		for (int i = 0; i < M; i++) {
			cksum[i] = new ConditionalSum(X, cl, i);
			S.post(new LessOrEqual(cksum[i], V[i]));
		}
		for (int i = 0; i < M; i++) {
			for (int j = 0; j < ct.length; j++) {
				int k = 0;
				if (ct[j].length == 2) {
					S.post(new NotEqual(X[ct[j][k]], X[ct[j][k + 1]]));
				}
				if (ct[j].length == 3) {
					S.post(new AND(new NotEqual(X[ct[j][k]], X[ct[j][k + 1]]),
							new NotEqual(X[ct[j][k + 1]], X[ct[j][k + 2]])));
				}
			}
		}
		mgr.close();
	}

	public void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.LocalSearch(S, 10000);
	}

	public void printRS() {
		for (int i = 0; i < N; i++) {
			System.out.print("X["+i+"]= "+X[i].getValue() + " ");
			if(i==9)
				System.out.println();
		}
	}

	public static void main(String[] args) {
		xepChatLong app = new xepChatLong();
		app.stateModel();
		app.search();
		app.printRS();
	}

}
