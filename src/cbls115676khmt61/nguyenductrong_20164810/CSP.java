package cbls115676khmt61.nguyenductrong_20164810;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CSP {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[5];
		ConstraintSystem S = new ConstraintSystem(mgr);

		for (int i = 0; i < X.length; i++) {
			X[i] = new VarIntLS(mgr, 1, 5);
		}

		// X2+3 != X1
		S.post(new NotEqual(new FuncPlus(X[2], 3), X[1]));

		// X3 <= X4
		S.post(new LessOrEqual(X[3], X[4]));

		// X2+X3 = X0+1
		S.post(new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0], 1)));

		// X4 <= 3
		S.post(new LessOrEqual(X[4], 3));

		// X1+X4 = 7
		S.post(new IsEqual(new FuncPlus(X[1], X[4]), 7));

		// X2 = 1 => X4 != 2
		S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));

		mgr.close();

		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 10000);
		for (int i = 0; i < X.length; i++) {
			System.out.println("X[" + i + "]  = " + X[i].getValue());
		}
	}
}
