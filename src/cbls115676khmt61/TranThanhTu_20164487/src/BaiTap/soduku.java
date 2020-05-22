package BaiTap;


import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class soduku {
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[][] x;

	public void stateModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[9][9];
		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 9; j++) {
				x[i][j] = new VarIntLS(mgr, 1, 9);
				x[i][j].setValue(j + 1);
			}
		S = new ConstraintSystem(mgr);
		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = x[i][j];
			}
			S.post(new AllDifferent(y));

		}

		for (int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for (int j = 0; j < 9; j++) {
				y[j] = x[j][i];
			}
			S.post(new AllDifferent(y));

		}

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				VarIntLS[] y = new VarIntLS[9];
				int idx = -1;
				for (int I = 0; I < 2; I++)
					for (int J = 0; J < 2; J++) {
						idx++;
						y[idx] = x[3 * i + I][3 * j + J];
					}
				S.post(new AllDifferent(y));
			}
		}
		mgr.close();
	}
	
	public void search1() {
		HillClimbingSearch serch = new HillClimbingSearch();
		serch.LocalSearch(S, 10000);
	}
	public static void main(String[] args) {
		soduku sdk = new soduku();
		sdk.stateModel();
		sdk.search1();
	}
}
