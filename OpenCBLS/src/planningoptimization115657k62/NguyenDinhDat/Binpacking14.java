import org.chocosolver.memory.Except_0;
import org.chocosolver.memory.ICondition;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ISatFactory;
import org.chocosolver.solver.constraints.nary.automata.FA.ICostAutomaton;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;

import com.google.ortools.constraintsolver.Constraint;

public class Binpacking14 {
	public static void main(String[] arg) {
	  
	  int N= 2 ;
	  int W = 4;
	  int H = 6;
	  int[] w = {3,3,1};
	  int[] h = {2,4,6};
	  Model model = new Model("2d binpacking");
		  
	 
	  IntVar[] x = new IntVar[N] ;
	  IntVar[] y = new IntVar[N];
	  IntVar[] o = new IntVar[N];
	  
	 
	  for (int i = 0; i < N; ++i) {
		          x[i]= model.intVar("x[" + i + "]", 0, W);
		          y[i] = model.intVar("y[" + i + "]",0,H);
		          o[i] = model.intVar("o[" + i + "]",0,1);
		    
        }
	  model.or(model.arithm(x[0],"=", 2),model.arithm(x[1],"=",2 )).post();
	  for(int i=0;i < N;i++) {
		  org.chocosolver.solver.constraints.Constraint c1 = model.arithm(model.intOffsetView(x[i],w[i]),"<=" ,W);
		  org.chocosolver.solver.constraints.Constraint c2 = model.arithm(model.intOffsetView(x[i], h[i]),"<=", H);
		  model.ifThen(model.arithm(o[i], "=", 0), model.and(c1,c2));
		  
		  org.chocosolver.solver.constraints.Constraint c3 = model.arithm(model.intOffsetView(x[i],h[i]), "<=", W);
		  org.chocosolver.solver.constraints.Constraint c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", H);
		  model.ifThen(model.arithm(o[i], "=", 1), model.and(c3,c4));
		  
		  
		  
	  }
	  for(int i=0;i < N;i++) {
		  for(int j= i+1;j < N;j++) {
			  org.chocosolver.solver.constraints.Constraint c1 = model.and(model.arithm(o[i], "=", 0),model.arithm(o[j],"=", 0));
			  org.chocosolver.solver.constraints.Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			  org.chocosolver.solver.constraints.Constraint c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
			  org.chocosolver.solver.constraints.Constraint c4 = model.arithm(model.intOffsetView(y[i],h[i]), "<=", y[j]);
			  org.chocosolver.solver.constraints.Constraint c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
			  model.ifThen(c1, model.or(c2,c3,c4,c5));
			  
			  c1 = model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 1));
			  c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			  c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			  c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
			  c4 = model.arithm(model.intOffsetView(y[i],h[i]), "<=", y[j]);
			  c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
			  model.ifThen(c1, model.or(c2,c3,c4,c5));
			  

			  c1 = model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 0));
			  c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			  c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			  c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
			  c4 = model.arithm(model.intOffsetView(y[i],h[i]), "<=", y[j]);
			  c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
			  model.ifThen(c1, model.or(c2,c3,c4,c5));
			  

			  c1 = model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 1));
			  c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			  c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			  c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
			  c4 = model.arithm(model.intOffsetView(y[i],h[i]), "<=", y[j]);
			  c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
			  model.ifThen(c1, model.or(c2,c3,c4,c5));
			  
			  
			  
			  
			  
			  
		  }
		  
	  }
	       Solver s = model.getSolver();
		   System.out.println("------");
		   
		   while(s.solve()) {
			   for(int i=0;i < N;i++) {
				   System.out.println("Goc trai cua goi hang " + i + " o vi tri: (" + y[i].getValue() + ", " + x[i].getValue() + ")   " + o[i].getValue());
			   }
			   System.out.println("-----------");
		   }
		 
		 
		    
	}		    
  
}
