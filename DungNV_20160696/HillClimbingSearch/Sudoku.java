package baitap;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[][] X;
	Random R = new Random();

	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[9][9];
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				X[i][j] = new VarIntLS(mgr,1,9);
				X[i][j].setValue(j+1); // moi hang i: 1,2,3,4,....9
			}
		}
		S = new ConstraintSystem(mgr);
		
//		for(int i = 0; i < 9; i++) {
//			VarIntLS[] y = new VarIntLS[9];
//			for(int j = 0; j < 9; j++) {
//				y[j] = X[i][j];
//			}
//			S.post(new AllDifferent(y));
//		}
		
		for(int i = 0; i < 9; i++) {
			VarIntLS[] y = new VarIntLS[9];
			for(int j = 0; j < 9; j++) {
				y[j] = X[j][i];
			}
			S.post(new AllDifferent(y));
		}
		
		for(int I = 0; I < 3; I++) {
			for(int J = 0; J < 3; J++) {
				VarIntLS[] y = new VarIntLS[9];
				int idx = -1;
				for(int i = 0; i < 3; i++)
					for(int j = 0; j < 3; j++) {
						idx++;
						y[idx] = X[3*I+i][3*J+j];
					}
				S.post(new AllDifferent(y));
			}
		}
		mgr.close();
	}
	
	class AssignMove{
		int i;
		int j1;
		int j2;
		public AssignMove(int i, int j1, int j2){
			this.i = i; 
			this.j1 = j1;
			this.j2 = j2;
		}
	}
	
	public void printSolution() {
		for(int i = 0; i < 9; i++) {
			for(int j =0 ;j < 9; j++)
				System.out.print(X[i][j].getValue()+" ");
			System.out.println();
		}		
	}
	
	public void search1() {
		int it = 0; 
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		while(it < 100000 & S.violations() > 0) {
			int minDelta = Integer.MAX_VALUE;
			candidate.clear();
			for(int i = 0; i < 9 ; i++) {
				for(int j1 = 0; j1 < 8; j1++) {
					for(int j2 = j1 + 1; j2 < 9; j2++) {
						int delta = S.getSwapDelta(X[i][j1], X[i][j2]);
						if(delta < minDelta) {
							candidate.clear();
							candidate.add(new AssignMove(i, j1, j2));
							minDelta = delta;
						}else if(delta == minDelta)
							candidate.add(new AssignMove(i, j1, j2));
					}
				}
			}
			int idx = R.nextInt(candidate.size());
			AssignMove m = candidate.get(idx);
			int i = m.i; int j1 = m.j1; int j2 = m.j2;
			X[i][j1].swapValuePropagate(X[i][j2]);
			System.out.println("Step "+it+" violations= " + S.violations());
			it++;
		}
	}
	
	public void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S,  100000);
	}
	
	public static void main(String[] args) {
		Sudoku App = new Sudoku();
		App.stateModel();
		App.search1();
	}
}
