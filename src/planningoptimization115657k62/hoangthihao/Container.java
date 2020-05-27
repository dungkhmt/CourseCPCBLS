package planningoptimization115657k62.hoangthihao;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class Container {
  int N = 6;
  int W = 4;
  int H = 6;
  IntVar[] x;
  IntVar[] y;
  IntVar[] o;
  Model model;
  int w[] = {1,3,2,3,1,2}; // chua khoi luong
  int h[] = {4,1,2,1,4,3}; // chua trong luong
  int[][] prequisites = { {0,1,1,1,1,1},
						  {0,0,1,1,1,1},
						  {0,0,0,1,1,1},
						  {0,0,0,0,1,1},
						  {0,0,0,0,0,1},
						  {0,0,0,0,0,0}};

  public void buildModel() {
	  model = new Model("2D Binpacking");
	  x = new IntVar[N];
	  y = new IntVar[N];
	  o = new IntVar[N];
	  for(int i = 0; i < N; i++) {
		  x[i] = model.intVar("x[" + i + "]", 0, W);
		  y[i] = model.intVar("y[" + i + "]", 0, H);
		  o[i] = model.intVar("o[" + i + "]", 0, 1);;
		  	  
	  }
	  for(int i = 0; i < N; i++) {
		  Constraint c1 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", W); 
		  Constraint c2 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", H);
		  model.ifThen(model.arithm(o[i], "=", 0), model.and(c1, c2));
		  
		  Constraint c3 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", W);
		  Constraint c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", H);
		  model.ifThen(model.arithm(o[i], "=", 1), model.and(c3, c4));
 
	  }
	  
	  for(int i = 0; i < N; i++) {  
		  for(int j = i+1; j < N; j++) {
			  Constraint c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0));
			  Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			  Constraint c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
			  Constraint c4 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
			  Constraint c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
			  model.ifThen(c1, model.or(c2, c3, c4, c5));
			  
			   c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1));
			   c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
			   c3 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[j]);
			   c4 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
			   c5 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
			  model.ifThen(c1, model.or(c2, c3, c4, c5));
			  
			   c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0));
			   c2 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
			   c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
			   c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
			   c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
			  model.ifThen(c1, model.or(c2, c3, c4, c5));
			  
			   c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1));
			   c2 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
			   c3 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
			   c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
			   c5 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
			  model.ifThen(c1, model.or(c2, c3, c4, c5));
			  	  
		  }
	  }
	  
	  for (int i = 0; i < N; i++) {
		  for (int j = 0; j < N; j++) {
			  if (prequisites[i][j] == 1) {
				  Constraint c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0));
				  Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				  Constraint c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				  Constraint c4 = model.arithm(model.intOffsetView(y[i], h[i]), ">", y[j]);
				  Constraint c5 = model.and(c3, c4);
				  Constraint c6 = model.and(c2, c4);
				  model.ifThen(c1, model.or(c5, c6));
				  
				  c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0));
				  c2 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				  c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				  c4 = model.arithm(model.intOffsetView(y[i], w[i]), ">", y[j]);
				  c5 = model.and(c3, c4);
				  c6 = model.and(c2, c4);
				  model.ifThen(c1, model.or(c5, c6));
				  
				  c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1));
				  c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				  c3 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				  c4 = model.arithm(model.intOffsetView(y[i], h[i]), ">", y[j]);
				  c5 = model.and(c3, c4);
				  c6 = model.and(c2, c4);
				  model.ifThen(c1, model.or(c5, c6));
				  
				  c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1));
				  c2 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				  c3 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				  c4 = model.arithm(model.intOffsetView(y[i], w[i]), ">", y[j]);
				  c5 = model.and(c3, c4);
				  c6 = model.and(c2, c4);
				  model.ifThen(c1, model.or(c5, c6));
				  
			  }
		  }
	  }
	  
  }
  
  public void search() {
	  Solver s = model.getSolver();
	 System.out.println("-----------");
	 
	 while(s.solve()) {
		 for(int i = 0; i < N; i++) {
			 System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
			 
		 }
		 System.out.println("--------");
	 }

  }
  
  public static void main(String[] args) {
		Container bin = new Container();
		bin.buildModel();
		bin.search();
		
	}
}
