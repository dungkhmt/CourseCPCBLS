package lol;
import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {

	LocalSearchManager mgr;
	VarIntLS x[][];
	ConstraintSystem s;
	
	public void buildModel() {
		mgr=new LocalSearchManager();
		x=new VarIntLS[9][9];
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				x[i][j]=new VarIntLS(mgr,1,9);
				x[i][j].setValue(j+1);
			}
		}
		s=new ConstraintSystem(mgr);
		for(int i=0;i<9;i++) {
			VarIntLS y[]=new VarIntLS[9];
			for(int j=0;j<9;j++) {
				y[j]=x[i][j];
			}
			s.post(new AllDifferent(y));
		}
		for(int i=0;i<9;i++) {
			VarIntLS y[]=new VarIntLS[9];
			for(int j=0;j<9;j++) {
				y[j]=x[j][i];
			}
			s.post(new AllDifferent(y));
		}
		for(int I = 0; I <= 2; I++){ 
			for(int J = 0; J <= 2;J++){ 
				VarIntLS[] y = new VarIntLS[9]; 
				int idx = -1; 
				for(int i = 0; i <= 2; i++) 
					for(int j = 0; j <= 2; j++){ 
						idx++; y[idx] = x[3*I+i][3*J+j]; 
					}
				s.post(new AllDifferent(y)); 
			}
		} 
		mgr.close(); 
	}
	
	class Move {
		int i;
		int j1,j2;
		public Move(int i,int j1,int j2) {
			this.i=i;
			this.j1=j1;
			this.j2=j2;
		}
	}
	
	public void search() {
		Random r=new Random();
		ArrayList<Move> candidates=new ArrayList<Move>();	
		int it=0;
		while(it<=100000 && s.violations()>0) {
			candidates.clear();
			int minDelta=Integer.MAX_VALUE;
			for(int i=0;i<9;i++) {
				for(int j1=0;j1<9;j1++) {
					for(int j2=j1+1;j2<9;j2++) {
						int delta=s.getSwapDelta(x[i][j1], x[i][j2]);
						if(delta<minDelta) {
							candidates.clear();
							candidates.add(new Move(i,j1,j2));
							minDelta=delta;
						}
						else if(delta==minDelta) {
							candidates.add(new Move(i,j1,j2));
						}
					}
				}
			}
			int index=r.nextInt(candidates.size());
			Move m=candidates.get(index);
			x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]);
			System.out.println("step"+it+" : violations "+s.violations()); 
			it++;
		}
		for(int i = 0; i < 9; i++){ 
			for(int j = 0; j < 9; j++) System.out.print(x[i][j].getValue() + " "); 
			System.out.println(); 
		}

	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Sudoku so=new Sudoku();
		so.buildModel();
		so.search();
	}

}
