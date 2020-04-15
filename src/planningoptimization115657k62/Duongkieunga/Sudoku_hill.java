package planningoptimization115657k62.Duongkieunga;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import planningoptimization115657k62.Duongkieunga.HimbClimbingSearch.Move;

public class Sudoku_hill {
	static LocalSearchManager mgr;
	static VarIntLS[][] X;
	static ConstraintSystem S;

	class Move{
		int i,j1,j2;
		public Move(int i, int j1, int j2) {
			this.i=i;
			this.j1=j1;
			this.j2=j2;
		}
	}
	public void buiModel() {
		mgr=new LocalSearchManager();
		X=new VarIntLS[9][9];
		
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				X[i][j]=new VarIntLS(mgr,1,9);
				X[i][j].setValue(j+1); 
			}
		}
		
		S= new ConstraintSystem(mgr);
		
		// define rang buoc AllDifferent theo hang
		for(int i=0;i<9;i++) {
			VarIntLS[] y = new VarIntLS[9];
			for(int j=0;j<9;j++) {
				y[j] = X[i][j];
			}
			S.post(new AllDifferent(y));
		}
		
		// define rang buoc AllDifferent theo cot
		for(int i=0;i<9;i++) {
			VarIntLS[] x = new VarIntLS[9];
			for(int j=0;j<9;j++) {
				x[j] = X[j][i];
			}
			S.post(new AllDifferent(x));
		}
		
		// define rang buoc AllDifferent theo hinh vuong con 3x3
		for(int I=0;I<=2;I++) {
			for(int J=0;J<=2;J++) {
				VarIntLS[] y = new VarIntLS[9];
				int idx=-1;
				for(int  i = 0; i <= 2; i++) {
					for(int j = 0; j <= 2; j++){
						idx++;
						y[idx] = X[3*I+i][3*J+j];
					}
					
				}
				S.post(new AllDifferent(y));
			}
		}
		
		mgr.close();

	}
	public void generateInitialSolution() {
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				X[i][j].setValuePropagate(j+1);
			}
		}
	}
	private void exploreNeighborhood(ArrayList<Move> cand) {
		cand.clear();
		int minDelta=Integer.MAX_VALUE;
		for(int i=0;i<9;i++) {
			for(int j1=0;j1<8;j1++) {
				for(int j2=j1+1;j2<9;j2++) {
					int delta=S.getSwapDelta(X[i][j1], X[i][j2]);
					if(delta<=0) {
						if(delta<minDelta) {
							cand.clear();
							cand.add(new Move(i,j1,j2));
							minDelta=delta;
						}
						else if(delta==minDelta) {
							cand.add(new Move(i,j1,j2));
						}
					}
				}
			}
		}
	}
	
	public void search() {
//		HimbClimbingSearch searcher= new HimbClimbingSearch();
//		searcher.search(S, 100);
		generateInitialSolution();
		int it=0;
		Random R=new Random();
		ArrayList<Move> cand= new ArrayList<Move>();
		while(it<10000 && S.violations()>0) {//violation: so bien khong thoa man rang buoc
			exploreNeighborhood(cand);
			if(cand.size()==0) {
				System.out.println("reach local optimum");break;
			}
			Move m=cand.get(R.nextInt(cand.size()));
			X[m.i][m.j1].swapValuePropagate(X[m.i][m.j2]);//local move swap 2 vars
			
			System.out.println("Step "+it+" violations = "+S.violations());
			it++;
		}
	}
	
	public void print() {
		for(int i=0;i<9;i++) {
			for(int j=0;j<9;j++) {
				System.out.print(""+X[i][j].getValue()+" ");
			}
			System.out.println();
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		Random R = new Random();
		ArrayList<Move> candidates = new ArrayList<Move>();
		
		Sudoku_hill app = new Sudoku_hill();
		app.buiModel();
		app.search();
		app.print();
			
	}
	

}
