package CBLS;

import localsearch.model.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.*;
import localsearch.functions.sum.Sum;
import localsearch.search.TabuSearch;
import localsearch.selectors.*;

import java.io.PrintWriter;
import java.util.*;

public class CSP_HCS {
	public static void main(String[] args) {
		//modeling
		LocalSearchManager mgr;
		VarIntLS[] X;
		ConstraintSystem S;
		
		mgr = new LocalSearchManager();
		
		//Khoi tao cau hinh bai toan
		X = new VarIntLS[5];
		for(int i = 0 ; i < 5; i++)
			X[i] = new VarIntLS(mgr, 1, 5);
		
		//Khai bao rang buoc
		S = new ConstraintSystem(mgr);
		S.post(new NotEqual(new FuncPlus(X[2],3), X[1]));
		S.post(new LessOrEqual(X[3], X[4]));
		S.post(new IsEqual(new FuncPlus(X[2],X[3]), new FuncPlus(X[0],1)));
		S.post(new LessOrEqual(X[4], 3));
		S.post(new IsEqual(new FuncPlus(X[4],X[1]), 7));
		S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));
		S.post(new LessOrEqual(7, new Sum(X)));
		
		mgr.close();	
		
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 10000);
		for(int i = 0; i < X.length; i++)
			System.out.println("X["+i+"] = "+ X[i].getValue());
	}
}
