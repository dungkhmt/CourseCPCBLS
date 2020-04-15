package CBLS;

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

public class BinPacking2D {
	int W = 4;
	int H = 6;
	int N = 3;
	int[] w = {3, 2, 1};
	int[] h = {2, 3, 6};
	
	VarIntLS[] x, y, o;
	
	LocalSearchManager mgr;
	ConstraintSystem S;
	
	private void stateModel() {
		mgr = new LocalSearchManager(); 
		S = new ConstraintSystem(mgr);
		
		x = new VarIntLS[N];
		y = new VarIntLS[N];
		o = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 0, W);
			y[i] = new VarIntLS(mgr, 0, H);
			o[i] = new VarIntLS(mgr, 0, 1);
		}
		
		//Rang buoc goi hang i phai nam tron trong thung xe
		for (int i = 0; i < N; i++) {
			//Khong xoay
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(
					new FuncPlus(x[i], w[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(
					new FuncPlus(y[i], h[i]), H)));
			//Xoay
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(
					new FuncPlus(x[i], h[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(
					new FuncPlus(y[i], w[i]), H)));
		}
		
		for(int i = 0; i < N-1; i++) {
			for(int j = i + 1; j < N; j++) {
				//o[i] = 0 and o[j] = 0
				IConstraint[] c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				c[1] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				c[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(o[j], 0)), new OR(c)));
				
				//o[i] = 0 and o[j] = 1
				c[1] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]);
				c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
				S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(o[j], 1)), new OR(c)));
				
				//o[i] = 1 and o[j] = 1
				c[0] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]);
				c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
				S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(o[j], 1)), new OR(c)));
				
				//o[i] = 1 and o[j] = 0
				c[1] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(o[j], 0)), new OR(c)));
			}
		}
		
		mgr.close();
	}
	
	public void search() {
		//HillClimbingSearch searcher = new HillClimbingSearch();
		//searcher.search(S, 10000);
		TabuSearch searcher = new TabuSearch(S);
		searcher.search(10000, 4, 100);
	}
	
	public void printSolution() {
		for(int i = 0; i < N; i++) {
			if(o[i].getValue() == 0)
				System.out.println("item "+i+" -> khong xoay -> "+x[i].getValue()+" "+y[i].getValue());
			else
				System.out.println("item "+i+" -> xoay -> "+y[i].getValue()+ x[i].getValue());
		}
	}
	
	public static void main(String[] args) {
		BinPacking2D App = new BinPacking2D();
		App.stateModel();
		App.search();
		App.printSolution();
	}
}
