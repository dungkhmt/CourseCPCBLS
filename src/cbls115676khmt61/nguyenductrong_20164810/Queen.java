package cbls115676khmt61.nguyenductrong_20164810;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class Queen {

	public Queen(int n) {
		super();
		this.N = n;
	}

	int N;

	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;

	private void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			X[i] = new VarIntLS(mgr, 1, N);
		}

		S = new ConstraintSystem(mgr);
		IConstraint c = new AllDifferent(X);
		S.post(c);
		// S.post(new AllDiff(X));
		IFunction[] f1 = new IFunction[N];
		for (int i = 0; i < N; i++) {
			f1[i] = new FuncPlus(X[i], i);
		}
		S.post(new AllDifferent(f1));

		IFunction[] f2 = new IFunction[N];
		for (int i = 0; i < N; i++) {
			f2[i] = new FuncPlus(X[i], -i);
		}
		S.post(new AllDifferent(f2));

		mgr.close();
	}

	public void printSol() {
		for (int i = 0; i < N; i++)
			System.out.print(X[i].getValue() + " ");
		System.out.println();
	}

	private void localSearch() {
		printSol();
		System.out.println("init, S = " + S.violations());
		int it = 1;
		while (it < 1000000 && S.violations() > 0) {
			MinMaxSelector mms = new MinMaxSelector(S);
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_value = mms.selectMostPromissingValue(sel_x);
			sel_x.setValuePropagate(sel_value);

			System.out.print("Step " + it + ", S = " + S.violations());
			System.out.println();
			it++;
		}
		System.out.print("Best solution: ");
		printSol();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Queen app = new Queen(10);
		app.stateModel();
		app.localSearch();
	}

}
