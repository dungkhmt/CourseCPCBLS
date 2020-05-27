package cbls115676khmt61.HuyLQ_20161813;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

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
	int L, W, w[], h[], N;
	VarIntLS[] X; //toa do x cua kien hang
	VarIntLS[] Y; //toa do y cua kien hang
	VarIntLS[] O; //huong cua kien hang nam binh thuong hoac xoay 90
	ConstraintSystem S;
	LocalSearchManager mgr;
	public Container() {
		
	}
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		X = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			X[i] = new VarIntLS(mgr, 0, W);
		}
		Y = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			Y[i] = new VarIntLS(mgr, 0, L);
		}
		O = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
		    O[i] = new VarIntLS(mgr, 0, 1);
		}
		
		for (int i = 0; i < N-1; i++) {
			for (int j = i+1; j < N; j++) {
				IConstraint[] or1 = new IConstraint[4];
				IConstraint[] or2 = new IConstraint[4];
				IConstraint[] or3 = new IConstraint[4];
				IConstraint[] or4 = new IConstraint[4];
				
				or1[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				or1[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				or1[2] = new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]);
				or1[3] = new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]);
				
				or2[0] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
				or2[1] = new LessOrEqual(new FuncPlus(X[j], h[j]), X[i]);
				or2[2] = new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]);
				or2[3] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				
				or3[0] = new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]);
				or3[1] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
				or3[2] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				or3[3] = new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]);
				
				or4[0] = new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]);
				or4[1] = new LessOrEqual(new FuncPlus(X[j], h[j]), X[i]);
				or4[2] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
				or4[3] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
				
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0)), new OR(or1)));
				S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1)), new OR(or2)));
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0)), new OR(or3)));
				S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1)), new OR(or4)));
			}                                                                                        
		}
		for (int i = 0; i < N; i++) {
			S.post(new Implicate(new IsEqual(O[i], 0), new AND(new LessOrEqual(new FuncPlus(X[i], w[i]), W), new LessOrEqual(new FuncPlus(X[i], w[i]), W))));
			S.post(new Implicate(new IsEqual(O[i], 0), new AND(new LessOrEqual(new FuncPlus(Y[i], h[i]), L), new LessOrEqual(new FuncPlus(Y[i], h[i]), L))));
			S.post(new Implicate(new IsEqual(O[i], 1), new AND(new LessOrEqual(new FuncPlus(X[i], h[i]), W), new LessOrEqual(new FuncPlus(X[i], h[i]), W))));
			S.post(new Implicate(new IsEqual(O[i], 1), new AND(new LessOrEqual(new FuncPlus(Y[i], w[i]), L), new LessOrEqual(new FuncPlus(Y[i], w[i]), L))));
		}
		for (int i = 0; i < N-1; i++) {
			for (int j = i+1; j < N; j++) {
				// neu kien i duoc lay truoc kien j thi kien i co khoang cach den cua gan hon kien j
				// gia su cua container nam tai Y = 0
				// Neu x cua i va j bi de len nhau thi y[i] be hon y[j] be hon
				AND and1 = new AND(new LessOrEqual(X[i], X[j]), new LessOrEqual( X[j], new FuncPlus(X[i],  w[i])));
				AND and2 = new AND(new LessOrEqual(X[j], X[i]), new LessOrEqual( X[i], new FuncPlus(X[j],  w[j])));
				OR or5 = new OR(and1, and2); 
				S.post(new Implicate(or5, new LessOrEqual(Y[i], Y[j])));
			}
		}
		mgr.close();
	}
	
	public void solve() {
		HillClimbingSearch s = new HillClimbingSearch();
		s.search(S, 10000);
	}
	
	public void printSolution() {
		for (int i = 0; i < N; i++) {
			System.out.println("X[" + i + "]: (x, y, o) = " + X[i].getValue() + " " + Y[i].getValue() + " " + O[i].getValue());
		}
	}
	
	public void readData(String name) throws FileNotFoundException {
		Scanner s = new Scanner(new File(name));
		//Cau truc file
		// dong 1: L, W, N
		// dong 2 -> N + 1: i, w[i], h[i]
		L = s.nextInt();
		W = s.nextInt();
		N = s.nextInt();
		w = new int[N];
		h = new int[N];
		for (int i = 0; i < N; i++) {
			w[i] = s.nextInt();
			h[i] = s.nextInt();
		}
		s.close();
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		Container container = new Container();
		container.readData("D:\\Document\\Java\\Test-CBLS\\File\\container_6_1.txt");
		container.stateModel();
		container.solve();
		container.printSolution();
	}
}
