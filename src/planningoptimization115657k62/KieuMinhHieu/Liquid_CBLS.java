package planningoptimization115657k62.KieuMinhHieu;


import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;


public class Liquid_CBLS {
	LocalSearchManager mgr;
	VarIntLS[][] X;
	ConstraintSystem S;
	
	int N = 20;
	int M = 5;
	int limit[] = {60, 70, 80, 90, 100};
	int V[] = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 
			20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	
	int[] oneM = {1,1,1,1,1};
	int[][] conf = { {0,1,-1},
					{7,8,-1},
					{12,17,-1},
					{8,9,-1},
					{1,2,9},
					{0,9,12}};
	
	
	public void stateModel(){
		mgr = new LocalSearchManager();
		X = new VarIntLS[M][N];
		
		for (int i = 0; i < M; i++) 
			for (int j = 0; j< N; j++) 
				X[i][j] = new VarIntLS(mgr,0,1);
		
		S = new ConstraintSystem(mgr);
		
		for(int i = 0; i < M; i++){
			IFunction[] y = new IFunction[M];
			for(int j = 0; j < N; j++) 
			
		}
	}

}
