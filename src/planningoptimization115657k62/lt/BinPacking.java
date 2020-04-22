package planning_optimization_lam.lt;

import org.chocosolver.solver.*;
import org.chocosolver.solver.constraints.*;
import org.chocosolver.solver.variables.IntVar;

public class BinPacking {
	public static void main(String[] args) {

		int N = 3;
		int H = 6, W = 4;
		int[] h = { 3, 4, 6 };

		int[] w = { 2, 3, 1 };

		Model model = new Model("BinPacking");

		IntVar x[] = new IntVar[N];
		IntVar y[] = new IntVar[N];
		IntVar o[] = new IntVar[N];

		for (int i = 0; i < N; i++) {
			x[i] = model.intVar("X[" + i + "]", 0, W - 1);
			y[i] = model.intVar("Y[" + i + "]", 0, H - 1);
			o[i] = model.intVar("O[" + i + "]", 0, 1);
		}

		for (int i = 0; i < N; i++) {
			Constraint c1 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", W);
			Constraint c2 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", H);
			model.ifThen(model.arithm(o[i], "=", 0), model.and(c1, c2));

			Constraint c3 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", W);
			Constraint c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", H);
			model.ifThen(model.arithm(o[i], "=", 1), model.and(c3, c4));
		}

		for (int i = 0; i < N - 1; i++) {
			for (int j = i + 1; j < N; j++) {
				Constraint c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0));
				Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				Constraint c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				Constraint c4 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
				Constraint c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c2, c3, c4, c5));

				Constraint c6 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1));
				Constraint c7 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				Constraint c8 = model.arithm(model.intOffsetView(x[j], h[i]), "<=", x[i]);
				Constraint c9 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
				Constraint c10 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				model.ifThen(c6, model.or(c7, c8, c9, c10));

				Constraint c11 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0));
				Constraint c12 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[j]);
				Constraint c13 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				Constraint c14 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[j]);
				Constraint c15 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
				model.ifThen(c11, model.or(c12, c13, c14, c15));

				Constraint c16 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1));
				Constraint c17 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				Constraint c18 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				Constraint c19 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
				Constraint c20 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				model.ifThen(c16, model.or(c17, c18, c19, c20));

			}
		}

		model.getSolver().solve();
		for (int i = 0; i < N; i++) {
			System.out.print("(" + x[i] + " ," + y[i] + ", " + o[i] + ")\n");

		}
	}
}
