package planningoptimization115657k62.nguyenthinhung.assignment;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainBest;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.IntVar;


public class Container_with_Choco {
	int N = 6;
	int L = 6;
	int W = 4;
	int[] w = {1,3,2,3,1,2};
	int[] h = {4,1,2,1,4,3};
	Model model = new Model("Container") ;
	IntVar[] x = new IntVar[100];
	IntVar[] y = new IntVar[100];
	IntVar[] o = new IntVar[100];
	
	public void buid() {
		for(int i = 0;i < N;i++) {
			o[i] = model.intVar("o[" + i + "]", 0,1);
			x[i] = model.intVar("x[" + i + "]", 0,W-1);
			y[i] = model.intVar("y[" + i + "]", 0,L-1);
		}
		
		for(int i=0;i<N-1;i++) {
			for(int j=i+1;j<N;j++) {
				Constraint c1= model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 0));
				Constraint c2= model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
				Constraint c3= model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
				Constraint c4= model.arithm(model.intOffsetView(y[i], h[i]),"<=",y[j]);
				Constraint c5= model.arithm(model.intOffsetView(y[j], h[j]),"<=",y[i]);
				Constraint c6 = model.or(model.arithm(x[i], ">=", x[j]),
						model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", y[i]), 
								model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[i])));
				Constraint c7 = model.or(model.arithm(x[i], ">=", x[j]),
						model.or(model.arithm(model.intOffsetView(x[i], h[i]), "<=", y[i]), 
								model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[i])));
				Constraint c8 = model.or(model.arithm(x[i], ">=", x[j]),
						model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", y[i]), 
								model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[i])));
				Constraint c9 = model.or(model.arithm(x[i], ">=", x[j]),
						model.or(model.arithm(model.intOffsetView(x[i], h[i]), "<=", y[i]), 
								model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[i])));
				
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				model.ifThen(c1,c6);
				
				 c1= model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 1));
				 c2= model.arithm(model.intOffsetView(x[i], w[i]),"<=",x[j]);
				 c3= model.arithm(model.intOffsetView(x[j], h[j]),"<=",x[i]);
				 c4= model.arithm(model.intOffsetView(y[i], h[i]),"<=",y[j]);
				 c5= model.arithm(model.intOffsetView(y[j], w[j]),"<=",y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				model.ifThen(c1, c8);
				
				 c1= model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 0));
				 c2= model.arithm(model.intOffsetView(x[i], h[i]),"<=",x[j]);
				 c3= model.arithm(model.intOffsetView(x[j], w[j]),"<=",x[i]);
				 c4= model.arithm(model.intOffsetView(y[i], w[i]),"<=",y[j]);
				 c5= model.arithm(model.intOffsetView(y[j], h[j]),"<=",y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				model.ifThen(c1,c9);
				
				 c1= model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 1));
				 c2= model.arithm(model.intOffsetView(x[i], h[i]),"<=",x[j]);
				 c3= model.arithm(model.intOffsetView(x[j], h[j]),"<=",x[i]);
				c4= model.arithm(model.intOffsetView(y[i], w[i]),"<=",y[j]);
				 c5= model.arithm(model.intOffsetView(y[j], w[j]),"<=",y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				model.ifThen(c1,c7);
			}}
		}
		/*
		for(int i = 0;i < N-1;i++) {
			for(int j = i+1;j < N;j++) {
				model.or(model.arithm(x[i], ">=", x[j]),
						model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", y[i]), 
								model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[i])));
			}
		}
		*/
	public void Search() {
		boolean s = model.getSolver().solve();
		if(s) {
			for(int i = 0;i < N;i++) {
				System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
			}
			System.out.println("-------------------");
		}
		/*
		while (s.solve()) {
			// System.out.println("test");
			for(int i = 0;i < N;i++) {
				System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
			}
			System.out.println("-------------------");
		}
		*/
	}

	public static void main(String[] args) {
		Container_with_Choco t = new Container_with_Choco();
		t.buid();
		t.Search();
	}
}
