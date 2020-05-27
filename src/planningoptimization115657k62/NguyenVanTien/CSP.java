package planningoptimization115657k62.NguyenVanTien;

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
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[5];
		for (int i = 0; i < 5; i++) {
			x[i] = new VarIntLS(mgr, 1, 5);
		}
		
		ConstraintSystem CS = new ConstraintSystem(mgr);
		
		CS.post(new NotEqual(new FuncPlus(x[2],3), x[1]));
		CS.post(new LessOrEqual(x[3], x[4]));
		CS.post(new IsEqual(new FuncPlus(x[2],x[3]), new FuncPlus(x[0],1)));
		CS.post(new LessOrEqual(x[4], 3));
		CS.post(new IsEqual(new FuncPlus(x[4],x[1]), 7));
		CS.post(new Implicate(new IsEqual(x[2], 1), new NotEqual(x[4], 2)));
		mgr.close();
		
		HillClimbingSearch a = new HillClimbingSearch();
		a.search(CS, 10000);
		
	}

}
