package cbls115676khmt61.ThaiDD_20163676;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CSP {
	int n; // number of variables
	LocalSearchManager mgr;
	VarIntLS[] x;
	ConstraintSystem S;
	int min_value, max_value; // min variable value, max variable value
	
	public CSP(int n, int min_value, int max_value) {
		this.n = n;
		this.min_value = min_value;
		this.max_value = max_value;
	}
	
	public ConstraintSystem stateInitial() {
		this.mgr = new LocalSearchManager();
		this.S = new ConstraintSystem(mgr);
		this.x = new VarIntLS[this.n];
		for (int i = 0; i < this.n; i++) {
			x[i] = new VarIntLS(mgr, this.min_value, this.max_value);
		}
		
		S.post(new NotEqual(new FuncPlus(x[2], 3), x[1]));
		S.post(new LessOrEqual(x[3], x[4]));
		S.post(new IsEqual(new FuncPlus(x[2], x[3]), new FuncPlus(x[0], 1)));
		S.post(new LessOrEqual(x[4], 3));
		S.post(new IsEqual(new FuncPlus(x[1], x[4]), 7));
		S.post(new Implicate(new IsEqual(x[2], 1), new NotEqual(x[4], 2)));
		
		this.mgr.close();
		return S;
	}
	
	public void printResults() {
		for(int i = 0; i < this.x.length; i++)
			System.out.println("x[" + i + "] = " + x[i].getValue());
		System.out.println();
	}
	
	public static void main(String[] args) {
		CSP app = new CSP(5, 1, 5);
		ConstraintSystem S = app.stateInitial();

		HillClimbingSearch s = new HillClimbingSearch(S);
		s.search(10000);
		app.printResults();
	}
}
