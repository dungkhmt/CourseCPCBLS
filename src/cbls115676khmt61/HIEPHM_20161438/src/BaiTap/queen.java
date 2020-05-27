package BaiTap;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class queen {
	public queen(int n) {
		this.n = n;
	}
	// data
	int n;
	
	// modeling
	LocalSearchManager mgr;
	VarIntLS [] X;
	ConstraintSystem S;
	
	private void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[n];
		for(int i =0; i < n; i++)
			X[i] = new VarIntLS(mgr, 1, n);
		
		S = new ConstraintSystem(mgr);
		IConstraint c = new AllDifferent(X);
		S.post(c);
		
		IFunction[] f1 = new IFunction[n];
		for(int i = 0; i < n; i++)
			f1[i] = new FuncPlus(X[i], i);
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for(int i = 0; i < n; i++)
			f2[i] = new FuncPlus(X[i], -i);
		S.post(new AllDifferent(f2));
		
		mgr.close();
	}
	
	public void printSol() {
		for(int i = 0; i < n; i++) System.out.println(X[i].getValue() + " ");
		System.out.println();
	}
	
	private void localSearch() {
		printSol();
		System.out.println("init, S =  " + S.violations());
		int it = 1;
		MinMaxSelector mns = new MinMaxSelector(S);
		while (it < 10000 && S.violations() > 0) {
			
			VarIntLS sel_x = mns.selectMostViolatingVariable();
			int sel_value = mns.selectMostPromissingValue(sel_x);
			sel_x.setValuePropagate(sel_value);
			System.out.println("Step " + it + " , S = " + S.violations());
			it ++;
		}
		System.out.println("Best Solution: ");
		printSol();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		queen app = new queen(100);
		app.stateModel();
		app.localSearch();

	}
	
}