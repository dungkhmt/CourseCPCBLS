package planningoptimization115657k62.phamvietbang.project;
import java.util.*;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.*;
import localsearch.model.*;
public class Sudoku {
	LocalSearchManager mgr;
	VarIntLS[][]x;
	ConstraintSystem S;
	public void build() {
		mgr=new LocalSearchManager();
		x= new VarIntLS[9][9];
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				x[i][j]=new VarIntLS(mgr,1,9);
				x[i][j].setValue(j+1);
			}
		}
		S=new ConstraintSystem(mgr);
		for(int i=1;i<9;i++) {
			VarIntLS[]y=new VarIntLS[9];
			for(int j=0;j<9;j++) {
				y[j]=x[i][j];			
			}
			S.post(new AllDifferent(y));
		}
		for(int i=0;i<9;i++) {
			VarIntLS[]y=new VarIntLS[9];
			for (int j=0;j<9;j++) {
				y[j]=x[j][i];			
			}
			S.post(new AllDifferent(y));
		}
		for(int i=0;i<=2;i++) {
			for(int j=0;j<=2;j++) {
				VarIntLS[] y=new VarIntLS[9];
				int indx=-1;
				for(int p=0;p<=2;p++) {
					for(int q=0;q<=2;q++) {
						indx++;
						y[indx]=x[3*i+p][3*j+q];
					}
				}
				S.post(new AllDifferent(y));
			}
		}
		mgr.close();
	}
	public void search() {
		class Move{
			int i,j1,j2;
			public Move(int i, int j1,int j2) {
				this.i=i;this.j1=j1;this.j2=j2;
			}
		}
		Random R=new Random();
		ArrayList<Move> cand=new ArrayList<Move>();
		int it=0;
		while(it<=1000&&S.violations()>0) {
			cand.clear();int minDelta=Integer.MAX_VALUE;
			for(int i=0;i<9;i++) {
				for(int j1=0;j1<8;j1++) {
					for(int j2=j1+1;j2<9;j2++) {
						int delta=S.getSwapDelta(x[i][j1], x[i][j2]);
						if(delta<minDelta) {
							cand.clear();
							cand.add(new Move(i,j1,j2));
							minDelta=delta;
						}else if(delta==minDelta) {
							cand.add(new Move(i,j1,j2));
						}
					}
				}
			}
			int idx=R.nextInt(cand.size());
			Move m=cand.get(idx);
			int i=m.i;int j1=m.j1;int j2=m.j2;
			x[i][j1].swapValuePropagate(x[i][j2]);
		
			System.out.print("step: "+ it+" violation: ");
			System.out.println(S.violations());
			it++;
		}
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				System.out.print(x[i][j].getValue()+" ");
				
			}
			System.out.println();
		}
		
	}
	public static void main(String[] args) {
		Sudoku app=new Sudoku();
		app.build();
		app.search();
	}
}
