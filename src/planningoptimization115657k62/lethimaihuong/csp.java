package localsearch.search;

import localsearch.search.HillClimbing;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class csp {
    
	public static void main(String[] args) {
		// TODO Auto-generated method stub
      LocalSearchManager mgr = new LocalSearchManager();
      VarIntLS[] X = new VarIntLS[5];
      for(int i=0;i<5;i++) {
    	  X[i] = new VarIntLS(mgr,1,5);
      }
      ConstraintSystem CS = new ConstraintSystem(mgr);
      IConstraint[] c = new IConstraint[6];
      c[0] = new NotEqual(new FuncPlus(X[2], 3),X[1]);
      c[1] = new LessOrEqual(X[3],X[4]);
      c[2] = new IsEqual(new FuncPlus(X[2],X[3]),new FuncPlus(X[0],1));
      c[3] = new LessOrEqual(X[4],3);
      c[4] = new IsEqual(new FuncPlus(X[1],X[4]),7);
      c[5] = new Implicate(new IsEqual(X[2],1),new NotEqual(X[4],2));
      mgr.close();
      
      HillClimbing seacher = new HillClimbing();
      seacher.hillClimbing(CS, 100000);
      
      for(int i= 0;i<X.length;i++) {
    	  System.out.println("X[" +i+ "] = " +X[i].getValue());
      }
      
      
      
	}

}
