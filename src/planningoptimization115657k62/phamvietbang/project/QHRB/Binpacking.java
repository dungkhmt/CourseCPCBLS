package planningoptimization115657k62.phamvietbang.project.QHRB;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class Binpacking {
	public static void main(String [] args) {
		int N=3;
		int H=6, W=4;
		int w[]= {3,3,1};
		int h[]= {2,4,6};
		Model model=new Model("bin packing");
		IntVar check[]= new IntVar[N];
		IntVar x[]=new IntVar[W];
		IntVar y[]=new IntVar[H];
		for(int i=0;i<N;i++) {
			x[i]=model.intVar("x["+i+"]",0,W);
		
			y[i]=model.intVar("y["+i+"]",0,H);
		
			check[i]=model.intVar("check["+i+"]",0,1);
		}
		for(int i=0;i<N;i++) {
			Constraint c1= model.arithm(model.intOffsetView(x[i],w[i]),"<=",W);
		    Constraint c2= model.arithm(model.intOffsetView(y[i],h[i]), "<=", H);
		    model.ifThen(model.arithm(check[i], "=", 0), model.and(c1,c2));
		    Constraint c3= model.arithm(model.intOffsetView(x[i],h[i]),"<=",W);
		    Constraint c4= model.arithm(model.intOffsetView(y[i],w[i]), "<=", H);
		    model.ifThen(model.arithm(check[i], "=", 1), model.and(c3,c4));
		}	
		
		for(int i=0;i<N-1;i++) {
			for(int j=i+1;j<N;j++) {
				Constraint c5= model.arithm(model.intOffsetView(x[i],w[i]),"<=",x[j]);
				Constraint c6= model.arithm(model.intOffsetView(x[j],w[j]),"<=",x[i]);
				Constraint c7= model.arithm(model.intOffsetView(y[i],h[i]),"<=",y[j]);
				Constraint c8= model.arithm(model.intOffsetView(y[j],h[j]),"<=",y[i]);
				Constraint c9= model.and(model.arithm(check[i], "=", 0),model.arithm(check[j], "=", 0));
				model.ifThen(c9, model.or(c5,c6,c7,c8));	
				Constraint c10= model.arithm(model.intOffsetView(x[i],w[i]),"<=",x[j]);
				Constraint c11= model.arithm(model.intOffsetView(x[j],h[j]),"<=",x[i]);
				Constraint c12= model.arithm(model.intOffsetView(y[i],h[i]),"<=",y[j]);
				Constraint c13= model.arithm(model.intOffsetView(y[j],w[j]),"<=",y[i]);
				Constraint c14= model.and(model.arithm(check[i], "=", 0),model.arithm(check[j], "=", 1));
				model.ifThen(c14, model.or(c10,c11,c12,c13));
				Constraint c15= model.arithm(model.intOffsetView(x[i],h[i]),"<=",x[j]);
				Constraint c16= model.arithm(model.intOffsetView(x[j],w[j]),"<=",x[i]);
				Constraint c17= model.arithm(model.intOffsetView(y[i],w[i]),"<=",y[j]);
				Constraint c18= model.arithm(model.intOffsetView(y[j],h[j]),"<=",y[i]);
				Constraint c19= model.and(model.arithm(check[i], "=", 1),model.arithm(check[j], "=", 0));
				model.ifThen(c19, model.or(c15,c16,c17,c18));
				Constraint c20= model.arithm(model.intOffsetView(x[i],h[i]),"<=",x[j]);
				Constraint c21= model.arithm(model.intOffsetView(x[j],h[j]),"<=",x[i]);
				Constraint c22= model.arithm(model.intOffsetView(y[i],w[i]),"<=",y[j]);
				Constraint c23= model.arithm(model.intOffsetView(y[j],w[j]),"<=",y[i]);
				Constraint c24= model.and(model.arithm(check[i], "=", 1),model.arithm(check[j], "=", 1));
				model.ifThen(c24, model.or(c20,c21,c22,c23));
			}
		}
		Solver s=model.getSolver();
		System.out.println("-----------------");
		while(s.solve()) {
			for(int i=0;i<N;i++) {
				System.out.println(x[i].getValue()+" "+y[i].getValue()+" "+ check[i].getValue());
				
			}
			System.out.println("-----------------");
		}

	}
	
}
