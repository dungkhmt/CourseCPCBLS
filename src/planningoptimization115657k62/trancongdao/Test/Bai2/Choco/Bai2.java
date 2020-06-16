package chocoSolver;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Bai2 {
	int L = 6;
	int W = 4;
	int n = 6;
	int[] l = new int[] {4,1,2,1,4,3};
	int[] w = new int[] {1,3,2,3,1,2};
	IntVar[][] x = new IntVar[n][2];
	IntVar[] r = new IntVar[n];
	Model model;
	public void binpacking() {
		model = new Model("BinPacking");
		r = model.intVarArray(n,0,1);
		for (int i = 0; i < n; i++) {
			x[i][0] = model.intVar(0,L-1);
			x[i][1] = model.intVar(0,W-1);
		}
				
		for (int i = 0; i < n; i++) {
			model.ifThenElse(
					model.arithm(r[i], "=", 0), 
					model.and(
						model.arithm(model.intOffsetView(x[i][0], l[i]), "<=", L),
						model.arithm(model.intOffsetView(x[i][1], w[i]), "<=", W)
					),
					model.and(
						model.arithm(model.intOffsetView(x[i][0], w[i]), "<=", L),
						model.arithm(model.intOffsetView(x[i][1], l[i]), "<=", W)
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
									model.arithm(model.intOffsetView(x[i][0],l[i]), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],l[j]), "<=", x[i][0])
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
									model.arithm(model.intOffsetView(x[i][0],l[i]), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],w[j]), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],w[i]), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],l[j]), "<=", x[i][1])
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
									model.arithm(model.intOffsetView(x[j][0],l[j]), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],l[i]), "<=", x[j][1]),
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
									model.arithm(model.intOffsetView(x[i][1],l[i]), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],l[j]), "<=", x[i][1])
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
			int[][] rec = new int[L][W];
			for (int i = 0; i < n; i++) {
				int rot = r[i].getValue();
				int a = x[i][0].getValue();
				int b = x[i][1].getValue();
				if (rot == 1) {
					for (int j = 0; j < w[i]; j++) {
						for (int k = 0; k < l[i]; k++) {
							rec[a+j][b+k] = i;
						}
					}
				} else {
					for (int j = 0; j < l[i]; j++) {
						for (int k = 0; k < w[i]; k++) {
							rec[a+j][b+k] = i;
						}
					}
				}
			}
			for (int i = 0; i < L; i++) {
				for (int j = 0; j < W; j++) {
					System.out.print(rec[i][j] + " ");
				}
				System.out.println();
			}
		}
	}
	
	public static void main(String[] args) {
		Bai2 app = new Bai2();
		app.binpacking();
	}
}
