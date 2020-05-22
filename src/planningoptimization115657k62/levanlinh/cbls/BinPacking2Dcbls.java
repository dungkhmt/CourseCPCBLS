package planningoptimization115657k62.levanlinh.cbls;

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

public class BinPacking2Dcbls {
	
	int N = 3;
	int W = 4;
	int H = 6;
	int[] w = {3, 3, 1};
	int[] h = {2, 4 ,6};
	
	LocalSearchManager mgr;
	VarIntLS[] x, y, o;
	ConstraintSystem S;
	
	public void buildModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		x = new VarIntLS[N];
		y = new VarIntLS[N];
		o = new VarIntLS[N];
		
		for (int i = 0; i < N; ++i) {
			x[i] = new VarIntLS(mgr, 0, W-1);
			y[i] = new VarIntLS(mgr, 0, H-1);
			o[i] = new VarIntLS(mgr, 0, 1);
		}
		for (int i = 0; i < N; ++i) {
			IConstraint c1 = new LessOrEqual(new FuncPlus(x[i], w[i]), W);
			IConstraint c2 = new LessOrEqual(new FuncPlus(y[i], h[i]), H);
			IConstraint c3 = new LessOrEqual(new FuncPlus(x[i], h[i]), W);
			IConstraint c4 = new LessOrEqual(new FuncPlus(y[i], w[i]), H);
			
			S.post(new Implicate(new IsEqual(o[i], 0), new AND(c1, c2)));
			S.post(new Implicate(new IsEqual(o[i], 1), new AND(c3, c4)));
		}
		
		for (int i = 0; i < N - 1; ++i) {
			for (int j = i + 1; j < N; j++) {
				IConstraint o00 = new AND(new IsEqual(o[i], 0), new IsEqual(o[j], 0));
				IConstraint o01 = new AND(new IsEqual(o[i], 0), new IsEqual(o[j], 1));
				IConstraint o10 = new AND(new IsEqual(o[i], 1), new IsEqual(o[j], 0));
				IConstraint o11 = new AND(new IsEqual(o[i], 1), new IsEqual(o[j], 1));
				
				IConstraint c1 = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				IConstraint c2 = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				
				IConstraint c3 = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				IConstraint c4 = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				
				IConstraint c5 = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				IConstraint c6 = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				
				IConstraint c7 = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				IConstraint c8 = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				
				IConstraint[] s = new IConstraint[4];
				s[0] = new Implicate(o00, new OR(new IConstraint[] {c1, c2, c5, c6}));
				s[1] = new Implicate(o01, new OR(new IConstraint[] {c1, c4, c5, c8}));
				s[2] = new Implicate(o10, new OR(new IConstraint[] {c3, c2, c7, c6}));
				s[3] = new Implicate(o11, new OR(new IConstraint[] {c3, c4, c7, c8}));
				
				S.post(s[0]); S.post(s[1]); S.post(s[2]); S.post(s[3]);
			}
		}
		mgr.close();
	}
	
	public void solve() {
		System.out.println("Init S = " + S.violations());
		HillClimbingSearch searcher = new HillClimbingSearch(S);
		searcher.search(10000);
		for (int i = 0; i < N; ++i) {
			System.out.print("x[" + i + "] = " + x[i].getValue());
			System.out.print("\ty[" + i + "] = " + y[i].getValue());
			System.out.println("\to[" + i + "] = " + o[i].getValue());
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BinPacking2Dcbls app = new BinPacking2Dcbls();
		app.buildModel();
		app.solve();
	}

}
