package planningoptimization115657k62.NguyenVanTien;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {

	LocalSearchManager lsm;
	VarIntLS[][] x;
	ConstraintSystem CS;

	public void buildModel() {
		lsm = new LocalSearchManager();
		x = new VarIntLS[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				x[i][j] = new VarIntLS(lsm, 1, 9);
				x[i][j].setValue(j + 1);
			}
		}

		CS = new ConstraintSystem(lsm);

		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = x[i][j];
			}
			CS.post(new AllDifferent(y));
		}

		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = x[j][i];
			}
			CS.post(new AllDifferent(y));
		}

		for (int I = 0; I < 3; I++) {
			for (int J = 0; J < 3; J++) {
				VarIntLS[] y = new VarIntLS[9];
				int idx = -1;
				for (int i = 0; i < 3; i++) {
					for (int j = 0; j < 3; j++) {
						idx++;
						y[idx] = x[3 * I + i][3 * J + j];
					}
				}
				CS.post(new AllDifferent(y));
			}
		}

		lsm.close();
	}

	class Move {
		int i;
		int j1;
		int j2;

		public Move(int i, int j1, int j2) {
			this.i = i;
			this.j1 = j1;
			this.j2 = j2;
		}

	}

	public void search(int maxIter) {
		buildModel();
		int it = 0;
		ArrayList<Move> cand = new ArrayList<Move>();
		Random ran = new Random();

		while (it < maxIter && CS.violations() > 0) {
			int minDelta = Integer.MAX_VALUE;
			cand.clear();
			for (int i = 0; i < 9; i++) {
				for (int j1 = 0; j1 < 8; j1++) {
					for (int j2 = j1 + 1; j2 < 9; j2++) {
						int delta = CS.getSwapDelta(x[i][j1], x[i][j2]);
						if (delta < minDelta) {
							cand.clear();
							cand.add(new Move(i, j1, j2));
							minDelta = delta;
						} else if(delta == minDelta){
							cand.add(new Move(i, j1, j2));
						}
					}
				}
			}

			int idx = ran.nextInt(cand.size());
			Move m = cand.get(idx);
			int i = m.i, j1 = m.j1, j2 = m.j2;
			x[i][j1].swapValuePropagate(x[i][j2]);

			System.out.println("Step " + it + " violations= " + CS.violations());
			it++;
		}

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print(x[i][j].getValue() + " ");
			System.out.println();
		}

	}


	public static void main(String[] args) {
		Sudoku app = new Sudoku();
		app.search(100000);
	}
}
