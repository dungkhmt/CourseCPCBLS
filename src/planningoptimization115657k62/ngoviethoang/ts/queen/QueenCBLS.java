package planningoptimization115657k62.ngoviethoang.ts.queen;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class QueenCBLS {

	class Move{
		int i; int v;
		public Move(int i, int v){
			this.i = i; this.v = v;
		}
	}
	
	public void solve(){
		int N = 50;
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[N];
		for(int i = 0; i < N; i++)
			X[i] = new VarIntLS(mgr,0,N-1);
		ConstraintSystem S = new ConstraintSystem(mgr);
		
		S.post(new AllDifferent(X));
		
		IFunction[] f1 = new IFunction[N];		
		 for(int i = 0; i < N; i++)			
		  f1[i] = new FuncPlus(X[i], i);		
		 S.post(new AllDifferent(f1));
		 			 
		 IFunction[] f2 = new IFunction[N];		
		 for(int i = 0; i < N; i++)			
		  f2[i] = new FuncPlus(X[i], -i);		
		 S.post(new AllDifferent(f2));			
		 mgr.close();
		 
		 //TabuSearch ts = new TabuSearch();
		 //ts.search(S, 20, 100, 10000, 200);
		 
		 // the tabu search
		 
		 int[][] tabu = new int[N][N];
		 int tbl = 20;
		 for(int i = 0; i < N; i++)
			 for(int v = 0; v < N; v++) tabu[i][v] = -1;
		 
		 int nic = 0;
		 int maxStable = 20;
		 
		 int best = S.violations();
		 Random R = new Random();
		 int it = 0;
		 ArrayList<Move> cand = new ArrayList<Move>();
		 while(it < 10000 && S.violations() > 0){
			 int minDelta = Integer.MAX_VALUE;
			 for(int i = 0; i < N; i++){
				 for(int v = 0; v < N; v++) if(X[i].getValue() != v){
					 int delta = S.getAssignDelta(X[i], v);
					 if(tabu[i][v] <= it || 
							 delta + S.violations() < best){// aspiration criterion
						 if(delta < minDelta){
							 minDelta = delta;
							 cand.clear();
							 cand.add(new Move(i,v));
						 }else if(delta == minDelta){
							 cand.add(new Move(i,v));
						 }
					 }
				 }
			 }
			 
			 Move m = cand.get(R.nextInt(cand.size()));
			 
			 X[m.i].setValuePropagate(m.v);// local move
			 tabu[m.i][m.v] = it + tbl;// update tabu list
			 
			 if(S.violations() < best){
				 best = S.violations();
				 nic = 0;
			 }else{
				 nic++;
				 if(nic >=  maxStable){
					 //restart(); random
					 for(int i = 0; i < N; i++) X[i].setValuePropagate(R.nextInt(N));
				 }
			 }
			 System.out.println("Step " + it + " S = " + S.violations());
			 it++;
		 }
		
		 
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QueenCBLS app = new QueenCBLS();
		app.solve();
	}

}
