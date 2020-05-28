package planningoptimization115657k62.KieuMinhHieu;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class TimKiemLeoDoiViDu {
	public static void main(String[] args) {
	
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[5];
		for(int i = 0; i < 5; i++)
		x[i] = new VarIntLS(mgr,1,5);
		
		ConstraintSystem S = new ConstraintSystem(mgr);
		
		S.post(new NotEqual(new FuncPlus(x[2],3), x[1]));
		S.post(new LessOrEqual(x[3], x[4]));
		S.post(new IsEqual(new FuncPlus(x[2],x[3]), new FuncPlus(x[0],1)));
		S.post(new LessOrEqual(x[4], 3));
		S.post(new IsEqual(new FuncPlus(x[4],x[1]), 7));	
		S.post(new Implicate(new IsEqual(x[2], 1), new NotEqual(x[4], 2)));	
		mgr.close();
		
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S,10000);
		
		for (int i = 0; i < 5; i++)
			System.out.print(x[i].getValue()+" ");
	}
}


