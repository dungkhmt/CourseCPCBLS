package planningoptimization115657k62.phamthanhdong;
//bai toan quan hau
import localsearch.model.*;
import localsearch.constraints.alldifferent.*;
import localsearch.functions.basic.*;
import localsearch.selectors.*;

public class Queen {

	public static void main(String[] args) {
		
		int N = 200;
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[N];
		for(int i = 0; i < N; i++)
			X[i] = new VarIntLS(mgr,0,N-1);
		ConstraintSystem S = new ConstraintSystem(mgr);
		
		S.post(new AllDifferent(X));
		
		IFunction[] f1 = new IFunction[N];		
		 for(int i = 0; i < N; i++)			
		  f1[i] = new FuncPlus(X[i], i);		
		 S.post(new AllDifferent(f1));
		 			 
		 IFunction[] f2 = new IFunction[N];		
		 for(int i = 0; i < N; i++)			
		  f2[i] = new FuncPlus(X[i], -i);		
		 S.post(new AllDifferent(f2));			
		 mgr.close();
		 
		 System.out.println("Nhập S = " + S.violations());
		
		 
		 MinMaxSelector mms = new MinMaxSelector(S);
		 int it = 0;	
		 while(it < 100000 && S.violations() > 0){
		  VarIntLS sel_x = mms.selectMostViolatingVariable();	
		  int sel_v = mms.selectMostPromissingValue(sel_x);
		  
		  sel_x.setValuePropagate(sel_v);	
		  
		  System.out.println("Bước (step) " + it + ", vi phạm(violations) = " + S.violations());
		  it++;		
		 }
		 

	}

}
