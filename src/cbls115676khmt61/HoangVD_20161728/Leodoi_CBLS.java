package cbls115676khmt61.HoangVD_20161728;

import java.util.Random;


import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Leodoi_CBLS {
	LocalSearchManager mgr;
	VarIntLS[] X; //decision varibles
	ConstraintSystem S;
	Random R;
	
	private void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[5];
		for (int i = 0; i < 5; i++) {
			X[i] = new VarIntLS(mgr, 1, 5);
		}
		//random solution
		R = new Random();
		for (int i = 0; i < 5; i++) {
			X[i].setValuePropagate(R.nextInt(X[i].getMaxValue() - X[i].getMinValue() + 1) + X[i].getMinValue());
		}
		S = new ConstraintSystem(mgr);
		S.post(new NotEqual(new FuncPlus(X[2], 3), X[1]));
		S.post(new LessOrEqual(X[3], X[4]));
		S.post(new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0], 1)));
		S.post(new LessOrEqual(X[4], 3));
		S.post(new IsEqual(new FuncPlus(X[4], X[1]), 7));
		S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));
		mgr.close();
	}
	public void printSol() {
		for (int i = 0; i < 5; i++)
			System.out.print(X[i].getValue()+ " ");
		System.out.println();
	}
//	public void localSearch() {
//		printSol();
//		System.out.println("init, S = " + S.violations());
//		int it = 1;
//		while(it < 10000 && S.violations() > 0) {
//			MinMaxSelector mms = new MinMaxSelector(S);
//			VarIntLS sel_x = mms.selectMostViolatingVariable();
//			int sel_value = mms.selectMostPromissingValue(sel_x);
//			sel_x.setValuePropagate(sel_value); //local move: asign value & propagate update violations by dependency graph
//			System.out.println("Step " + it + ", S = " + S.violations());
//			it++;
//		}
//		System.out.print("Best solution: ");
//		printSol();
//	}
	public void hillClimbingSearch() {
		stateModel();
		printSol();
		System.out.println("init, S = " + S.violations());
		HillClimbingSearch h = new HillClimbingSearch();
		h.search(S, 10000);
		System.out.print("Best solution: ");
		printSol();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Leodoi_CBLS leodoi_CBLS = new Leodoi_CBLS();
		leodoi_CBLS.hillClimbingSearch();
	}

}
