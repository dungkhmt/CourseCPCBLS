package cbls115676khmt61.DuongLeGiang_20161164;

import localsearch.model.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.*;
import localsearch.search.TabuSearch;
import localsearch.selectors.*;
import localsearch.constraints.basic.*;
import java.io.PrintWriter;
import java.util.*;

public class CSP {
	public static void main(String[] argc) {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[5];
		for(int i = 0; i < X.length; i++ ) {
			X[i] = new VarIntLS(mgr, 1, 5);
		}
		ConstraintSystem CS = new ConstraintSystem(mgr);
		
		CS.post(new NotEqual(new FuncPlus(X[2],3), X[1]));
		CS.post(new LessOrEqual(X[3], X[4]));
		CS.post(new IsEqual(new FuncPlus(X[2],X[3]), new FuncPlus(X[0],1)));
		CS.post(new LessOrEqual(X[4], 3));
		CS.post(new IsEqual(new FuncPlus(X[4],X[1]), 7));
		CS.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));
		
		mgr.close();
		
		HillClimbingSearch search = new HillClimbingSearch();
		search.search(CS, 100000);
		
		for(int i = 0; i < X.length; i++) {
			System.out.println("X[" + i + "] = " + X[i].getValue());
		}
	}
}