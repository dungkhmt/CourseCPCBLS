package BinPacking2D;

import Leodoi.HillClimbingSearch;
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

public class BinPacking2D_CBLS {
	int N = 3;
	int W = 4;
	int H = 6;
	int[] w = {3, 3, 1};
	int[] h = {2, 4, 6};
//	int N = 6;
//	int W = 10;
//	int H = 7;
//	int[] w = {6,5,2,3,3,2};
//	int[] h = {3,2,4,4,3,1};
	ConstraintSystem S;
	VarIntLS[] X;
	VarIntLS[] Y;
	VarIntLS[] O;
	LocalSearchManager mgr;
	public BinPacking2D_CBLS() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
	}
	public void stateModel() {
		X = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			X[i] = new VarIntLS(mgr, 0, W);
		}
		Y = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			Y[i] = new VarIntLS(mgr, 0, H);
		}
		O = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
		    O[i] = new VarIntLS(mgr, 0, 1);
		}
		for (int i = 0; i < N-1; i++) {
			for (int j = i+1; j < N; j++) {
				IConstraint[] or1 = new IConstraint[2];
				IConstraint[] or2 = new IConstraint[2];
				IConstraint[] or3 = new IConstraint[2];
				IConstraint[] or4 = new IConstraint[2];
				IConstraint[] or5 = new IConstraint[2];
				IConstraint[] or6 = new IConstraint[2];
				IConstraint[] or7 = new IConstraint[2];
				IConstraint[] or8 = new IConstraint[2];
				
				or1[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				or1[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				or2[0] = new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]);
				or2[1] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				IConstraint and1 = new AND(new OR(or1), new OR(or2));
				
				or3[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				or3[1] = new LessOrEqual(new FuncPlus(X[j], h[j]), X[i]);
				or4[0] = new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]);
				or4[1] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				IConstraint and2 = new AND(new OR(or3), new OR(or4));
				

				or5[0] = new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]);
				or5[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				or6[0] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				or6[1] = new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]);
				IConstraint and3 = new AND(new OR(or5), new OR(or6));
				
				
				or7[0] = new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]);
				or7[1] = new LessOrEqual(new FuncPlus(X[j], h[j]), X[i]);
				or8[0] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				or8[1] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				IConstraint and4 = new AND(new OR(or8), new OR(or7));
				
				
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0)), and1));
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1)), and2));
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0)), and3));
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1)), and4));
			}
		}
		for (int i = 0; i < N; i++) {
			S.post(new Implicate(new IsEqual(O[i], 0), new AND(new LessOrEqual(new FuncPlus(X[i], w[i]), W), new LessOrEqual(new FuncPlus(Y[i], h[i]), H))));
			S.post(new Implicate(new IsEqual(O[i], 1), new AND(new LessOrEqual(new FuncPlus(X[i], h[i]), W), new LessOrEqual(new FuncPlus(Y[i], w[i]), H))));
		}
		mgr.close();
	}
	public void printSolution() {
		for (int i = 0; i < N; i++) {
			System.out.print("(" + X[i].getValue() + "," + Y[i].getValue() + "," + O[i].getValue() + ") ");
		}
	}
	public void search() {
		HillClimbingSearch h = new HillClimbingSearch();
		h.search(S, 100000);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BinPacking2D_CBLS binPacking2D_CBLS = new BinPacking2D_CBLS();
		binPacking2D_CBLS.stateModel();
		binPacking2D_CBLS.search();
		binPacking2D_CBLS.printSolution();
	}

}
