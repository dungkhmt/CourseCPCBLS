package cbls115676khmt61.DoNgocSon_20163506;

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

public class Container {
	int W = 4;
	int L = 6;
	int N = 6;
	int[] w = {1, 3, 2, 3, 1, 2};
	int[] h = {4, 1, 2, 1, 4, 3};
	
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] x, y, o;
	
	private void stateModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[N];
		y = new VarIntLS[N];
		o = new VarIntLS[N];
		
		for (int i=0; i<N; i++) {
			x[i] = new VarIntLS(mgr, 0, W-1);
			y[i] = new VarIntLS(mgr, 0, L-1);
			o[i] = new VarIntLS(mgr, 0, 1);
		}
		
		S = new ConstraintSystem(mgr);
		for (int i=0; i<N; i++) {
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(x[i], w[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(y[i], h[i]), L)));
			
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(x[i], h[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(y[i], w[i]), L))); 			
		}
		
		for (int i=0; i<N-1; i++)
			for (int j=i+1; j<N; j++) {
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
		
		for (int i=0; i<N-1; i++)
			for (int j=i+1; j<N; j++) {
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
		
		for (int i=0; i<N-1; i++)
			for (int j=i+1; j<N; j++) {
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
		
		for (int i=0; i<N-1; i++)
			for (int j=i+1; j<N; j++) {
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
		
		for(int i = 0 ; i < N-1 ; i ++)
			for(int j = i + 1 ; j < N ; j ++) {
				IConstraint c0 = new Implicate(new IsEqual(o[j], 1),
						new OR(new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]), 
						new OR(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(x[i], h[i]), x[j])), new LessOrEqual(y[i], y[j]))));
				
				IConstraint c1 = new Implicate(new IsEqual(o[j], 1),
						new OR(new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]), 
						new OR(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(x[i], w[i]), x[j])), new LessOrEqual(y[i], y[j]))));
				
				IConstraint c2 = new Implicate(new IsEqual(o[j], 0),
						new OR(new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]), 
						new OR(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(x[i], h[i]), x[j])), new LessOrEqual(y[i], y[j]))));
				
				IConstraint c3 = new Implicate(new IsEqual(o[j], 0),
						new OR(new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]), 
						new OR(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(x[i], w[i]), x[j])), new LessOrEqual(y[i], y[j]))));
				
				S.post(c0);
				S.post(c1);
				S.post(c2);
				S.post(c3);
			}
		
		mgr.close();
	}
	
	private void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 10000);
	}
	
	private void print() {
		char[][] p = new char[W][L];
		for (int i=0; i<W; i++)
			for (int j=0; j<L; j++)
				p[i][j] = '.';
		for (int i=0; i<N; i++) {
//			System.out.println(Integer.toString(x[i].getValue()) + ' '
//					 + Integer.toString(y[i].getValue()) + ' '
//					 + Integer.toString(o[i].getValue()));
			if (o[i].getValue() == 0) {
				for (int j=x[i].getValue(); j<x[i].getValue()+w[i]; j++)
					for (int k=y[i].getValue(); k<y[i].getValue()+h[i]; k++)
						p[j][k] = (char) (i + '0');
			}
			else {
				for (int j=x[i].getValue(); j<x[i].getValue()+h[i]; j++)
					for (int k=y[i].getValue(); k<y[i].getValue()+w[i]; k++)
						p[j][k] = (char) (i + '0');
			}
		}
		
		for (int i=0; i<W; i++)
			System.out.println(p[i]);
	}
	
	public void solve() {
		stateModel();
		search();
		print();
	}
	
	public static void main(String[] args) {
		Container app = new Container();
		app.solve();
	}
}
