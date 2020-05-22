package planningoptimization115657k62.phamvietbang.project;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.*;
import localsearch.model.*;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CSP {
	public void main(String[] args) {
		LocalSearchManager mgr=new LocalSearchManager();
		VarIntLS[]x=new VarIntLS[5];
		for(int i=0;i<5;i++) {
			x[i]=new VarIntLS(mgr,1,5);
		}
//		IConstraint[] c= new IConstraint[6];
//		c[0]=new NotEqual(new FuncPlus(x[2],3),x[1]);
//		c[1]=new LessOrEqual(x[3],x[4]);
//		c[3]=new IsEqual(new FuncPlus(x[2],x[3]),new FuncPlus(x[0],1));
//		c[4]=new LessOrEqual(x[4],3);
//		c[5]=new IsEqual(new FuncPlus(x[1],x[4]),7);
//		c[6]=new Implicate(new IsEqual(x[2],1),new NotEqual(x[4],2));
		ConstraintSystem S=new ConstraintSystem(mgr);
		S.post(new NotEqual(new FuncPlus(x[2],3), x[1]));
		S.post(new LessOrEqual(x[3], x[4]));
		S.post(new IsEqual(new FuncPlus(x[2],x[3]), new FuncPlus(x[0],1)));
		S.post(new LessOrEqual(x[4], 3));
		S.post(new IsEqual(new FuncPlus(x[4],x[1]), 7));
		S.post(new Implicate(new IsEqual(x[2], 1), new NotEqual(x[4], 2)));
		mgr.close();
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 1000);
		
	}
}
