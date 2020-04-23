package planningoptimization115657k62.levanlinh.cbls;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class SudokuCBLS {
	
	LocalSearchManager mgr;
	VarIntLS[][] x;
	ConstraintSystem S;
	
	public void buildModel() {
		LocalSearchManager mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		
		x = new VarIntLS[9][9];
		for (int i = 0; i < 9; ++i)
			for (int j = 0; j < 9; ++j) {
				x[i][j] = new VarIntLS(mgr, 1, 9);
				x[i][j].setValue(j+1);
			}
		// Rang buoc theo cot 
		for (int i = 0; i < 9; ++i) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; ++j) {
				y[j] = x[j][i];
			}
			S.post(new AllDifferent(y));
		}
		
		for (int i = 0; i <=2 ; ++i)
			for (int j = 0; j <= 2; ++j) {
				VarIntLS[] y = new VarIntLS[9];
				int id = -1;
				for (int i1 = 0; i1 <= 2; i1++)
					for (int j1 = 0; j1 <= 2; j1++)
						y[++id] = x[3*i + i1][3*j+j1];
				S.post(new AllDifferent(y));
			}
		
		mgr.close();
	}
	
	public class Move {
		int i;
		int j1;
		int j2;
		
		public Move(int i, int j1, int j2) {
			this.i = i;
			this.j1 = j1;
			this.j2 = j2;
		}
	}
	
	public void ExploreNeighborhood(ArrayList<Move> cand) {
		cand.clear();
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < 9; ++i) {
			for (int j = 0; j < 8; ++j) {
				for (int k = j+1; k < 9; ++k) {
					int delta = S.getSwapDelta(x[i][j], x[i][k]);
					if (delta <= 0) {
						if (delta < minDelta) {
							minDelta = delta;
							cand.clear();
							cand.add(new Move(i, j, k));
						}
						if (delta == minDelta) {
							cand.add(new Move(i, j, k));
						}
					}
				}
			}
		}
		//return cand;
	}
	
	public void search(int maxIter) {
		Random R = new Random();
		ArrayList<Move> cand = new ArrayList<>();
		int it = 0;
		while (S.violations() > 0 && it < maxIter) {
			ExploreNeighborhood(cand);
			if (cand.size() == 0) {
				System.out.println ("Reach local minimum!");
				break;
			} else {
				Move m = cand.get(R.nextInt(cand.size()));
				x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]); 
				System.out.println ("HCS: Step " + it + ", violation: " + S.violations());
				it++;
			}
		}
	}
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SudokuCBLS app = new SudokuCBLS();
		app.buildModel();
		app.search(10000);

	}

}
