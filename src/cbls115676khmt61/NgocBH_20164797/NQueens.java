/*
* Problem: NQueens.java
* Description: 
* Created by ngocjr7 on [2020-03-28 21:16:09]
*/

package cbls115676khmt61.ngocbh_20164797;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;
import localsearch.model.LocalSearchManager;
public class NQueens {
	int NUM_ITER = 10000; 
	int n = 1; // number of queens
	LocalSearchManager mgr; // manage variables
	VarIntLS[] x;
	ConstraintSystem S;

	public NQueens(final int n) {
		this.n = n;
	}

	public void stateModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[n];
		for (int i = 0; i < n; i++)
			x[i] = new VarIntLS(mgr, 0, n - 1);
		S = new ConstraintSystem(mgr);
		S.post(new AllDifferent(x));

		IFunction[] f1 = new IFunction[n];
		for (int i = 0; i < n; i++) 
			f1[i] = new FuncPlus(x[i], i);
		S.post(new AllDifferent(f1));

		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++)
			f2[i] = new FuncPlus(x[i], -i);
		S.post(new AllDifferent(f2));
		mgr.close();
	}

	public void search() {
		MinMaxSelector mms = new MinMaxSelector(S);
		int it = 0;
		while ( it < NUM_ITER && S.violations() > 0 ) {
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			sel_x.setValuePropagate(sel_v);
			System.out.printf("Violations = %d\n", S.violations());
			++it;
		}
	}

	public void printResults() {
		System.out.printf("Final violations = %d, when X = ", S.violations());
		for (int i = 0; i < n; i++) {
			System.out.print(x[i].getValue() + 1);
			System.out.print(" ");
		}
	}

	public static void main(final String[] args) {
		final NQueens prob = new NQueens(8);
		prob.stateModel();
		prob.search();
		prob.printResults();
	}
} 