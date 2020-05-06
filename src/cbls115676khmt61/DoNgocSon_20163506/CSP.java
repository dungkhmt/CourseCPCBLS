package cbls115676khmt61.DoNgocSon_20163506;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CSP {
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[5];
		for (int i=0; i<X.length; i++)
			X[i] = new VarIntLS(mgr, 1, 5);
		ConstraintSystem CS = new ConstraintSystem(mgr);
		
		CS.post(new NotEqual(new FuncPlus(X[2], 3), X[1]));
		CS.post(new LessOrEqual(X[3], X[4]));
		CS.post(new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0], 1)));
		CS.post(new LessOrEqual(X[4], 3));
		CS.post(new IsEqual(new FuncPlus(X[1], X[4]), 7));
		CS.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));
		CS.post(new LessOrEqual(7, new Sum(X)));
		
		mgr.close();
		
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(CS, 10000);
		for (int i=0; i<X.length; i++)
			System.out.println("X[" + i + "] = " + X[i].getValue());
	}
}
