package planningoptimization115657k62.phamthanhdong;

//bai toan xep hang 2D

import org.chocosolver.solver.Model;
//import org.chocosolver.solver.search.strategy.Search;
//import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
//import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.IntVar;
//import org.chocosolver.solver.*;
//import java.util.*;
//import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
//import org.chocosolver.solver.search.strategy.Search;
//import org.chocosolver.solver.variables.IntVar;

 


public class BinPacking 
{
  int N = 2;
  int W = 4;
  int H = 6;
  IntVar[] x;
  IntVar[] y;
  IntVar[] o;
  Model model;
  

 

  int w[] = {1, 2, 3}; // chua khoi luong
  int h[] = {3, 4, 6}; // chua trong luong
  
 

 

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
      
  }
  
  public void search() {
      Solver s = model.getSolver();
      System.out.println("Ket qua :");
     System.out.println("------------");
     
     while(s.solve()) {
         for(int i = 0; i < N; i++) {
             System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
             
         }
         System.out.println("*************");
     }
  }
  
    public static void main(String[] args) {
        BinPacking bin = new BinPacking();
        bin.buildModel();
        bin.search();
        
    }
}
 
//chương trình cũ

/*
public class BinPacking 
{
	int N = 2;
	int W = 4;
	int H = 6;
	Model model;
	IntVar[] x;

	public void buildModel() {
		model = new Model("2D BinPacking");
		x = new IntVar[2];

		for (int i = 0; i < N; i++)
			x[i] = model.intVar("x[" + i + "]",0,2);
		model.or(model.arithm(x[0], "=", 2), model.arithm( x[1], "=", 2)).post();
	}

	public void search() {
		org.chocosolver.solver.Solver s = model.getSolver();
		s.setSearch(Search.intVarSearch(new FirstFail(model), new IntDomainMin()));

		while (s.solve()) {
			for (int i = 0; i < N; i++)
				System.out.println(x[i]);
			System.out.println("***************");
		}

	}

	public static void main(String... args) 
	{
		BinPacking app = new BinPacking();
		app.buildModel();
		app.search();
	}
}
*/