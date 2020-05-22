package planningoptimization115657k62.nguyenthinhung;

import java.util.ArrayList;
import java.util.Random;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

import cbls115676khmt61.phamquangdung.HillClimbingSearch;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.HillClimbing;

public class sudoku {
	LocalSearchManager mgr;
	VarIntLS[][] x;
	ConstraintSystem C;
	
	public void buildModel(){ 
		mgr = new LocalSearchManager(); 
		x = new VarIntLS[9][9]; 
		for(int i = 0; i < 9; i++){ 
			for(int j = 0; j < 9; j++){ 
				x[i][j] = new VarIntLS(mgr,1,9); 
				x[i][j].setValue(j+1); 
			} 
		}
		
		C = new ConstraintSystem(mgr);
		for(int i = 0; i < 9; i++){ 
			VarIntLS[] y = new VarIntLS[9]; 
			for(int j = 0; j < 9; j++) {
				y[j] = x[i][j];
			}
			C.post(new AllDifferent(y)); 
		}
		
		for(int i = 0; i < 9; i++){ 
			VarIntLS[] y = new VarIntLS[9]; 
			for(int j = 0; j < 9; j++) 
				y[j] = x[j][i]; 
			C.post(new AllDifferent(y)); 
		} 
		
		for(int I = 0; I <= 2; I++){ 
			for(int J = 0; J <= 2;J++){ 
				VarIntLS[] y = new VarIntLS[9]; 
				int idx = -1; 
				for(int i = 0; i <= 2; i++) 
					for(int j = 0; j <= 2; j++){ 
						idx++; 
						y[idx] = x[3*I+i][3*J+j]; 
					}
					C.post(new AllDifferent(y)); 
				} 
			}
		
		mgr.close();
	}
	
	public void InitSolution() {
		for(int i = 0;i < 9;i++) {
			for(int j = 0;j < 9;j++) {
				x[i][j].setValuePropagate(j+1);
			}
		}
	}
	class Move{ 
		int i; 
		int j1; 
		int j2;
		public Move(int i, int j1, int j2){ 
			this.i = i; 
			this.j1 = j1; 
			this.j2 = j2; 
		} 
	}
	
	public void exploreNeighborhood(ArrayList<Move> cand) {
		cand.clear();
		int minDel = Integer.MAX_VALUE;
		for(int i = 0;i < 9;i++) {
			for(int j  = 0; j < 9;j++) {
				for(int t = j + 1;t < 9;t++) {
					int del = C.getSwapDelta(x[i][j], x[i][t]);
					if(del <= 0) {
						if(del < minDel) {
							cand.clear();
							cand.add(new Move(i, j, t));
							minDel = del;
						}else if(del == minDel){
							cand.add(new Move(i, j, t));
						}
					}
				}
			}
		}
	}
	public void search(){ 
		InitSolution();
		Random r = new Random();
		ArrayList<Move> cand = new ArrayList<sudoku.Move>();
		int stp = 0;
		while(stp < 100000 && C.violations() > 0) {
			exploreNeighborhood(cand);
			if(cand.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			}
			Move mv = cand.get(r.nextInt(cand.size()));
			x[mv.i][mv.j1].swapValuePropagate(x[mv.i][mv.j2]);
			System.out.println("Step:" + stp + ", Violations: " + C.violations());
			stp ++;
		}
	}
	
	public void printSol() {
		for(int i = 0;i < 9;i++) {
			for(int j = 0;j < 9;j++) {
				System.out.print(x[i][j].getValue() + " ");
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		sudoku obj = new sudoku();
		obj.buildModel();
		obj.search();
		obj.printSol();
	}
	
}
