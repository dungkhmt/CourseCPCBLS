package planningoptimization115657k62.hoangthanhlam;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class QueenCBLS {

	public int n;
	public QueenCBLS(int n) {
		this.n = n;
	}
	
	public void test() {
		LocalSearchManager ls = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(ls);
		
		VarIntLS[] x = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(ls, 0, n-1);
		}
		
		S.post(new AllDifferent(x));
		
		IFunction[] f1 = new IFunction[n];
		for (int i = 0; i < n; i++) {
			f1[i] = new FuncPlus(x[i], i);
		}
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++) {
			f2[i] = new FuncPlus(x[i], -i);
		}
		S.post(new AllDifferent(f2));
		
		ls.close();
		System.out.println("Init S = " + S.violations());
		MinMaxSelector mms = new MinMaxSelector(S);
		
		int it = 0;
		while(it < 10000 && S.violations() > 0) {
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			
			sel_x.setValuePropagate(sel_v);
			System.out.println("Step " + it + ", S = " + S.violations());
			
			it++;
		}
		System.out.println(S.violations());
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QueenCBLS Q = new QueenCBLS(1000);
		Q.test();

	}

}
