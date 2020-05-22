package cbls115676khmt61.phamquangdung;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class BaiTap {
	int n = 20; // so dung dich
	int m = 5; 	// so thung
	int limitV[] = {60, 70, 90, 80, 100};
	int v[] = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	 
			
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	IFunction[] maxV;
	IFunction[] sumV;
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[m];
		S = new ConstraintSystem(mgr);
		
		for (int i = 0; i < n; i++) {
			X[i] = new VarIntLS(mgr, 0, m-1);
		}
		
		sumV = new IFunction[m];
		
		
		for (int i = 0; i < m; i++) {
			sumV[i] = new ConditionalSum(X, v, i); 
			S.post(new LessOrEqual(sumV[i], limitV[i])); // rang buoc ve the tich
      
     		// rang buoc cac dung dich khong duoc dong thoi xuat hien	 dung AND
		}
		
		
		
		mgr.close();
	}
	
	
	
}
