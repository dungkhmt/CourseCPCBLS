package TU;

import java.util.Scanner;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class BinPacking2D {
	
	public int N, W, H;
	public int[] h, w;
	public IntVar[] X, Y, o;
	Model model;
	
	public void readData() {
		Scanner scan = new Scanner(System.in);
		N = scan.nextInt();
		H = scan.nextInt();
		W = scan.nextInt();
		w = new int[N];
		h = new int[N];
		for (int i = 0; i < N; i++) {
			w[i] = scan.nextInt();
			h[i] = scan.nextInt();
		}
		scan.close();
	}

	public void init() {
		model = new Model("BinPacking2D");
		X = new IntVar[N];
		Y = new IntVar[N];
		o = new IntVar[N];
		for (int i = 0; i < N; i++) {
			X[i] = model.intVar("X[" + i + "]", 0, W);
			Y[i] = model.intVar("Y[" + i + "]", 0, H);
			o[i] = model.intVar("o[" + i + "]", 0, 1);
		}
	}

	public void createConstraint() {
		for (int i = 1; i <= N; i++) {
			Constraint c1 = model.arithm(model.intOffsetView(X[i], w[i]), "<=",
					W);
			Constraint c2 = model.arithm(model.intOffsetView(Y[i], h[i]), "<=",
					H);
			model.ifThen(model.arithm(o[i], "=", 0), model.and(c1, c2));

			Constraint c3 = model.arithm(model.intOffsetView(X[i], h[i]), "<=",
					W);
			Constraint c4 = model.arithm(model.intOffsetView(Y[i], w[i]), "<=",
					H);
			model.ifThen(model.arithm(o[i], "=", 0), model.and(c3, c4));
		}

		for (int i = 1; i < N + 1; i++) {
			for (int j = i + 1; j < N; j++) {
				Constraint c1 = model.and(model.arithm(o[i], "=", 0),
						model.arithm(o[j], "=", 0));
				Constraint c2 = model.arithm(model.intOffsetView(X[i], w[i]),
						"<=", X[j]);
				Constraint c3 = model.arithm(model.intOffsetView(X[j], w[j]),
						"<=", X[i]);
				Constraint c4 = model.arithm(model.intOffsetView(Y[i], h[i]),
						"<=", Y[j]);
				Constraint c5 = model.arithm(model.intOffsetView(Y[j], h[j]),
						"<=", Y[i]);
				model.ifThen(c1, model.or(c5, c2, c3, c4));

				c1 = model.and(model.arithm(o[i], "=", 0),
						model.arithm(o[j], "=", 1));
				c2 = model.arithm(model.intOffsetView(X[i], w[i]), "<=", X[j]);
				c3 = model.arithm(model.intOffsetView(X[j], h[j]), "<=", X[i]);
				c4 = model.arithm(model.intOffsetView(Y[i], h[i]), "<=", Y[j]);
				c5 = model.arithm(model.intOffsetView(Y[j], w[j]), "<=", Y[i]);
				model.ifThen(c1, model.or(c5, c2, c3, c4));

				c1 = model.and(model.arithm(o[i], "=", 1),
						model.arithm(o[j], "=", 0));
				c2 = model.arithm(model.intOffsetView(X[i], h[i]), "<=", X[j]);
				c3 = model.arithm(model.intOffsetView(X[j], w[j]), "<=", X[i]);
				c4 = model.arithm(model.intOffsetView(Y[i], w[i]), "<=", Y[j]);
				c5 = model.arithm(model.intOffsetView(Y[j], h[j]), "<=", Y[i]);
				model.ifThen(c1, model.or(c5, c2, c3, c4));

				c1 = model.and(model.arithm(o[i], "=", 1),
						model.arithm(o[j], "=", 1));
				c2 = model.arithm(model.intOffsetView(X[i], h[i]), "<=", X[j]);
				c3 = model.arithm(model.intOffsetView(X[j], h[j]), "<=", X[i]);
				c4 = model.arithm(model.intOffsetView(Y[i], w[i]), "<=", Y[j]);
				c5 = model.arithm(model.intOffsetView(Y[j], w[j]), "<=", Y[i]);
				model.ifThen(c1, model.or(c5, c2, c3, c4));
			}
		}
	}

	public void solve() {
		readData();
		init();
		createConstraint();
		Solver s = model.getSolver();
		System.out.println("---------");
		while (s.solve()) {
			for (int i = 0; i < N; i++) {
				System.out.println("Pack" + i + ":" + X[i].getValue() + ","
						+ Y[i].getValue() + "," + o[i].getValue());
			}
			System.out.println("---------");
		}
	}

	public static void main(String[] args) {
		BinPacking2D bk = new BinPacking2D();
		bk.solve();
	}
}