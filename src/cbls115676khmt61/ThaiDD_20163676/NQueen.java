package cbls115676khmt61.ThaiDD_20163676;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class NQueen {

	int n;
	LocalSearchManager mgr;
	VarIntLS[] x;
	ConstraintSystem S;
	
	public NQueen(int n) {
		this.n = n;
	}
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(mgr, 0, n-1);
		}
		S = new ConstraintSystem(mgr);
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
		mgr.close();
	}
	
	public void search() {
		MinMaxSelector mms = new MinMaxSelector(S);
		int ite = 0;
		while (ite < 10000 && S.violations() > 0) {
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			sel_x.setValuePropagate(sel_v);
			System.out.println("Step " + ite + ", violation = " + S.violations());
			ite++;
		}
	}
	
	public void printResults() {
		for (VarIntLS x_i : x) {
			System.out.print(x_i.getValue() + " ");
		}
	}
	
	public static void main(String[] args) {
		NQueen app = new NQueen(1000);
		app.stateModel();
		app.search();
		app.printResults();
	}
}
