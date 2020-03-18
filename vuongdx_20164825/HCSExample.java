package practice;

import localsearch.model.*;
import practice.search.*;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.*;

public class HCSExample {
	private final int n = 5;
	private LocalSearchManager mgr;
	private VarIntLS[] x;
	private ConstraintSystem S;
	
	private HCSExample() {
	}
	
	private void stateModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[5];
		for (int i = 0; i < 5; i++) {
			x[i] = new VarIntLS(mgr, 1, 5);
		}
		S = new ConstraintSystem(mgr);
		
		S.post(new NotEqual(new FuncPlus(x[2], 3), x[1]));
		S.post(new LessOrEqual(x[3], x[4]));
		S.post(new IsEqual(new FuncPlus(x[2], x[3]), new FuncPlus(x[0], 1)));
		S.post(new LessOrEqual(x[4], 3));
		S.post(new IsEqual(new FuncPlus(x[4],x[1]), 7));
		S.post(new Implicate(new IsEqual(x[2], 1), new NotEqual(x[4], 2)));
		
		mgr.close();
	}
	
	private void search() {
		HillClimbingSearch s= new HillClimbingSearch(S);
		s.search(10000);
	}
	
	private void printResult() {
		for (int i = 0; i < this.n; i++) {
			System.out.print(x[i].getValue() + " ");
		}
	}
	
	public static void main(String[] args) {
		HCSExample solver = new HCSExample();
		solver.stateModel();
		solver.search();
		solver.printResult();
	}
}
