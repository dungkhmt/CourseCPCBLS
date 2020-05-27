package cbls115676khmt61.NguyenVanSon_20163560.Search;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class QueenCBLS {
	int N; // so quan hau
	
	// modelling
	LocalSearchManager mgr;
	VarIntLS[] X; // cac bien quyet dinh
	
	ConstraintSystem S;
	
	private QueenCBLS(int N) {
		this.N = N;
	}
	private void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for(int i = 0; i< N; i++) {
			X[i]  =new VarIntLS(mgr, 1,N);
		}
		S = new ConstraintSystem(mgr);
		IConstraint c = new AllDifferent(X);
		S.post(c);
		
		IFunction[] f1 = new IFunction[N];
		for (int i = 0; i< N; i++)
			f1[i] = new FuncPlus(X[i], i);
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[N];
		for(int i= 0; i< N; i++)
			f2[i] = new FuncPlus(X[i], -i);
		S.post(new AllDifferent(f2));
		mgr.close();
	}
	
	private void printSol() {
		for(int i = 0; i<N; i++) {
			System.out.print(X[i].getValue() + " ");
			
		}
		System.out.println();
	}
	
	private void localSearch() {
		printSol();
		System.out.println("init, S = "+ S.violations());
		int  it= 1;
		while(it < 10000 && S.violations() > 0) {
			MinMaxSelector mms = new MinMaxSelector(S);
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_value = mms.selectMostPromissingValue(sel_x);
			
			sel_x.setValuePropagate(sel_value);
			
			System.out.println("Step " + it + " , S = "  + S.violations());
			it++;
	}
		System.out.println("Best Solution: ");
		printSol();
	}
	public static void main(String[] args) {
		
		QueenCBLS app = new QueenCBLS(100);
		app.stateModel();
		app.localSearch();
	}
		
		
		
		
		
	}
	

