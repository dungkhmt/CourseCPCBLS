planningoptimization115657k62.trancongdao;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sodoku {
	LocalSearchManager mgr;
	VarIntLS[][] x;
	ConstraintSystem S;

	public void build() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				x[i][j] = new VarIntLS(mgr, 1, 9);
			}
		}
		S = new ConstraintSystem(mgr);

		// constraint on rows;
		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = x[i][j];
			}
			S.post(new AllDifferent(y));

		}
		
		// constraint on columns
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				VarIntLS[] y = new VarIntLS[9];
				int index = -1;
				for (int k = 0; k < 3; k++) {
					for (int h = 0; h < 3; h++) {
						index++;
						y[index] = x[3 * i + k][3 * j + h];
					}
				}
				S.post(new AllDifferent(y));

			}
		}
		mgr.close();
	}

	public void generateInit() {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				x[i][j].setValuePropagate(j + 1);
			}
		}
	}

	class Move {
		int i, j1, j2;

		public Move(int i, int j1, int j2) {
			this.i = i;
			this.j1 = j1;
			this.j2 = j2;
		}
	}

	public void exPlore(ArrayList<Move> cand) {
		cand.clear();
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < 9; i++) {
			for (int j1 = 0; j1 < 8; j1++) {
				for (int j2 = j1 + 1; j2 < 9; j2++) {
					int delta = S.getSwapDelta(x[i][j1], x[i][j2]);
					if (delta < minDelta) {
						cand.clear();
						cand.add(new Move(i, j1, j2));
						minDelta = delta;
					} else if (delta == minDelta) {
						cand.add(new Move(i, j1, j2));
					}
				}
			}
		}

	}

	public void search() {
		generateInit();
		Random R = new Random();
		ArrayList<Move> cand = new ArrayList<Move>();
		int it = 0;
		while (it < 10000 & S.violations() > 0) {
			exPlore(cand);
			if (cand.size() == 0) {
				System.out.println("success");
				break;
			}
			Move m = cand.get(R.nextInt(cand.size()));
			x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]);
			System.out.println("step" + it + ", S = " + S.violations());
			it++;
		}

	}

	public static void main(String[] args) {
		Sodoku app = new Sodoku();
		app.build();
		app.search();
	}
}
