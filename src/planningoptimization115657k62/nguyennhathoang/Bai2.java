package _assigment;

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
import tkcucbo.HillClimbingSearch;

public class Bai2 {
	
	int N = 6;
	int L = 6;
	int W = 4;
	int[] l = {4,1,2,1,4,3};
	int[] w = {1,3,2,3,1,2};
	LocalSearchManager lsm;
	ConstraintSystem S;
	VarIntLS[] X;
	VarIntLS[] Y;
	VarIntLS[] O;
	
	
	public Bai2() {
		lsm = new LocalSearchManager();
		S = new ConstraintSystem(lsm);
	}
	
	public void init() {
		
		X = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			X[i] = new VarIntLS(lsm, 0, W);
		}
		
		Y = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			Y[i] = new VarIntLS(lsm, 0, L);
		}
		
		O = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
		    O[i] = new VarIntLS(lsm, 0, 1);
		}
		
		
		
		for (int i = 0; i < N-1; i++) {
			for (int j = i+1; j < N; j++) {
				IConstraint[] c1 = new IConstraint[2];
				IConstraint[] c2 = new IConstraint[2];
				IConstraint[] c3 = new IConstraint[2];
				IConstraint[] c4 = new IConstraint[2];
				IConstraint[] c5 = new IConstraint[2];
				IConstraint[] c6 = new IConstraint[2];
				IConstraint[] c7 = new IConstraint[2];
				IConstraint[] c8 = new IConstraint[2];
				
				c1[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				c1[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				c2[0] = new LessOrEqual(new FuncPlus(Y[i], l[i]), Y[j]);
				c2[1] = new LessOrEqual(new FuncPlus(Y[j], l[j]), Y[i]);
				IConstraint and1 = new AND(new OR(c1), new OR(c2));
				
				c3[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				c3[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				c4[0] = new LessOrEqual(new FuncPlus(Y[i], l[i]), Y[j]);
				c4[1] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				IConstraint and2 = new AND(new OR(c3), new OR(c4));
				

				c5[0] = new LessOrEqual(new FuncPlus(X[i], l[i]), X[j]);
				c5[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				c6[0] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				c6[1] = new LessOrEqual(new FuncPlus(Y[j], l[j]), Y[i]);
				IConstraint and3 = new AND(new OR(c5), new OR(c6));
				
				
				c7[0] = new LessOrEqual(new FuncPlus(X[i], l[i]), X[j]);
				c7[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				c8[0] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				c8[1] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				IConstraint and4 = new AND(new OR(c8), new OR(c7));
				
				
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0)), and1));
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1)), and2));
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0)), and3));
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1)), and4));
			}
		}
		
		for (int i = 0; i < N; i++) {
			S.post(new Implicate(new IsEqual(O[i], 0), new AND(new LessOrEqual(new FuncPlus(X[i], w[i]), W), new LessOrEqual(new FuncPlus(Y[i], l[i]), L))));
			S.post(new Implicate(new IsEqual(O[i], 1), new AND(new LessOrEqual(new FuncPlus(X[i], l[i]), W), new LessOrEqual(new FuncPlus(Y[i], w[i]), L))));
		}
		// rang buoc goi hang i lay ra truoc goi hang j      
		for(int i = 0; i < N - 1; i++) {
			for(int j = i+1; j < N; j++) {
				IConstraint c1;
				if (w[i] + w[j] <= W) {
					c1= new LessOrEqual(X[i], X[j])	;
				}else {
					c1 = new LessOrEqual(Y[i], Y[j]);
				}
				 
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0)), c1));
				
				if(w[i] + l[j] <= W) {
					c1 = new LessOrEqual(X[i], X[j]);
				}else {
					c1 = new LessOrEqual(Y[i], Y[j]);
				}
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1)), c1));
				
				if(l[i] + w[j] <= W) {
					c1 = new LessOrEqual(X[i], X[j]);
				}else {
					c1 = new LessOrEqual(Y[i], Y[j]);
				}
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0)), c1));
				
				if(l[i] + l[j] <= W) {
					c1 = new LessOrEqual(X[i], X[j]);
				}else {
					c1 = new LessOrEqual(Y[i], Y[j]);
				}
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1)), c1));
			}
		}
		
		lsm.close();
	}
	
	
	public void search() {
		HillClimbingSearch h = new HillClimbingSearch();
		h.search(S, 100000);
	}
	
	public void solver() {
		for (int i = 0; i < N; i++) {
			System.out.print("(" + X[i].getValue() + "," + Y[i].getValue() + "," + O[i].getValue() + ") ");
		}
	}
	
	public static void main(String[] args) {
		Bai2 container = new Bai2();
		container.init();
		container.search();
		container.solver();
	}
}
