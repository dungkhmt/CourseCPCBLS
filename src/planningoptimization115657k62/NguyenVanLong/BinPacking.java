package chocoSolver;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class BinPacking {

	public static void binPacking(int W, int H, int[] h, int[] w, int N) {
		Model model = new Model("BinPacking");
		IntVar[] x = new IntVar[3];
		IntVar[] y = new IntVar[3];
		IntVar[] o = new IntVar[3];
		
		for(int i = 0; i<N; i++) {
			x[i] = model.intVar("x[" + i + "]", 0, W);
			y[i] = model.intVar("y[" + i + "]", 0, H);
			o[i] = model.intVar("o[" + i + "]", 0, 1);
		}
		//model.arithm() tra ve Constraint
		//model.intOffSetView tra ve IntVar
		
		for(int i = 0; i<N; i++) {
		Constraint c1 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", W);
		Constraint c2 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", H);
		model.ifThen(model.arithm(o[i], "=", 0), model.and(c1,c2));
		
		Constraint c3 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", W);
		Constraint c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", H);
		model.ifThen(model.arithm(o[i], "=", 1), model.and(c3,c4));
		}
		
		for(int i = 0; i<N-1; i++)
			for(int j = i+1; j<N; j++) {
				
				Constraint c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0));
				Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				Constraint c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				Constraint c4 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
				Constraint c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				
				c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1));
				c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				c3 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				c4 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
				c5 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				
				c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0));
				c2 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
				c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
				
				c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1));
				c2 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				c3 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
				c5 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c2,c3,c4,c5));
			}
		
		model.getSolver().solve();
		
		System.out.println("toa do cua cac thung hang x - y - o la: ");
		for(int i = 0; i<N; i++) {
			System.out.println(x[i].getValue() + " - " + y[i].getValue() + " - " + o[i].getValue());
		}
	}
	
	public static void main(String[] args) {
		int W = 4,H = 6;
		int h[] = {2,4,6};
		int w[] = {3,3,1};
		int N = 3;
		BinPacking.binPacking(W, H, h, w, N);
	}
}
