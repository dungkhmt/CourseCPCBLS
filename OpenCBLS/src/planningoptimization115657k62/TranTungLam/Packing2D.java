package buoi4;

import java.util.ArrayList;
import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Packing2D {

	int W = 4;
	int H = 6;
	int n = 3;
	int[] w = {3,3,1};
	int[] h = {2,4,6};
	List<Pair<Integer, Integer>> items = new ArrayList<Pair<Integer, Integer>>();
	
	public void solver() {
		for (int i = 0; i < n; i++) {
			Pair<Integer, Integer> item = new Pair<Integer, Integer>(w[i], h[i]);
			items.add(item);
		}
		
		Model model = new Model("2DPacking");
		IntVar[] x = new IntVar[n];
		IntVar[] y = new IntVar[n];
		IntVar[] o = new IntVar[n];
		
		for (int i = 0; i < n; i++) {
			x[i] = model.intVar(0, W);
			y[i] = model.intVar(0, H);
			o[i] = model.intVar(0, 1);
		}
		//constraint 1: every 2 item cannot overlap
		for (int i = 0; i < n; i++) {
			int wi = items.get(i).getW();
			int hi = items.get(i).getH();
			model.ifThen(model.arithm(o[i], "=", 0), 
						model.and(model.arithm(model.intOffsetView(x[i], wi), "<=", W), 
								model.arithm(model.intOffsetView(y[i], hi), "<=", H)
								)
						);
			model.ifThen(model.arithm(o[i], "=", 1), 
						model.and(model.arithm(model.intOffsetView(x[i], hi), "<=", W), 
								model.arithm(model.intOffsetView(y[i], wi), "<=", H)
								)
						);
		}
		
		//constraint 2
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				int wi = items.get(i).getW();
				int hi = items.get(i).getH();
				int wj = items.get(j).getW();
				int hj = items.get(j).getH();
				if (j != i) {
					model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0)), 
								model.or(model.arithm(model.intOffsetView(x[i], wi), "<=", x[j]),
										model.arithm(model.intOffsetView(x[j], wj), "<=", x[i]),
										model.arithm(model.intOffsetView(y[i], hi), "<=", y[j]),
										model.arithm(model.intOffsetView(y[j], hj), "<=", y[i])
								)
					);
					model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1)), 
							model.or(model.arithm(model.intOffsetView(x[i], hi), "<=", x[j]),
									model.arithm(model.intOffsetView(x[j], hj), "<=", x[i]),
									model.arithm(model.intOffsetView(y[i], wi), "<=", y[j]),
									model.arithm(model.intOffsetView(y[j], wj), "<=", y[i])
							)
					);
					model.ifThen(model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1)), 
							model.or(model.arithm(model.intOffsetView(x[i], wi), "<=", x[j]),
									model.arithm(model.intOffsetView(x[j], hj), "<=", x[i]),
									model.arithm(model.intOffsetView(y[i], hi), "<=", y[j]),
									model.arithm(model.intOffsetView(y[j], wj), "<=", y[i])
							)
					);
					model.ifThen(model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0)), 
							model.or(model.arithm(model.intOffsetView(x[i], hi), "<=", x[j]),
									model.arithm(model.intOffsetView(x[j], wj), "<=", x[i]),
									model.arithm(model.intOffsetView(y[i], wi), "<=", y[j]),
									model.arithm(model.intOffsetView(y[j], hj), "<=", y[i])
							)
					);
				}
			}
		}
		
		while(model.getSolver().solve()) {
			for (int i = 0; i < n; i++) {
				System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
			}
			System.out.println("---------------");
		}
		
	}	
	public static void main(String[] args) {
		
		Packing2D app = new Packing2D();
		app.solver();
		

	}

}
