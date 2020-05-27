package planningoptimization115657k62.LeTrungHoangLong;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {

	LocalSearchManager lsm;
	VarIntLS[][] X;
	ConstraintSystem S;

	public void initModel(){
		lsm= new LocalSearchManager();
		X = new VarIntLS[10][10];
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				X[i][j] = new VarIntLS(lsm,1,9);
				X[i][j].setValue(j+1);
			}
		}
		S = new ConstraintSystem(lsm);
		// define rang buoc AllDifferent theo hang
		for(int i = 0; i < 9; i++){
			VarIntLS[] y = new VarIntLS[9];
			for(int j = 0; j < 9; j++)
				y[j] = X[i][j];
			S.post(new AllDifferent(y));
			}
		// define rang buoc AllDifferent theo cot
		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = X[j][i];
			}
			S.post(new AllDifferent(y));
		}
		for(int i = 0; i <= 2; i++){
			for(int j = 0; j <= 2; j++){
				VarIntLS[] y = new VarIntLS[9];
				int idx= -1;
				for(int im = 0; im <= 2; im++)
					for(int jm = 0; jm <= 2; jm++){
						idx++;
						y[idx] = X[3*i+im][3*j+jm];
					}
				S.post(new AllDifferent(y));
			}
		}
		lsm.close();
	}

	public void search() {
		class Move {
			int i;
			int j1;
			int j2;

			public Move(int i, int j1, int j2) {
				this.i = i;
				this.j2 = j2;
				this.j1 = j1;
			}
		}

		Random R = new Random();
		ArrayList<Move> candidates = new ArrayList<Move>();
		int it = 0;

		while (it <= 100000 && S.violations() > 0) {
			candidates.clear();
			int minDelta = Integer.MAX_VALUE;
			for (int i = 0; i < 9; i++) {
				for (int j1 = 0; j1 < 8; j1++) {
					for (int j2 = j1 + 1; j2 < 9; j2++) {
						int delta = S.getSwapDelta(X[i][j1], X[i][j2]);
						if (delta < minDelta) {
							candidates.clear();
							candidates.add(new Move(i, j1, j2));
							minDelta = delta;
						} else if (delta == minDelta)
							candidates.add(new Move(i, j1, j2));
					}
				}
			}
			int idx = R.nextInt(candidates.size());
			Move m = candidates.get(idx);
			int i = m.i;
			int j1 = m.j1;
			int j2 = m.j2;
			X[i][j1].swapValuePropagate(X[i][j2]);
			it++;
		}
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++)
				System.out.print(X[i][j].getValue() + " ");
			System.out.println();
		}
	}


	public static void main(String[] args) {
		Sudoku app = new Sudoku();
		app.initModel();
		app.search();
	}
}
