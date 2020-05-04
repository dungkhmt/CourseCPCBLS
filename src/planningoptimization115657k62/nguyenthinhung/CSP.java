package planningoptimization115657k62.nguyenthinhung;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import planningoptimization115657k62.phamquangdung.HillClimbingSearch;

public class CSP {
	public static void main(String[] agrs) {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[5];
		for(int i = 0;i < 5;i++) {
			x[i] = new VarIntLS(mgr, 1,5);
		}
		IConstraint[] c = new IConstraint[6];
		c[0] = new NotEqual(new FuncPlus(x[2], 3), x[1]);
		c[1] = new localsearch.constraints.basic.LessOrEqual(x[3], x[4]);
		c[2] = new IsEqual(new FuncPlus(x[2], x[3]), new FuncPlus(x[0], 1));
		c[3] = new LessOrEqual(x[4], 3);
		c[4] = new IsEqual(new FuncPlus(x[1], x[4]), 7);
		c[5] = new Implicate(new IsEqual(x[2], 1), new NotEqual(x[4], 2));
		
		IConstraint cs = new AND(c);
		mgr.close();
		
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(cs, 100000);
		for(int i = 0; i < 5;i++) {
			System.out.println("X[" + i + "] = " + x[i].getValue());
		}
	}
	
}
