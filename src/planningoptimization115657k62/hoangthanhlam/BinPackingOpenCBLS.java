package planningoptimization115657k62.hoangthanhlam;

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

public class BinPackingOpenCBLS {
	
	public int N = 3;
	public int H = 6;
	public int W = 4;
	
	public int[] h = {2, 4, 5};
	public int[] w = {3, 3, 1};
	
	LocalSearchManager mgr;
	ConstraintSystem C;
	VarIntLS[] x, y, o;
	
	public void createModel() {
		mgr = new LocalSearchManager();
		C = new ConstraintSystem(mgr);
		
		x = new VarIntLS[N];
		y = new VarIntLS[N];
		o = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 0, W);
			y[i] = new VarIntLS(mgr, 0, H);
			o[i] = new VarIntLS(mgr, 0, 1);
		}
		
		for (int i = 0; i < N; i++) {
			IConstraint c[] = new IConstraint[2];
			
			IConstraint[] c1 = new IConstraint[2];
			c1[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), W);
			c1[1] = new LessOrEqual(new FuncPlus(y[i], h[i]), H);
			c[0] = new Implicate(new IsEqual(o[i], 0), new AND(c1));
			C.post(c[0]);
			
			c1 = new IConstraint[2];
			c1[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), W);
			c1[1] = new LessOrEqual(new FuncPlus(y[i], w[i]), H);
			c[1] = new Implicate(new IsEqual(o[i], 1), new AND(c1));
			C.post(c[1]);
		}
		
		for (int i = 0; i < N-1; i++) {
			for (int j = i+1; j < N; j++) {
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 0);
				c1[1] = new IsEqual(o[j], 0);
				IConstraint c2 = new AND(c1);
				IConstraint[] c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				c3[2] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				IConstraint c4 = new OR(c3);
				C.post(new Implicate(c2, c4));
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 0);
				c1[1] = new IsEqual(o[j], 1);
				c2 = new AND(c1);
				c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				c3[2] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				c4 = new OR(c3);
				C.post(new Implicate(c2, c4));
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 1);
				c1[1] = new IsEqual(o[j], 0);
				c2 = new AND(c1);
				c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				c3[2] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				c4 = new OR(c3);
				C.post(new Implicate(c2, c4));
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 1);
				c1[1] = new IsEqual(o[j], 1);
				c2 = new AND(c1);
				c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				c3[2] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				c4 = new OR(c3);
				C.post(new Implicate(c2, c4));
			}
		}
		
		mgr.close();
	}
	
	public void search() {
		HillClimbing search = new HillClimbing();
		search.search(C, 10000);
		for(int i = 0; i < N; i++){
			System.out.println("Item " + i + ": " + x[i].getValue() + "," + y[i].getValue() + "," + o[i].getValue());
		}
	}

	public static void main(String[] args) {
		BinPackingOpenCBLS lam = new BinPackingOpenCBLS();
		lam.createModel();
		lam.search();

	}

}
