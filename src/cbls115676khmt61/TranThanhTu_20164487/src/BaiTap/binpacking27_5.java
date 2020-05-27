package BaiTap;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class binpacking27_5 {
	int W = 4;
	int H = 7;
	int N = 5;
	int[] w = { 1, 3, 2, 3, 1, 2 };
	int[] h = { 4, 1, 2, 1, 4, 3 };
	LocalSearchManager mgr;
	VarIntLS[] x;
	VarIntLS[] y;
	VarIntLS[] o;
	ConstraintSystem S;

	public void stateModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[N];
		y = new VarIntLS[N];
		o = new VarIntLS[N];

		for (int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 0, W - 1);
			y[i] = new VarIntLS(mgr, 0, H - 1);
			o[i] = new VarIntLS(mgr, 0, 1);
		}

		S = new ConstraintSystem(mgr);
		for (int i = 0; i < N; i++) {
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(x[i], w[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(y[i], h[i]), H)));

			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(x[i], h[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(y[i], w[i]), H)));
		}

		for (int i = 0; i < N - 1; i++)
			for (int j = i + 1; j < N; j++) {
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 0);
				c1[1] = new IsEqual(o[j], 0);
				IConstraint c2 = new AND(c1);

				IConstraint[] c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				IConstraint c4 = new OR(c3);

				S.post(new Implicate(c2, c4));
			}

		for (int i = 0; i < N - 1; i++)
			for (int j = i + 1; j < N; j++) {
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 0);
				c1[1] = new IsEqual(o[j], 1);
				IConstraint c2 = new AND(c1);

				IConstraint[] c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				IConstraint c4 = new OR(c3);

				S.post(new Implicate(c2, c4));
			}

		for (int i = 0; i < N - 1; i++)
			for (int j = i + 1; j < N; j++) {
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 1);
				c1[1] = new IsEqual(o[j], 0);
				IConstraint c2 = new AND(c1);

				IConstraint[] c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				IConstraint c4 = new OR(c3);

				S.post(new Implicate(c2, c4));
			}

		for (int i = 0; i < N - 1; i++)
			for (int j = i + 1; j < N; j++) {
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 1);
				c1[1] = new IsEqual(o[j], 1);
				IConstraint c2 = new AND(c1);

				IConstraint[] c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				IConstraint c4 = new OR(c3);

				S.post(new Implicate(c2, c4));
			}

		for (int i = 0; i < N - 1; i++) {
			for (int j = i + 1; j < N; j++) {
				S.post(new LessOrEqual(y[j], y[i]));
			}
		}

		mgr.close();
	}

	public void search() {
		HillClimbingSearch clb = new HillClimbingSearch();
		clb.LocalSearch(S, 1000);

	}

	public void solve() {
		stateModel();
		search();
		for (int i = 0; i < N; i++) {
			System.out.println("Item " + i + ": " + x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
		}
	}

	public static void main(String[] args) {
		binpacking27_5 app = new binpacking27_5();
		app.stateModel();
		app.search();
		app.solve();

	}
}
