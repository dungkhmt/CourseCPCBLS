package cbls115676khmt61.ThaiDD_20163676;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncPlus;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Container {
	
	int n;
	int width, length;
	ConstraintSystem S;
	LocalSearchManager mgr;
	VarIntLS[] X; // x[i] la toa do diem trai duoi theo chieu x cua vat i
	VarIntLS[] Y; // y[i] la toa do diem trai duoi theo chieu y cua vat i
	VarIntLS[] R; // r[i] quyet dinh co xoay vat khong
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		width = 4;
		length = 6;
		X = new VarIntLS[n];
		Y = new VarIntLS[n];
		for (int i = 0; i < n; i++) {
			X[i] = new VarIntLS(mgr, 0, width);
			Y[i] = new VarIntLS(mgr, 0, length);
			R[i] = new VarIntLS(mgr, 0, 1);
		}
		for (int i = 0; i < n - 1; i++) {
			// khong xoay vat thu i 
			// f1: [(y[i] <= y[i+1] and (x[i] >= x[i+1] + w_i+1 or x[i] + w_i <= x[i+1])
			// or f2: (y[i] + l_i <= y[i+1])]
			// and f3: x[i+1] + w_i+1 <= width and f4: y[i+1] + l_i+1 <= length)
			// chu y nhung cai cuoi
			S.post(new Implicate(new IsEqual(R[i], 0), 
					new AND(new OR(new AND(new LessOrEqual(Y[i], Y[i+1]), 
							new OR(
									new LessOrEqual(
											new FuncPlus(x[i+1], w[i+1]), 
											x[i]), 
									new LessOrEqual(
											new FuncPlus(x[i], w[i]), 
											x[i+1])
									)
							), new LessOrEqual(new FuncPlus(y[u], l[i]), y[i+1])
							))));
		
		}
		
		mgr.close();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
