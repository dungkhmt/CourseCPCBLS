package planningoptimization115657k62.nguyenthinhung;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class NQueen {
	int n = 1000;
	LocalSearchManager mgr;
	VarIntLS[] x;
	ConstraintSystem C;
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[n];
		for(int i = 0;i < n;i++) {
			x[i] = new VarIntLS(mgr, 0, n-1);
		}
		C = new ConstraintSystem(mgr);
		C.post(new AllDifferent(x));
		IFunction[] f1 = new IFunction[n];
		for(int i = 0;i < n;i++) {
			f1[i] = new FuncPlus(x[i], i);
		}
		C.post(new AllDifferent(f1));
		IFunction[] f2 = new IFunction[n];
		for(int i = 0;i < n;i++) {
			f2[i] = new FuncPlus(x[i], -i);
		}
		C.post(new AllDifferent(f2));
		
		mgr.close();
	}
	
	public void search() {
		System.out.println("init C = " + C.violations());
		MinMaxSelector mms = new MinMaxSelector(C);
		int stp = 0;
		while(stp < 100000 && C.violations() > 0) {
			VarIntLS slc_x = mms.selectMostViolatingVariable();
			int slc_v = mms.selectMostPromissingValue(slc_x);
			slc_x.setValuePropagate(slc_v);
			System.out.println("Step " + stp + ", violations = " + C.violations());
			stp ++;
		}
	}
	
	public static void main(String[] args) {
		NQueen obj = new NQueen();
		obj.stateModel();
		obj.search();
	}
	
}
