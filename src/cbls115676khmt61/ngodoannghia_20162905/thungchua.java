package localsearch.applications.inclass;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class thungchua {
	
	int N = 20; // so chat long
	int M = 5; // so thung chua
	
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
	public thungchua() {
		
	}
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	IFunction[] V_liquid_per_barrel; 
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for(int i = 0; i < N; i++)
			X[i] = new VarIntLS(mgr, 0, N-1);
		
		S = new ConstraintSystem(mgr);
		for(int k = 0; k < ban_list2.length; k++) {
			IConstraint c = new NotEqual(X[ban_list2[k][0]], X[ban_list2[k][1]]);
			S.post(c);
		}
		
		for(int k = 0; k < ban_list3.length; k++) {
			IConstraint c = new AND(new NotEqual(X[ban_list3[k][0]], X[ban_list3[k][1]]), new NotEqual(X[ban_list3[k][0]], X[ban_list3[k][2]]));
			S.post(c);
		}
		
		V_liquid_per_barrel = new IFunction[N];

		
		for(int j = 0; j < N; j++) {
			V_liquid_per_barrel[j] = new ConditionalSum(X, V_Liquid, j);
			
			S.post(new LessOrEqual(V_liquid_per_barrel[j], V_Barrel[j]));
		}
		
		mgr.close();
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
