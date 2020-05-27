package toiuu2;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;

public class Container {
	int N = 6;
	int W = 4;
	int H = 6;
	int[] w = { 1, 3, 2, 3, 1, 2};
	int[] h = { 4, 1, 2, 1, 4, 3};
	IntVar[] x, y, o;
	Model model;
	
	private void buildModel() {
		model = new Model(" Container -- Assignment 1");
		x = model.intVarArray("x", N, 1, W);
		y = model.intVarArray("y", N, 1, H);
		o = model.intVarArray("axis", N, 0, 1);
		
		for (int i = 0; i < N; ++i) {
			model.ifThen(model.arithm(o[i], "=", 0), model.and(model.arithm(model.intOffsetView(x[i], w[i]), "<=", W+1), model.arithm(model.intOffsetView(y[i], h[i]), "<=", H+1)));
			model.ifThen(model.arithm(o[i], "=", 1), model.and(model.arithm(model.intOffsetView(x[i], h[i]), "<=", W+1), model.arithm(model.intOffsetView(y[i], w[i]), "<=", H+1)));
		}
		
		for (int i = 0; i < N-1; ++i)
			for (int j = i+1; j < N; ++j) {
				model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0)), model.or(
						model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
						model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
						model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]),
						model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i])));
				model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1)), model.or(
						model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
						model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]),
						model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]),
						model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i])));
				model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0)), model.or(
						model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]),
						model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
						model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]),
						model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i])));
				model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1)), model.or(
						model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]),
						model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]),
						model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]),
						model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i])));
			}
	}
	for (int i1 = 0; i1 < N - 1; i1++) {
        for (int i2 = i1 + 1; i2 < N; i2++) {
            
            Constraint c1 = model.arithm(y[i1], "<=", y[i2]);

            Constraint c2 = model.scalar(
                    new IntVar[] {x[i1], o[i2], x[i2]},
                    new int[] {1, w[i2]-h[i2], -1},
                    "<=", w[i2]
            );

            Constraint c3 = model.scalar(
                    new IntVar[] {x[i1], o[i1], x[i2]},
                    new int[] {-1, w[i1]-h[i1], 1},
                    "<=", w[i1]
            );


            model.or(c1, c2, c3).post();
        }
	}
	
	
	public void solve() {
		Solver s = model.getSolver();
		
		if(s.solve()) {
			for (int i = 0; i < N; i++) {
				System.out.println (x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
			}
			System.out.println ("-----------------------");
		}
	}
	
	public static void main(String[] args) {
		Container app = new Container();
		app.buildModel();
		app.solve();
	}
	

}