package baitap;

import localsearch.model.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.*;
import localsearch.search.TabuSearch;
import localsearch.selectors.*;
import localsearch.constraints.basic.*;
import java.io.PrintWriter;
import java.util.*;
import localsearch.functions.conditionalsum.ConditionalSum;

public class DungDich {
	
	int N = 20;
	int Nb = 5;
	int[] V_Liquid = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[] Dung_tich = {60, 70, 90, 80, 100};
	
	int [][] pre1 = {
			{0,1},
			{7,8},
			{12,17},
			{8,9},
	};
	
	int [][] pre2 = {
			{1, 2, 9},
			{0, 9, 12}
	};
	
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	IFunction[] Vbin;
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for(int i = 0; i < N; i++)
			X[i] = new VarIntLS(mgr, 0, Nb-1);
		
		S = new ConstraintSystem(mgr);
		for(int k = 0; k < pre1.length; k++) {
			IConstraint c = new NotEqual(X[pre1[k][0]], X[pre1[k][1]]);
			S.post(c);
		}
		
		Vbin = new IFunction[Nb];
		
		for(int j = 0; j < Nb; j++) {
			Vbin[j] = new ConditionalSum(X, V_Liquid, j);
			S.post(new LessOrEqual(Vbin[j], Dung_tich[j]));
		}
		
		mgr.close();
	}
	
	public void search() {
		MyTabuSearch searcher = new MyTabuSearch(S);
        searcher.search(10000, 60, 100);
	}
	
	public void printResult() {
		for(int j = 0; j < Nb; j++) {
			System.out.println("Barrel: "+ j + ": ");
			for(int i = 0; i < N; i++) 
				if (X[i].getValue() == j) {
					System.out.print(i + ", ");
				}
			
			System.out.println("The tich hien tai = " + Vbin[j].getValue());
		}
	}
	
	public static void main(String[] args) {
		DungDich App = new DungDich();
		App.stateModel();
		App.search();
		App.printResult();
	}
}
