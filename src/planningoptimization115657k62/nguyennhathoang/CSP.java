package planningoptimization115657k62;


import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CSP {

	public static void main(String[] args) {
		LocalSearchManager mgr= new LocalSearchManager();
		VarIntLS[] X= new VarIntLS[5];
		for(int i =0;i<5;i++) {
			X[i]= new VarIntLS(mgr, 1,5);
		}
		
		IConstraint[] c= new IConstraint[6];
		c[0]=new NotEqual(new FuncPlus(X[2], 3), X[1]);
		c[1]= new LessOrEqual(X[3],X[4]);
		c[2]= new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0], 1));
		c[3]= new LessOrEqual(X[4], 3);
		c[4]= new IsEqual(new FuncPlus(X[1], X[4]), 7);
		c[5]= new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2));
		
		IConstraint CS= new AND(c);
		
		mgr.close();
		
		HillClimbingSearch searcher= new HillClimbingSearch();
		searcher.search(CS,100000);
		for(int i=0;i<X.length;i++) {
			System.out.println("X["+ i +"]="+X[i].getValue());
		}
		
		
		

	}

}
