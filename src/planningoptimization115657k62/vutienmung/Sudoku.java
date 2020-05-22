package planningoptimization115657k62.vutienmung;

import localsearch.search.*;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {
	LocalSearchManager mgr;
	VarIntLS[][] x;
	ConstraintSystem S;
	
	public void buildModel() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[9][9];
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				x[i][j] = new VarIntLS(mgr, 1, 9);
			}
		}
		S = new ConstraintSystem(mgr);
		
		// constraint on rows
		for(int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for(int j = 0; j < 9; j++) y[i] =  x[i][j];
			S.post(new AllDifferent(y));
		}
		
		// constraints on columns
		for(int j = 0; j < 9; j++) {
			VarIntLS[] y = new VarIntLS[9];
			for(int i = 0; i < 9; i++) {
				y[i] = x[i][j];
			}
			S.post(new AllDifferent(y));
		}
		
		//constraints on sub-squares 3x3
		for(int I = 0; I < 3; I++) {
			for(int J = 0; J < 3; J++) {
				VarIntLS[] y = new VarIntLS[9];
				int idx = -1;
				for(int i = 0; i < 3; i++) {
					for(int j = 0; j < 3; j++) {
						idx++;
						y[idx] = x[3*I + i][3*J +j];
					}
				}
				S.post(new AllDifferent(y));
			}
		}
		mgr.close();
	}
	public void search() {
		HillClimbingSearch searcher = new HillCimbingSearch();
		
		
	}
}
