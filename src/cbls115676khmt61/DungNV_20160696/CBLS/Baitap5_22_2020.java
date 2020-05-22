package baitap;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Baitap5_22_2020 {
	/*
	 * Bien X[i] bieu dien thung ma chat long i duoc do vao
	 * Mien gia tri D(X[i]) = {0,2,...,N_Barrel-1) voi moi i = 0:N_Barrel-1
	 * Rang buoc:
	 * 		X[i] != X[j] voi moi i, j thuoc ban_list2
	 * 		X[i] != X[j] AND X[i] != X[k] voi moi i, j, k thuoc ban_list3
	 * 		Sum(X[i] = j)*V_liquid(i) <= V_Barrel voi j = 0,2,...N_Barrel - 1
	 */
	
	int N_Liquid = 20;
	int N_Barrel = 5;
	int[] V_Liquid = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20
					,25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[] V_Barrel = {60, 70, 90, 80, 100};
	
	int [][] ban_list2 = {
			{0,1},
			{7,8},
			{12,17},
			{8,9},
			};
	
	int [][] ban_list3 = {
			{1, 2, 9},
			{0, 9, 12}
			
	};
	//model
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	IFunction[] V_liquid_per_barrel; //The tich moi loai chat long trong 1 thung
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N_Liquid];
		for(int i = 0; i < N_Liquid; i++)
			X[i] = new VarIntLS(mgr, 0, N_Barrel-1);
		
		S = new ConstraintSystem(mgr);
		for(int k = 0; k < ban_list2.length; k++) {
			IConstraint c = new NotEqual(X[ban_list2[k][0]], X[ban_list2[k][1]]);
			S.post(c);
		}
		
		for(int k = 0; k < ban_list3.length; k++) {
			IConstraint c = new AND(new NotEqual(X[ban_list3[k][0]], X[ban_list3[k][1]]), new NotEqual(X[ban_list3[k][0]], X[ban_list3[k][2]]));
			S.post(c);
		}
		
		V_liquid_per_barrel = new IFunction[N_Barrel];

		
		for(int j = 0; j < N_Barrel; j++) {
			V_liquid_per_barrel[j] = new ConditionalSum(X, V_Liquid, j);
			
			S.post(new LessOrEqual(V_liquid_per_barrel[j], V_Barrel[j]));
		}
		
		mgr.close();
	}
	
	public void search() {
		TabuSearch searcher = new TabuSearch(S);
		searcher.search(10000, 4, 100);
	}
	
	public void printResult() {
		for(int j = 0; j < N_Barrel; j++) {
			System.out.println("Barrel: "+ j + ": ");
			for(int i = 0; i < N_Liquid; i++) 
				if (X[i].getValue() == j) {
					System.out.print(i + ", ");
				}
			System.out.println("current_V = " + V_liquid_per_barrel[j].getValue());
		}
	}
	
	public static void main(String[] args) {
		Baitap5_22_2020 App = new Baitap5_22_2020();
		App.stateModel();
		App.search();
		App.printResult();
	}
}
