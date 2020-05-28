package planningoptimization115657k62.ngoviethoang.csp.binpacking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class BinPackingFromFile {
	int H;
	int W;
	int n;
//	int[] h = new int[] {2, 4, 6};
//	int[] w = new int[] {3, 3, 1};
	ArrayList<Integer> h = new ArrayList<Integer>();
	ArrayList<Integer> w = new ArrayList<Integer>();
	IntVar[][] x;
	IntVar[] r;
	Model model;
	public void readData(String file) {
		try {
			File fi = new File(file);
			Scanner s = new Scanner(fi);
			W = s.nextInt(); H = s.nextInt();
			int temp;
			while(true) {
				temp = s.nextInt();
				//System.out.println(temp);
				if (temp == -1) {
					break;
				} else {
					w.add(temp);
					h.add(s.nextInt());
				}
			}
			n = h.size();
			s.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public void binpacking() {
		String file = "./data/csp/BinPacking2D/bin-packing-2D-W10-H7-I6.txt";
			readData(file);
		model = new Model("BinPacking");
		x = new IntVar[n][2];
		r = new IntVar[n];
		r = model.intVarArray(n,0,1);
		for (int i = 0; i < n; i++) {
			x[i][0] = model.intVar(0,H-1);
			x[i][1] = model.intVar(0,W-1);
		}
				
		for (int i = 0; i < n; i++) {
			model.ifThenElse(
					model.arithm(r[i], "=", 0), 
					model.and(
						model.arithm(model.intOffsetView(x[i][0], h.get(i)), "<=", H),
						model.arithm(model.intOffsetView(x[i][1], w.get(i)), "<=", W)
					),
					model.and(
						model.arithm(model.intOffsetView(x[i][0], w.get(i)), "<=", H),
						model.arithm(model.intOffsetView(x[i][1], h.get(i)), "<=", W)
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
									model.arithm(model.intOffsetView(x[i][0],h.get(i)), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],h.get(j)), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],w.get(i)), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],w.get(j)), "<=", x[i][1])
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
									model.arithm(model.intOffsetView(x[i][0],h.get(i)), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],w.get(j)), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],w.get(i)), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],h.get(j)), "<=", x[i][1])
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
									model.arithm(model.intOffsetView(x[i][0],w.get(i)), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],h.get(j)), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],h.get(i)), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],w.get(j)), "<=", x[i][1])
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
									model.arithm(model.intOffsetView(x[i][0],w.get(i)), "<=", x[j][0]),
									model.arithm(model.intOffsetView(x[j][0],w.get(j)), "<=", x[i][0])
								),
								model.or (
									model.arithm(model.intOffsetView(x[i][1],h.get(i)), "<=", x[j][1]),
									model.arithm(model.intOffsetView(x[j][1],h.get(j)), "<=", x[i][1])
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
					for (int j = 0; j < w.get(i); j++) {
						for (int k = 0; k < h.get(i); k++) {
							map[a+j][b+k] = i+1;
						}
					}
				} else {
					for (int j = 0; j < h.get(i); j++) {
						for (int k = 0; k < w.get(i); k++) {
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
		BinPackingFromFile app = new BinPackingFromFile();
		app.binpacking();
	}
}
