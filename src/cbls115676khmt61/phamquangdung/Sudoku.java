package cbls115676khmt61.phamquangdung;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {
	class Move{
		int i;
		int j1;
		int j2;
		public Move(int i, int j1, int j2){
			this.i = i; this.j1 = j1; this.j2 = j2;
		}
	}
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[][] X;
	
	public void stateModel(){
		mgr = new LocalSearchManager();
		X = new VarIntLS[9][9];
		for(int i = 0; i < 9; i++)
			for(int j = 0; j < 9; j++){
				X[i][j] = new VarIntLS(mgr,1,9);
				X[i][j].setValue(j+1);// moi hang i: 1,2,3,4,5,6,7,8,9
			}
		S = new ConstraintSystem(mgr);
		/*
		for(int i = 0; i < 9; i++){
			VarIntLS[] y = new VarIntLS[9];
			for(int j = 0; j < 9; j++) y[j] = X[i][j];
			S.post(new AllDifferent(y));
		}
		*/
		for(int j = 0; j < 9; j++){
			VarIntLS[] y = new VarIntLS[9];
			for(int i = 0; i < 9; i++) y[i] = X[i][j];
			S.post(new AllDifferent(y));
		}
		
		for(int I = 0; I < 3; I++){
			for(int J = 0; J < 3; J++){
				VarIntLS[] y = new VarIntLS[9];
				int idx = -1;
				for(int i = 0; i < 3; i++){
					for(int j = 0; j < 3; j++){
						idx++;
						y[idx] = X[3*I+i][3*J+j];
					}
				}
				S.post(new AllDifferent(y));
			}
		}
		mgr.close();
	}
	public void search(){
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 100000);
	}
	private void exploreNeighborhood(ArrayList<Move> cand){
		int minDelta = Integer.MAX_VALUE;
		cand.clear();
		for(int i = 0; i < 9; i++){
			for(int j1 = 0; j1 < 8; j1++){
				for(int j2 = j1 + 1; j2 < 9; j2++){
					int delta = S.getSwapDelta(X[i][j1], X[i][j2]);// danh gia do tot cua lang gieng
					if(delta < minDelta){
						cand.clear(); cand.add(new Move(i,j1,j2)); minDelta = delta;
					}else if(delta == minDelta){
						cand.add(new Move(i,j1,j2));
					}
				}
			}
		}
	}
	public void search1(){
		Random R = new Random();
		ArrayList<Move> cand = new ArrayList<Move>();
		int it = 0;
		while(it < 100000 && S.violations() > 0){
			exploreNeighborhood(cand);
			if(cand.size() == 0){
				break;
			}
			Move m = cand.get(R.nextInt(cand.size()));
			X[m.i][m.j1].swapValuePropagate(X[m.i][m.j2]);
			it++;
			System.out.println("Step " + it + " S = " + S.violations());
		}				
	}
	public void printSolution(){
		for(int i = 0; i < 9; i++){
			for(int j = 0; j < 9; j++){
				System.out.print(X[i][j].getValue() + " ");
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sudoku app = new Sudoku();
		app.stateModel();
		//app.search();
		app.search1();
		app.printSolution();
	}

}
