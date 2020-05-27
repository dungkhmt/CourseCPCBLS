package planningoptimization115657k62.levanlinh.Exercise;

import java.util.Arrays;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class ContainerChoco {
	int N = 6;
	int W = 4;
	int L = 6;
	int[] w = {1, 3, 2, 3, 1, 2};
	int[] l = {4, 1, 2, 1, 4, 3};
	IntVar[] x, y, o;
	Model model;
	
	private void buildModel() {
		model = new Model("BinPacking");
		x = model.intVarArray("x", N, 1, W);
		y = model.intVarArray("y", N, 1, L);
		o = model.intVarArray("axis", N, new int[] {0, 1});
		
		for (int i = 0; i < N; ++i) {
			model.ifThen(model.arithm(o[i], "=", 0), model.and(model.arithm(model.intOffsetView(x[i], w[i]), "<=", W+1), model.arithm(model.intOffsetView(y[i], l[i]), "<=", L+1)));
			model.ifThen(model.arithm(o[i], "=", 1), model.and(model.arithm(model.intOffsetView(x[i], l[i]), "<=", W+1), model.arithm(model.intOffsetView(y[i], w[i]), "<=", L+1)));
		}
		
		for (int i = 0; i < N-1; ++i)
			for (int j = i+1; j < N; ++j) {
				model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0)), model.or(
						model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
						model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
						model.arithm(model.intOffsetView(y[i], l[i]), "<=", y[j])));						;
				model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1)), model.or(
						model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]),
						model.arithm(model.intOffsetView(x[j], l[j]), "<=", x[i]),
						model.arithm(model.intOffsetView(y[i], l[i]), "<=", y[j])));
						
				model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0)), model.or(
						model.arithm(model.intOffsetView(x[i], l[i]), "<=", x[j]),
						model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]),
						model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j])));
						
				model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1)), model.or(
						model.arithm(model.intOffsetView(x[i], l[i]), "<=", x[j]),
						model.arithm(model.intOffsetView(x[j], l[j]), "<=", x[i]),
						model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j])));						
			}
	}
	
	public void solve() {
		Solver s = model.getSolver();
		//s.setSearch(Search.intVarSearch(new FirstFail(model), new IntDomainMin()));
		
		s.solve();
		int[][] ctn = new int[L][W];
		for (int i = 0; i < L; ++i) Arrays.fill(ctn[i], -1);
		
		for (int i = 0; i < N; i++) {
			int X = x[i].getValue()-1;
			int Y = y[i].getValue()-1;
			
			if (o[i].getValue() == 0) {
				for (int i1 = 0; i1 < w[i]; ++i1) for (int i2 = 0; i2 < l[i]; ++i2) ctn[Y+i2][X+i1] = i;
			} else {
				for (int i1 = 0; i1 < l[i]; ++i1) for (int i2 = 0; i2 < w[i]; ++i2) ctn[Y+i2][X+i1] = i;
			}
			//System.out.println (x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
		}
		
		// Print matrix of container
		for (int i = L -1; i >= 0; --i) {
			for (int j = 0; j < W; ++j) System.out.print(ctn[i][j] + "\t"); 
			System.out.println();
		}
		//System.out.println ("-----------------------");
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ContainerChoco app = new ContainerChoco();
		app.buildModel();
		app.solve();
	}

}
