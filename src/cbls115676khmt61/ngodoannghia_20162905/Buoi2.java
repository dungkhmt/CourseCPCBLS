package cbls115676khmt61.ngodoannghia_20162905;

import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

//Queen CBLS
public class Buoi2 {
	int N; // so con hau
	
	public Buoi2(int N){
		this.N=N;
	}
	// modelling (mo hinh hoa)
	LocalSearchManager mgr;
	VarIntLS[] x;// bien quyet dinh
	ConstraintSystem S;
	
	public void stateModel(){
		mgr = new LocalSearchManager();
		x = new VarIntLS[N];
		for(int i= 0; i < N; i++){
			x[i] = new VarIntLS(mgr, 1, N);
		}
		S = new ConstraintSystem(mgr);
		IConstraint c = new AllDifferent(x);
		S.post(c);
		// S.post(new AllDiferent(x))
		
		IFunction[] f1 = new IFunction[N];
		for(int i = 0; i < N; i++){
			f1[i] = new FuncPlus(x[i],i);
		}
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[N];
		for(int i = 0; i < N; i++){
			f2[i] = new FuncPlus(x[i],-i);
		}
		S.post(new AllDifferent(f2));
		
		mgr.close();
	}
	public void printSol(){
		for(int i = 0; i < N; i++){
			System.out.print(x[i].getValue()+ " ");	
		}
		System.out.println();
	}
	public void localSearch(){
		printSol();
		System.out.println("init, S = "+ S.violations());
		int it = 1;
		MinMaxSelector mms = new MinMaxSelector(S);
		while(it < 10000 && S.violations() > 0){	
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_value = mms.selectMostPromissingValue(sel_x);
			sel_x.setValuePropagate(sel_value);
			
			System.out.println("Step "+it + " S = "+ S.violations());
			it++;
		}
		System.out.print("Best solution: ");
		printSol();
	}
	// gia thich ve assigDetal
	public void test1(){
		mgr = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[5];
		Random R = new Random();
		for(int i = 0; i < 5; i++){
			x[i] = new VarIntLS(mgr, 1, 10);
			//x[i].setValue(R.nextInt(10)+1);
			x[i].setValue(i+1);
		}
		
		IFunction T = new Sum(x);
		mgr.close();
		printSol();
		System.out.println("T = "+ T.getValue());
		int delta = T.getAssignDelta(x[2], 4);
		printSol();
		System.out.println("delta = "+delta+" T= "+T.getValue());
		
		x[2].setValuePropagate(4);
		System.out.println("delta = "+delta+" T = "+ T.getValue());
	}
}