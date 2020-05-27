package cbls115676khmt61.HoangVD_20161728;

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
	/*
	 * Problem
	 * mot xe container can van chuyen 6 kien hang: 0,1,2,3,4,5
	 * den cac diem theo thu tu do
	 * kich thuoc xe: L = 6m, W=4m
	 * cho chieu dai rong cac kien hang
	 * Constraint:
	 * oi = 0 -> xi + wi <= W and yi + li <= L
	 * oi = 1 -> yi + li <= W and yi + wi <= L
	 * */
	int N = 6;
	int L = 6;
	int W = 4;
	int[] l = {4,1,2,1,4,3};
	int[] w = {1,3,2,3,1,2};
	ConstraintSystem S;
	VarIntLS[] X;
	VarIntLS[] Y;
	VarIntLS[] O;
	LocalSearchManager mgr;
	
	public Container() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
	}
	
	public void stateModel() {
		// coi W la truc Ox, L theo truc Oy
		// hoanh do cua cac kien hang
		X = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			X[i] = new VarIntLS(mgr, 0, W);
		}
		// tung do cua cac kien hang
		Y = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			Y[i] = new VarIntLS(mgr, 0, L);
		}
		// huong cua cac kien hang
		// o = 0: khong xoay
		// o = 1: xoay
		O = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
		    O[i] = new VarIntLS(mgr, 0, 1);
		}
		
		// rang buoc hai goi hang i, j khong nam chong cheo len nhau
		
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
				or2[0] = new LessOrEqual(new FuncPlus(Y[i], l[i]), Y[j]);
				or2[1] = new LessOrEqual(new FuncPlus(Y[j], l[j]), Y[i]);
				IConstraint and1 = new AND(new OR(or1), new OR(or2));
				
				or3[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				or3[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				or4[0] = new LessOrEqual(new FuncPlus(Y[i], l[i]), Y[j]);
				or4[1] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				IConstraint and2 = new AND(new OR(or3), new OR(or4));
				

				or5[0] = new LessOrEqual(new FuncPlus(X[i], l[i]), X[j]);
				or5[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				or6[0] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				or6[1] = new LessOrEqual(new FuncPlus(Y[j], l[j]), Y[i]);
				IConstraint and3 = new AND(new OR(or5), new OR(or6));
				
				
				or7[0] = new LessOrEqual(new FuncPlus(X[i], l[i]), X[j]);
				or7[1] = new LessOrEqual(new FuncPlus(X[j], l[j]), X[i]);
				or8[0] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				or8[1] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				IConstraint and4 = new AND(new OR(or8), new OR(or7));
				
				
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0)), and1));
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1)), and2));
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0)), and3));
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1)), and4));
			}
		}
		// rang buoc goi hang i nam tron trong container
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
		Container container = new Container();
		container.stateModel();
		container.search();
		container.printSolution();
	}
}
