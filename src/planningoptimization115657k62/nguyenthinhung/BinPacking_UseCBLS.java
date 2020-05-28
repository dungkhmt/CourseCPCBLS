package planningoptimization115657k62.nguyenthinhung;

import org.mockito.internal.matchers.LessOrEqual;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class BinPacking_UseCBLS {
	int N = 3;
	int H = 6;
	int W = 4;
	int[] w = {3,3,1};
	int[] h = {2,4,6};
	
	LocalSearchManager mgr;
	VarIntLS[] x;
	VarIntLS[] y;
	VarIntLS[] o;
	ConstraintSystem C;
	
	public void buildModel(){ 
		mgr = new LocalSearchManager();
		x = new VarIntLS[100];
		y = new VarIntLS[100];
		o = new VarIntLS[100];
		for(int i = 0;i < N;i++) {
			o[i] =  new VarIntLS(mgr,0,1);
			y[i] =  new VarIntLS(mgr,1,H-1);
			x[i] =  new VarIntLS(mgr,0,W-1);
		}
		
		
		for(int i=0;i<N-1;i++) {
			for(int j=i+1;j<N;j++) {
				IConstraint[] c1 = new IConstraint[2];
				c1[0] = new IsEqual(o[i], 0);
				c1[1] = new IsEqual(o[j], 0);
				IConstraint c2 = new AND(c1);
				IConstraint[] c3 = new IConstraint[4];
				c3[0] = new LessOrEqual(new FuncPlus(x[i],w[i]), x[j]);
				c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]) , x[i]);
				
				
			}
		}
	}
}
