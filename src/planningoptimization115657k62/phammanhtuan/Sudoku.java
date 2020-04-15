package phammanhtuan;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Sudoku {

	class Move {
		int i;
		int j1;
		int j2;
		public Move (int i,int j1,int j2) {
			this.i=i;
			this.j1=j1;
			this.j2=j2;
		}	
	}
	private void exploreNeighborhood (IConstraint S,VarIntLS[][] X,ArrayList<Move> cand) {
		cand.clear();
		int minDelta = Integer.MAX_VALUE;
		for (int i=0;i<9;i++)
			for (int j1=0;j1<8;j1++)
				for (int j2=j1+1;j2<9;j2++) {
					int delta = S.getSwapDelta(X[i][j1], X[i][j2]);
					if (delta<minDelta) {
						cand.clear();
						cand.add(new Move(i,j1,j2));
						minDelta = delta;
					} else if (delta==minDelta) {
						cand.add(new Move(i,j1,j2));
					}
				}
	}
	private void search (ConstraintSystem S,int maxIter,VarIntLS[][] X) {
		ArrayList<Move> cand = new ArrayList<Sudoku.Move>();
		Random r = new Random();
		int it=0;
		while(it<maxIter && S.violations()>0) {
			exploreNeighborhood(S, X, cand);
			int idx = r.nextInt(cand.size());
			Move m = cand.get(idx);
			int i=m.i;int j1=m.j1;int j2=m.j2;
			X[i][j1].swapValuePropagate(X[i][j2]);
			System.out.println("Step : "+it+" violations: "+S.violations());
			it++;
		}
	}
public static void main(String[] args) {
	int N = 9;
	LocalSearchManager mng = new LocalSearchManager();
	VarIntLS[][] X = new VarIntLS[N][N];
	for (int i=0;i<N;i++) 
		for (int j=0;j<N;j++) {
			X[i][j] = new VarIntLS(mng, 1 , N);
			X[i][j].setValuePropagate(j+1);
		}
	ConstraintSystem S = new ConstraintSystem(mng);
	for (int i=0;i<N;i++) {
		S.post(new AllDifferent(X[i]));
	}
	for (int i=0;i<N;i++) {
		VarIntLS[] y = new VarIntLS[N];
		for (int j=0;j<N;j++) y[j] = X[j][i];
		S.post(new AllDifferent(y));
	}
	for (int I=0;I<N/3;I++)
		for (int J=0;J<N/3;J++) {
			VarIntLS[] y = new VarIntLS[N];
			int idx=0;
			for (int i=0;i<N/3;i++)
				for (int j=0;j<N/3;j++) {
					y[idx++]=X[I*N/3+i][J*N/3+j];
				}
			S.post(new AllDifferent(y));
		}
	mng.close();
//	HimbClimbingSearch search = new HimbClimbingSearch();
//	search.search(S, 1000000);
	Sudoku app = new Sudoku();
	app.search(S,100000,X);
	for (int i=0;i<N;i++) {
		for (int j=0;j<N;j++) System.out.print(X[i][j].getValue() + " ");
		System.out.println();
	}
}
}
