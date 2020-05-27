package planningoptimization115657k62.hoangmanhhung;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;

public class BinPacking {
	int N = 3;
	int W = 4;
	int H = 6;
	int[] w = {3, 3, 1};
	int[] h = {2, 4 ,6};
	IntVar[] x, y, o;
	Model model;
	
	private void buildModel() {
		model = new Model("BinPacking");
		x = model.intVarArray("x", N, 1, W);
		y = model.intVarArray("y", N, 1, H);
		o = model.intVarArray("axis", N, new int[] {0, 1});
		
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
	
	public void solve() {
		Solver s = model.getSolver();
		//s.setSearch(Search.intVarSearch(new FirstFail(model), new IntDomainMin()));
		
		while (s.solve()) {
			for (int i = 0; i < N; i++) {
				System.out.println (x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
			}
			System.out.println ("-----------------------");
		}
	}
	
	public static void main(String[] args) {
		BinPacking binpacking = new BinPacking();
		binpacking.buildModel();
		binpacking.solve();
	}
	

}