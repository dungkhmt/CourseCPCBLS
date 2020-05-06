package CBLS;

import localsearch.model.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.*;
import localsearch.search.TabuSearch;
import localsearch.selectors.*;

import java.io.PrintWriter;
import java.util.*;

public class Queen {
	//data structures input 
	int N;
	
	//modeling
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	
	public Queen(int i) {
		this.N = i;
	}

	private void stateNode() {
		
		mgr = new LocalSearchManager();
		
		//Khoi tao cau hinh bai toan
		X = new VarIntLS[N];
		for(int i = 0 ; i < N; i++)
			X[i] = new VarIntLS(mgr, 1, N);
		
		//Khai bao rang buoc
		S = new ConstraintSystem(mgr);
		IConstraint c = new AllDifferent(X);
		S.post(c);
		
		IFunction[] f1 = new IFunction[N];
		for(int i = 0 ; i < N; i++)
			f1[i] = new FuncPlus(X[i], i);
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[N];
		for(int i = 0 ; i < N; i++)
			f2[i] = new FuncPlus(X[i], -i);
		S.post(new AllDifferent(f2));
		
		mgr.close();		
	}
	
	public void printSol() {
		for(int i = 0; i < N; i++)
			System.out.print(X[i].getValue() + " ");
		//System.out.println();
	}
	
	private void localSearch() {
		printSol();
		System.out.println("init, S = " + S.violations());
		int it = 1;
		
		while (it < 10000 && S.violations() > 0) {
			
			MinMaxSelector mms = new MinMaxSelector(S);
			
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			
			int sel_value = mms.selectMostPromissingValue(sel_x);
			
			sel_x.setValuePropagate(sel_value); // local move: assign value, propagate update violations
			System.out.println("Step "+ it + " , S = "+ S.violations());
			it++;
		}
		System.out.print("Best Solution: ");
		printSol();
	}
	
	public static void main(String[] args) {
		Queen app = new Queen(8);
		app.stateNode();
		app.localSearch();
	}
}
