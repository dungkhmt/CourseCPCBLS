package lol;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class TwoD_Binpacking {
	
	int N=6,H=10,W=7;
	int h[]= {6,5,2,3,3,2};
	int w[]= {3,2,4,4,3,1};
	
	public void solve() {
		Model model=new Model("2D binpacking");
		IntVar x[]=new IntVar[N];
		IntVar y[]=new IntVar[N];
		IntVar o[]=new IntVar[N];
		for(int i=0;i<N;i++) {
			x[i]=model.intVar("x["+i+"]", 0, H);
			y[i]=model.intVar("y["+i+"]", 0, W);
			o[i]=model.intVar("o["+i+"]", 0, 1);
			model.ifThenElse(model.arithm(o[i], "=", 0), 
					model.and(model.arithm(model.intOffsetView(x[i], w[i]),"<=",W),
							model.arithm(model.intOffsetView(y[i], h[i]),"<=",H)), 
					model.and(model.arithm(model.intOffsetView(x[i], h[i]),"<=",W),
							model.arithm(model.intOffsetView(y[i], w[i]),"<=",H)));
		}
		for(int i=0;i<N-1;i++) {
			for(int j=i+1;j<N;j++) {
				model.ifThen(model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 0)), 
						model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
								model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
								model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]),
								model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i])));
				model.ifThen(model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 1)), 
						model.or(model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
								model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]),
								model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]),
								model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i])));
				model.ifThen(model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 0)), 
						model.or(model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]),
								model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
								model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]),
								model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i])));
				model.ifThen(model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 1)), 
						model.or(model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]),
								model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]),
								model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]),
								model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i])));
			}
		}
		int i=1;
	       while (model.getSolver().solve()) {
	          System.out.println("Solution " + i++ + " found : ");
	          for(int j=0;j<N;j++) {
	        	  System.out.print(x[j]+",");
	        	  System.out.print(y[j]+",");
	        	  System.out.print(o[j]+",");
	        	  System.out.println();
	          }System.out.println();
	       }
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new TwoD_Binpacking().solve();
	}

}
