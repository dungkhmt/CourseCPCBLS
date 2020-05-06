package cbls115676khmt61.DuongLeGiang_20161164;

import localsearch.model.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.*;
import localsearch.search.TabuSearch;
import localsearch.selectors.*;
import localsearch.constraints.basic.*;
import java.io.PrintWriter;
import java.util.*;

public class Num_bt {
	int N;
	
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	
	public Num_bt(int N) {
		this.N = N;
	}

	private void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for(int i=0; i<N; i++) {
			X[i] = new VarIntLS(mgr, i+1, N);
		}
		S = new ConstraintSystem(mgr);
		S.post(new NotEqual(new FuncPlus(X[2],3), X[1]));
		S.post(new LessOrEqual(X[3], X[4]));
		S.post(new IsEqual(new FuncPlus(X[2],X[3]), new FuncPlus(X[0],1)));
		S.post(new LessOrEqual(X[4], 3));
		S.post(new IsEqual(new FuncPlus(X[4],X[1]), 7));
		S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));
		
		mgr.close();
	}
	
	public void printSol() {
		for(int i=0; i<N; i++) {
			System.out.print(X[i].getValue() + " ");
		}
		System.out.println();
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
		Num_bt app = new Num_bt(5);
		app.stateModel();
		app.localSearch();
	}
}











