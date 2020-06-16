package planningoptimization115657k62.ngoviethoang.modeling.binpacking;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class BinPackingChoco {
	long startTime;
	long endTime;
	int N = 6;
	int H = 6;
	int W = 4;
	int[] w = new int[] {1, 3, 2, 3, 1, 2};
	int[] h = new int[] {4, 1, 2, 1, 4, 3};
	Model model;
	IntVar[][] X;
	IntVar[] R;
	public void solve() {
		startTime = System.currentTimeMillis();
		model = new Model("BinPacking");
		X = new IntVar[N][2];
		R = model.intVarArray(N,0,1);
		for (int i = 0; i < N; i++) {
			X[i][0] = model.intVar(0,H-1);
			X[i][1] = model.intVar(0,W-1);
		}
				
		for (int i = 0; i < N; i++) {
			model.ifThenElse(
					model.arithm(R[i], "=", 0), 
					model.and(
						model.arithm(model.intOffsetView(X[i][0], h[i]), "<=", H),
						model.arithm(model.intOffsetView(X[i][1], w[i]), "<=", W)
					),
					model.and(
						model.arithm(model.intOffsetView(X[i][0], w[i]), "<=", H),
						model.arithm(model.intOffsetView(X[i][1], h[i]), "<=", W)
					)
			);
		}
		for (int i = 0; i < N; i++) {
			for (int j = i+1; j < N; j++) {
				model.ifThen(
						model.and(
								model.arithm(R[i],"=",0),
								model.arithm(R[j],"=",0)
						),
						model.or(
								model.or (
									model.arithm(model.intOffsetView(X[i][0],h[i]), "<=", X[j][0]),
									model.arithm(model.intOffsetView(X[j][0],h[j]), "<=", X[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(X[i][1],w[i]), "<=", X[j][1]),
									model.arithm(model.intOffsetView(X[j][1],w[j]), "<=", X[i][1])
								)								
						)						
				);
				
				model.ifThen(
						model.and(
								model.arithm(R[i],"=",0),
								model.arithm(R[j],"=",1)
						),
						model.or(
								model.or (
									model.arithm(model.intOffsetView(X[i][0],h[i]), "<=", X[j][0]),
									model.arithm(model.intOffsetView(X[j][0],w[j]), "<=", X[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(X[i][1],w[i]), "<=", X[j][1]),
									model.arithm(model.intOffsetView(X[j][1],h[j]), "<=", X[i][1])
								)
								
						)
						
				);
				
				model.ifThen(
						model.and(
								model.arithm(R[i],"=",1),
								model.arithm(R[j],"=",0)
						),
						model.or(
								model.or (
									model.arithm(model.intOffsetView(X[i][0],w[i]), "<=", X[j][0]),
									model.arithm(model.intOffsetView(X[j][0],h[j]), "<=", X[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(X[i][1],h[i]), "<=", X[j][1]),
									model.arithm(model.intOffsetView(X[j][1],w[j]), "<=", X[i][1])
								)
								
						)
						
				);
				
				model.ifThen(
						model.and(
								model.arithm(R[i],"=",1),
								model.arithm(R[j],"=",1)
						),
						model.or(
								model.or (
									model.arithm(model.intOffsetView(X[i][0],w[i]), "<=", X[j][0]),
									model.arithm(model.intOffsetView(X[j][0],w[j]), "<=", X[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(X[i][1],h[i]), "<=", X[j][1]),
									model.arithm(model.intOffsetView(X[j][1],h[j]), "<=", X[i][1])
								)
								
						)
						
				);
			}
		}
		for (int i = 0; i < N; i++) {
			for (int j = i+1; j < N; j++) {
				model.ifThen(
						model.and(
								model.arithm(R[i], "=", 0),
								model.arithm(R[j], "=", 0)
						),
						model.or(
								model.or(
										model.arithm(X[i][0], "<=", X[j][0]),
										model.arithm(model.intOffsetView(X[i][1],  w[i]), "<=", X[j][1])
								),
								model.arithm(model.intOffsetView(X[j][1],  w[j]), "<=", X[i][1])
						)
				);
				model.ifThen(
						model.and(
								model.arithm(R[i], "=", 0),
								model.arithm(R[j], "=", 1)
						),
						model.or(
								model.or(
										model.arithm(X[i][0], "<=", X[j][0]),
										model.arithm(model.intOffsetView(X[i][1],  w[i]), "<=", X[j][1])
								),
								model.arithm(model.intOffsetView(X[j][1],  h[j]), "<=", X[i][1])
						)
				);
				model.ifThen(
						model.and(
								model.arithm(R[i], "=", 1),
								model.arithm(R[j], "=", 0)
						),
						model.or(
								model.or(
										model.arithm(X[i][0], "<=", X[j][0]),
										model.arithm(model.intOffsetView(X[i][1],  h[i]), "<=", X[j][1])
								),
								model.arithm(model.intOffsetView(X[j][1],  w[j]), "<=", X[i][1])
						)
				);
				model.ifThen(
						model.and(
								model.arithm(R[i], "=", 1),
								model.arithm(R[j], "=", 1)
						),
						model.or(
								model.or(
										model.arithm(X[i][0], "<=", X[j][0]),
										model.arithm(model.intOffsetView(X[i][1],  h[i]), "<=", X[j][1])
								),
								model.arithm(model.intOffsetView(X[j][1],  h[j]), "<=", X[i][1])
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
			for (int i = 0; i < N; i++) {
				int rot = R[i].getValue();
				int a = X[i][0].getValue();
				int b = X[i][1].getValue();
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
			endTime = System.currentTimeMillis();
			System.out.print("Run time: " + (endTime - startTime)/1000.0 +  "s");
		}
	}
	public static void main(String[] args) {
		BinPackingChoco app = new BinPackingChoco();
		app.solve();
	}
}
