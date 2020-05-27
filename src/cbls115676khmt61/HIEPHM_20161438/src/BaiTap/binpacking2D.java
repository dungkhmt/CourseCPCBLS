package BaiTap;

import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class binpacking2D {
	int W = 4;
	int H = 6;
	int N = 3;
	int w[] = { 3, 3, 1 };
	int h[] = { 2, 4, 6 };
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
			x[i] = new VarIntLS(mgr, 0, W);
			y[i] = new VarIntLS(mgr, 0, H);
			o[i] = new VarIntLS(mgr, 0, 1);
		}
		S = new ConstraintSystem(mgr);
		for (int i = 0; i < N - 1; i++) {
			for (int j = i + 1; j < N; j++) {
				//1
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
				//2
				IConstraint[] cc1 = new IConstraint[2];
				cc1[0] = new IsEqual(o[i], 0);
				cc1[1] = new IsEqual(o[j], 1);
				IConstraint cc2 = new AND(cc1);

				IConstraint[] cc3 = new IConstraint[4];
				cc3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				cc3[1] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				cc3[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				cc3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				IConstraint cc4 = new OR(cc3);
				S.post(new Implicate(cc2, cc4));
				//3
				IConstraint[] c31 = new IConstraint[2];
				c31[0] = new IsEqual(o[i], 1);
				c31[1] = new IsEqual(o[j], 0);
				IConstraint c32 = new AND(c31);

				IConstraint[] c33 = new IConstraint[4];
				c33[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c33[1] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c33[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				c33[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				IConstraint c34 = new OR(c33);
				S.post(new Implicate(c32, c34));
				//4
				IConstraint[] c41 = new IConstraint[2];
				c41[0] = new IsEqual(o[i], 1);
				c41[1] = new IsEqual(o[j], 1);
				IConstraint c42 = new AND(c41);

				IConstraint[] c43 = new IConstraint[4];
				c43[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c43[1] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				c43[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				c43[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				IConstraint c44 = new OR(c43);
				S.post(new Implicate(c42, c44));
			}
		}
		for (int i = 0; i < N; i++) {
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(x[i], w[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(y[i], h[i]), H)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(x[i], h[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(y[i], w[i]), H)));
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
		for(int i = 0 ; i < N ; i ++){
			System.out.println("Item " + i + ": " + x[i].getValue() + " " +
					y[i].getValue() + " " + o[i].getValue());
		}
	}

	public static void main(String[] args) {
		binpacking2D binpacking2d = new binpacking2D();
		binpacking2d.solve();
	}

}
