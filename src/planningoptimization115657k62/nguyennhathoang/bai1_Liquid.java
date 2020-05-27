package planningoptimization115657k62.nguyennhathoang;

import localsearch.baitap.MySum;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.*;

public class bai1_Liquid {
	
	
	int N=20;
	int M=5;
	int[] V = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[] Dung_tich = {60, 70, 80, 90, 100};
	
	int[][] dieukien_1 = {
			{0,1},
			{7,8},
			{12,17},
			{8,9},
	};
	
	int[][] dieukien_2 = {
			{1, 2, 9},
			{0, 9, 12}
	};
	
	LocalSearchManager lsm;
	VarIntLS[] X;
	ConstraintSystem CS;
	IFunction[] V1;
	
	
	
	
	public void Model() {
		lsm= new LocalSearchManager();
		X= new VarIntLS[N];
		for(int i = 0; i < N; i++)
			X[i] = new VarIntLS(lsm, 0, M-1);
		
		CS = new ConstraintSystem(lsm);
		for(int k = 0; k < dieukien_1.length; k++) {
			IConstraint c = new NotEqual(X[dieukien_1[k][0]], X[dieukien_1[k][1]]);
			CS.post(c);
		}
		
		for(int k = 0; k < dieukien_2.length; k++) {
			IConstraint c = new AND(new NotEqual(X[dieukien_2[k][0]], X[dieukien_2[k][1]]), new NotEqual(X[dieukien_2[k][0]], X[dieukien_2[k][2]]));
			CS.post(c);
		}
		
		V1 = new IFunction[M];
		
		for(int j = 0; j < M; j++) {
			V1[j] = new ConditionalSum(X, V, j);
			
			CS.post(new LessOrEqual(V1[j], Dung_tich[j]));
		}
		
		lsm.close();
		
		
	}
	
	public void search() {
		
		
		
		MyTabuSearch searcher = new MyTabuSearch(CS);
        searcher.search( 10000, 4, 100);
	}
	
	public void InKetQua() {
		for(int j = 0; j < M; j++) {
			System.out.println("Thung: "+ j + ": ");
			for(int i = 0; i < N; i++) 
				if (X[i].getValue() == j) {
					System.out.print(i + ", ");
				}
			
			System.out.println("The tich  = " + V1[j].getValue());
		}
	}
	
	
	
	
	
	
	public static void main(String[] args) {
		bai1_Liquid App = new bai1_Liquid();
		App.Model();
		App.search();
		App.InKetQua();
		
	}

}
