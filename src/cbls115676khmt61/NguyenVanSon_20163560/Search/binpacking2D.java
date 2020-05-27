package cbls115676khmt61.NguyenVanSon_20163560.Search;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.Icon;
public class binpacking2D {
	int W = 4;
	int H = 6;
	int N = 3;
	int[] w = {3,3,1};
	int[] h = { 2,4,6};
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] x;
	VarIntLS[] y;
	VarIntLS[] o;
	
	private void statemodel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		x = new VarIntLS[N];
		y = new VarIntLS[N];
		o = new VarIntLS[N];
		for(int i = 0;i < N ;i ++) {
			x[i]  =new VarIntLS(mgr, 0,W);
			y[i] = new VarIntLS(mgr, 0,H);
			o[i] =new VarIntLS(mgr, 0,1);
		}
		for(int i = 0; i< N; i++) {
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(x[i], w[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(
					new FuncPlus(y[i], h[i]), H)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(
					new FuncPlus(x[i], h[i]), W)));
			S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(
					new FuncPlus(y[i], w[i]), H)));
		}
		
		
		for (int i = 0; i < N - 1; i++) {
			for (int j = i + 1; j < N; j++) {
				// o[i] = 0, o[j] = 0 (no orientation)
				IConstraint[] c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]); // l1.x>r2.x
				c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]); // l2.x>r1.x
				c[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]); // l1.y<r2.y
				c[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]); // l2.y<r1.y
				S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(
						o[j], 0)), new OR(c)));
				
				// o[i] = o, o[j] = 1
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]); // l1.x>r2.x
				c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]); // l2.x>r1.x
				c[2] = new LessOrEqual(new FuncPlus(y[i], h[i]), y[j]); // l1.y<r2.y
				c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]); // l2.y<r1.y
				S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(
						o[j], 1)), new OR(c)));

				// o[i] = 1, o[j] = 0
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]); // l1.x>r2.x
				c[1] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]); // l2.x>r1.x
				c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]); // l1.y<r2.y
				c[3] = new LessOrEqual(new FuncPlus(y[j], h[j]), y[i]); // l2.y<r1.y
				S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(
						o[j], 0)), new OR(c)));

				// o[i] = 1, o[j] = 1
				c = new IConstraint[4];
				c[0] = new LessOrEqual(new FuncPlus(x[j], h[j]), x[i]); // l1.x>r2.x
				c[1] = new LessOrEqual(new FuncPlus(x[i], h[i]), x[j]); // l2.x>r1.x
				c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]); // l1.y<r2.y
				c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]); // l2.y<r1.y
				S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(
						o[j], 1)), new OR(c)));
			}
		}
		mgr.close();
	}
	public void search(int num) {
		HillClimbingSearch s = new HillClimbingSearch();
		s.search(S, num);
		
	}
	public void print() {
		for (int i = 0; i < N; i++) {
			System.out.println("item " + (i + 1) + " :  " + x[i].getValue()
					+ " " + y[i].getValue() + " ->  " + (w[i]) + " " + (h[i])
					+ " " + o[i].getValue());
		}
	}
	public static void main(String[] args) {
		binpacking2D b = new binpacking2D();
		b.statemodel();
		b.search(1000);
		b.print();
	}
	
}

