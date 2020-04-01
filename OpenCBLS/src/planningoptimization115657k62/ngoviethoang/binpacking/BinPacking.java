package binpacking;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class BinPacking {
	int H = 6;
	int W = 4;
	int n = 3;
	int[] h = new int[] {2, 4, 6};
	int[] w = new int[] {3, 3, 1};
	IntVar[][] x = new IntVar[n][2];
	IntVar[] r = new IntVar[n];
	Model model;
	public void binpacking() {
		model = new Model("BinPacking");
		r = model.intVarArray(n,0,1);
		for (int i = 0; i < n; i++) {
			x[i][0] = model.intVar(0,H-1);
			x[i][1] = model.intVar(0,W-1);
		}
				
		for (int i = 0; i < n; i++) {
			model.ifThenElse(
					model.arithm(r[i], "=", 0), 
					model.and(
						model.arithm(model.intOffsetView(x[i][0], h[i]), "<=", H),
						model.arithm(model.intOffsetView(x[i][1], w[i]), "<=", W)
					),
					model.and(
						model.arithm(model.intOffsetView(x[i][0], w[i]), "<=", H),
						model.arithm(model.intOffsetView(x[i][1], h[i]), "<=", W)
					)
			);
		}
		for (int i = 0; i < n; i++) {
			for (int j = i+1; j < n; j++) {
				model.ifThen(
						model.and(
								model.arithm(r[i],"=",0),
								model.arithm(r[j],"=",0)
						),
						model.or(
								model.or (
									model.arithm(model.intOffsetView(x[i][0],h[i]), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],h[j]), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],w[i]), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],w[j]), "<=", x[i][1])
								)								
						)						
				);
				
				model.ifThen(
						model.and(
								model.arithm(r[i],"=",0),
								model.arithm(r[j],"=",1)
						),
						model.or(
								model.or (
									model.arithm(model.intOffsetView(x[i][0],h[i]), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],w[j]), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],w[i]), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],h[j]), "<=", x[i][1])
								)
								
						)
						
				);
				
				model.ifThen(
						model.and(
								model.arithm(r[i],"=",1),
								model.arithm(r[j],"=",0)
						),
						model.or(
								model.or (
									model.arithm(model.intOffsetView(x[i][0],w[i]), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],h[j]), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],h[i]), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],w[j]), "<=", x[i][1])
								)
								
						)
						
				);
				
				model.ifThen(
						model.and(
								model.arithm(r[i],"=",1),
								model.arithm(r[j],"=",1)
						),
						model.or(
								model.or (
									model.arithm(model.intOffsetView(x[i][0],w[i]), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],w[j]), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],h[i]), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],h[j]), "<=", x[i][1])
								)
								
						)
						
				);
			}
		}
		
		Solver s = model.getSolver();
		
		boolean res = s.solve();
		if (!res) {
			System.out.println("No solution");
		} else {
			int[][] map = new int[H][W];
			for (int i = 0; i < n; i++) {
				int rot = r[i].getValue();
				int a = x[i][0].getValue();
				int b = x[i][1].getValue();
				if (rot == 1) {
					for (int j = 0; j < w[i]; j++) {
						for (int k = 0; k < h[i]; k++) {
							map[a+j][b+k] = i+1;
						}
					}
				} else {
					for (int j = 0; j < h[i]; j++) {
						for (int k = 0; k < w[i]; k++) {
							map[a+j][b+k] = i+1;
						}
					}
				}
			}
			for (int i = 0; i < H; i++) {
				for (int j = 0; j < W; j++) {
					System.out.print(map[i][j] + " ");
				}
				System.out.println();
			}
		}
	}
	
	public static void main(String[] args) {
		BinPacking app = new BinPacking();
		app.binpacking();
	}
}
