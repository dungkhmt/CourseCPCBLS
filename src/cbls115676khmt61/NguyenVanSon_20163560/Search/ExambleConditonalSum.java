package cbls115676khmt61.NguyenVanSon_20163560.Search;

import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;

import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ExambleConditonalSum {
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		int m =7;
		int n = 3;
		int[] w = { 3,4,2,5,7,1,2};
		
		VarIntLS[] x = new VarIntLS[m];
		for(int i = 0; i<m; i++) {
			x[i] = new VarIntLS(mgr,0,n-1);
		}
		IFunction f = new ConditionalSum(x, w, 1);
		mgr.close();
		
		x[0].setValuePropagate(0);
		x[1].setValuePropagate(1);
		x[2].setValuePropagate(2);
		x[3].setValuePropagate(0);
		x[4].setValuePropagate(1);
		x[5].setValuePropagate(2);
		x[6].setValuePropagate(0);
		System.out.println("f = " + f.getValue());
	}

}
