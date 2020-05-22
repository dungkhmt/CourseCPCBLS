package localsearch.applications.inclass;

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

public class BinBacking2D {
	
	int W = 4;
	int H = 6;
	int N = 3;
	int[] w = {3,3,1};
	int[] h = {2,4,6};
	
	
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] X, Y, O;
	
	private void stateModel(){
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		Y = new VarIntLS[N];
		O = new VarIntLS[N];
		
		for(int i = 0; i < N; i++){
			X[i] = new VarIntLS(mgr, 0, W);
			Y[i] = new VarIntLS(mgr, 0, H);
			O[i] = new VarIntLS(mgr, 0, 1);
		}
		
		S = new ConstraintSystem(mgr);
		for(int i = 0; i < N-1; i++){
			for(int j = i+1; j < N; j++){
			//	IConstraint c = new Implicate(new IsEqual(O[i], 0), new LessOrEqual(new FuncPlus(X[i],w[i]),
			//			new FuncPlus(new AND(W,Y[i]), new LessOrEqual(h[i], H) )));
			/*	IConstraint c1 = new IsEqual(O[i], 0);
				IConstraint c2 = new LessOrEqual(new FuncPlus(X[i],w[i]), W);
				IConstraint c3 = new LessOrEqual(new FuncPlus(Y[i],h[i]), H);
				IConstraint c = new Implicate(c1,new AND(c2,c3));
				S.post(c);
				*/
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new IsEqual(O[i],0);
				c1[1] = new IsEqual(O[j], 0);
				IConstraint c2 = new AND(c1);
				
				IConstraint[] c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				c3[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				c3[2] = new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]);
				c3[4] = new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]);
				
				IConstraint c4 = new OR(c3);
				
				S.post(new Implicate(c2,c4));
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(O[i],0);
				c1[1] = new IsEqual(O[j], 1);
				c2 = new AND(c1);
				c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				c3[1] = new LessOrEqual(new FuncPlus(X[j], h[j]), X[i]);
				c3[2] = new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]);
				c3[4] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				c4 = new OR(c3);
				
				S.post(new Implicate(c2,c4));
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(O[i],1);
				c1[1] = new IsEqual(O[i],0);
				
				c2 = new AND(c1);
				
				c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]);
				c3[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				c3[2] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				c3[4] = new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]);
				c4 = new OR(c3);
				
				S.post(new Implicate(c2,c4));
				
				
				c1 = new IConstraint[2];
				c1[0] = new IsEqual(O[i],1);
				c1[1] = new IsEqual(O[i],1);
				
				c2 = new AND(c1);
				
				c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]);
				c3[1] = new LessOrEqual(new FuncPlus(X[j], h[j]), X[i]);
				c3[2] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				c3[4] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				
				c4 = new OR(c3);
				S.post(new Implicate(c2,c4));
			}
		}
	}
	
	private void search(){
		
	}
	
	private void solve(){
		
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
