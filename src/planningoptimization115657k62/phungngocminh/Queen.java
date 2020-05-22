package planningoptimization115657k62.phungngocminh;

import java.util.ArrayList;

import localsearch.*;
import localsearch.model.*;
import localsearch.constraints.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.*;
import localsearch.functions.basic.*;
import localsearch.selectors.*;
import java.util.*;
public class Queen {

	public void LSQueen(){
		int n=100;
		
		LocalSearchManager ls=new LocalSearchManager();
		ConstraintSystem S=new ConstraintSystem(ls);
		
		
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		VarIntLS[] x = new VarIntLS[n];
		for (int i = 0; i < n; i++){
			x[i] = new VarIntLS(ls, 0, n - 1);
		
			
			map.put(x[i], i);
		}
		
		S.post(new AllDifferent(x));
		
			
		
		IFunction[] f1=new IFunction[n];
		for (int i = 0; i < n; i++) 
			f1[i] =  new FuncPlus(x[i], i);

		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++) f2[i] = new FuncPlus(x[i], -i);
		S.post(new AllDifferent(f2));
		
		
		
		ls.close();
		System.out.println("Init S = " + S.violations());
		MinMaxSelector mms = new MinMaxSelector(S);
		
		int it = 0;
		while(it < 10000 && S.violations() > 0){
			
			VarIntLS sel_x = mms.selectMostViolatingVariable();
			int sel_v = mms.selectMostPromissingValue(sel_x);
			
			sel_x.setValuePropagate(sel_v);
			System.out.println("Step " + it + ", x[" + map.get(sel_x) + "] := " + sel_v + ", S = " + S.violations());
			
			it++;
		}
		
		
		System.out.println(S.violations());

	}

	public static void main(String[] args) {
		
		Queen Q = new Queen();
		Q.LSQueen();
	}

}
