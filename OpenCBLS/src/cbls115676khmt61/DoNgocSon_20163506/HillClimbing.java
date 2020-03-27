package cbls115676khmt61.TranHuyHung_20164777;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class HillClimbing {
	// data structures input
	
	//modeling
	LocalSearchManager mgr;	// manager object
	VarIntLS[] X;	// decision variables
	ConstraintSystem S;
	
	private void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[5];
		for (int i=0; i<5; i++)
			X[i] = new VarIntLS(mgr, 1, 5);
		
		S = new ConstraintSystem(mgr);
		S.post(new NotEqual(new FuncPlus(X[2], 3), X[1]));
		S.post(new LessOrEqual(X[3], X[4]));
		S.post(new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0], 1)));
		S.post(new LessOrEqual(X[4], 3));
		S.post(new IsEqual(new FuncPlus(X[1], X[4]), 7));
		S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));
		
		mgr.close();
	}
	
	public void printSol() {
		for (int i=0; i<5; i++)
			System.out.print(X[i].getValue() + " ");
		System.out.println();
	}
	
	private void localSearch() {
//		printSol();
		System.out.println("init, S = " + S.violations());
		int it = 1;
		while (it < 10000 && S.violations() > 0) {
			MinMaxSelector mms = new MinMaxSelector(S);
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_value = mms.selectMostPromissingValue(sel_x);
			sel_x.setValuePropagate(sel_value);	// local move: assign value,
											// propagate update violations,
											// thanks to dependency graph
			System.out.println("Step " + it + ", S = " + S.violations());
			it++;
		}
		System.out.print("Best solution: ");
		printSol();
	}
	
	public static void main(String[] args) {
		HillClimbing app = new HillClimbing();
		app.stateModel();
		app.localSearch();
	}
}
