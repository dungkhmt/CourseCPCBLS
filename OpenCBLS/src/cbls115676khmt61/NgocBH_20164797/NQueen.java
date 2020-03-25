package practice;

import localsearch.model.*;
import localsearch.constraints.alldifferent.*;
import localsearch.functions.basic.*;
import localsearch.selectors.*;

public class NQueen {
	private int n; // number of queens
	private LocalSearchManager mgr; // manager object
	private VarIntLS[] x; // decision variables
	private ConstraintSystem S;
	
	private NQueen(int n) {
		this.n = n;
	}
	
	private void stateModel() {
		mgr = new LocalSearchManager();
		
		x = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(mgr, 0, n - 1);
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
	
	private void search() {
		MinMaxSelector mms = new MinMaxSelector(S);
		int it = 0;
		while (it < 100000 && S.violations() > 0) {
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			sel_x.setValuePropagate(sel_v); // local move
			it++;
		}
	}
	
	private void printResult() {
		System.out.println(S.violations());
		for (int i = 0; i < this.n; i++) {
			System.out.print(Integer.toString(x[i].getValue()) + ' ');
		}
	}
	
	public static void main(String[] args) {
		NQueen nqueen = new NQueen(10);
		nqueen.stateModel();
		nqueen.search();
		nqueen.printResult();
	}
}
