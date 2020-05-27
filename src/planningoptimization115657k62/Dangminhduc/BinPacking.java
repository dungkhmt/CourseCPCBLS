package planningoptimization115657k62.Dangminhduc;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class BinPacking {

	public static void binPacking(int W, int H, int[] h, int[] w, int N) {
		Model model = new Model("BinPacking");
		IntVar[] x = new IntVar[N];
		IntVar[] y = new IntVar[N];
		IntVar[] o = new IntVar[N];
		
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
		
		/*for (int i = 0; i < N-1; i++) {
			model.arithm(y[i], "<=", y[i+1]).post();
		}*/
		for (int i1 = 0; i1 < N - 1; i1++) {
            for (int i2 = i1 + 1; i2 < N; i2++) {
                
                Constraint c1 = model.arithm(y[i1], ">=", y[i2]);

                Constraint c2 = model.scalar(
                        new IntVar[] {x[i1], o[i2], x[i2]},
                        new int[] {1, w[i2]-h[i2], -1},
                        ">=", w[i2]
                );

                Constraint c3 = model.scalar(
                        new IntVar[] {x[i1], o[i1], x[i2]},
                        new int[] {-1, w[i1]-h[i1], 1},
                        ">=", w[i1]
                );


                model.or(c1, c2, c3).post();
            }
		}
		
		model.getSolver().solve();
		
		System.out.println("toa do cua cac thung hang x - y - o la: ");
		for(int i = 0; i<N; i++) {
			System.out.println(x[i].getValue() + " - " + y[i].getValue() + " - " + o[i].getValue());
		}
	}
	
	public static void main(String[] args) {
		int W = 4,H = 6;
		int h[] = {4,1,2,1,4,3};
		int w[] = {1,3,2,3,1,2};
		int N = 6;
		BinPacking.binPacking(W, H, h, w, N);
	}
}
