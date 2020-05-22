package BaiTap;


import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class binpacking2D {
	int W=4;
	int H=6;
	int N=3;
	int[] w = {3,3,1};
	int[] h = {2,4,6};
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] x;
	VarIntLS[] y;
	VarIntLS[] o;
	public void stateModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[N];
		y = new VarIntLS[N];
		o = new VarIntLS[N];
		for(int i =0;i<N;i++) {
			x[i] = new VarIntLS(mgr, 0, W);
			y[i] = new VarIntLS(mgr, 0, H);
			o[i] = new VarIntLS(mgr, 0, 1);
		}
		S = new ConstraintSystem(mgr);
		for(int i=0;i<N-1;i++)
			for(int j =i+1; j<N;j++) {
				//items i and j cannot overlap
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new LessOrEqual(o[i], 0);
				c1[1] = new LessOrEqual(o[j], 0);
				IConstraint c2 = new AND(c1);
				
				IConstraint[] c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
				c3[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]);
				c3[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]);
				IConstraint c4 = new OR(c3);
				
				S.post(new Implicate(c2, c4));
			}
		mgr.close();
	}
	
	public void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.LocalSearch(S, 10000);
	}
	
	public void printRS() {
		for(int i =0;i<N;i++) {
			System.out.println(x[i].getValue()+" "+ y[i].getValue()+" "+o[i].getValue());
		}
	}
	
	public static void main(String[] args) {
		binpacking2D app = new binpacking2D();
		app.stateModel();
		app.search();
		app.printRS();
	}
}
