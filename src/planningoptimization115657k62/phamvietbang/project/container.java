package planningoptimization115657k62.phamvietbang.project;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
public class container {
	public static void main(String[] args) {
		Model model=new Model();
		int N=6;
		int L=6;
		int W=6;
		int w[]= {1,3,2,3,1,2};
		int l[]= {4,1,2,1,4,3};
		IntVar x[]=new IntVar[W];
		IntVar y[]=new IntVar[L];
		IntVar check[]=new IntVar[N];
		for(int i=0;i<N;i++) {
			x[i]=model.intVar("x["+i+"]",0,W);
			y[i]=model.intVar("y["+i+"]",0,L);
			check[i]=model.intVar("check["+i+"]",0,1);
		}
		for(int i=0;i<N;i++) {
			Constraint c1=model.arithm(model.intOffsetView(x[i], w[i]), "<=", W);
			Constraint c2=model.arithm(model.intOffsetView(y[i], l[i]), "<=", L);
			model.ifThen(model.arithm(check[i], "=", 0), model.and(c1,c2));
			Constraint c3=model.arithm(model.intOffsetView(x[i], l[i]), "<=", W);
			Constraint c4=model.arithm(model.intOffsetView(y[i], w[i]),"<=",L);
			model.ifThen(model.arithm(check[i], "=", 1), model.and(c3,c4));
		}
		for(int i=0;i<N-1;i++)
			for(int j=i+1;j<N;j++) {
				Constraint c1=model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				Constraint c2=model.arithm(model.intOffsetView(x[j],w[j]),"<=",x[i]);
				Constraint c3=model.arithm(model.intOffsetView(y[i], l[i]),"<=",y[j]);
				Constraint c4=model.arithm(model.intOffsetView(y[j], l[j]), "<=", y[i]);
				model.ifThen(model.and(model.arithm(check[i], "=", 0),model.arithm(check[i], "=", 0)),model.or(c1,c2,c3,c4));
				Constraint c5=model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				Constraint c6=model.arithm(model.intOffsetView(x[j],l[j]),"<=",x[i]);
				Constraint c7=model.arithm(model.intOffsetView(y[i], l[i]),"<=",y[j]);
				Constraint c8=model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				model.ifThen(model.and(model.arithm(check[i], "=", 0),model.arithm(check[i], "=", 1)),model.or(c5,c6,c7,c8));
				Constraint c9=model.arithm(model.intOffsetView(x[i], l[i]), "<=", x[j]);
				Constraint c10=model.arithm(model.intOffsetView(x[j],w[j]),"<=",x[i]);
				Constraint c11=model.arithm(model.intOffsetView(y[i], w[i]),"<=",y[j]);
				Constraint c12=model.arithm(model.intOffsetView(y[j], l[j]), "<=", y[i]);
				model.ifThen(model.and(model.arithm(check[i], "=", 1),model.arithm(check[i], "=", 0)),model.or(c9,c10,c11,c12));
				Constraint c13=model.arithm(model.intOffsetView(x[i], l[i]), "<=", x[j]);
				Constraint c14=model.arithm(model.intOffsetView(x[j],l[j]),"<=",x[i]);
				Constraint c15=model.arithm(model.intOffsetView(y[i], w[i]),"<=",y[j]);
				Constraint c16=model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				model.ifThen(model.and(model.arithm(check[i], "=", 1),model.arithm(check[i], "=", 1)),model.or(c13,c14,c15,c16));
				
			}
		Solver s=model.getSolver();
		System.out.print("Sap xep: \n");
		while(s.solve()) {
			for(int i=0;i<N;i++) {
				System.out.println(x[i].getValue()+" "+y[i].getValue()+" "+check[i].getValue());
			}
			System.out.println("---------------");
		}
		
	}
}
