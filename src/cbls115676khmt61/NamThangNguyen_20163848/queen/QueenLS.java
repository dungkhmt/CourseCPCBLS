package queen;

import java.util.ArrayList;
import java.util.Random;

class Move{
	int i;
	int v;

	public Move(int i, int v){
		this.i = i; this.v = v;
	}
}

public class QueenLS {
	int N;
	int[] X;
	Random R = new Random();
	private void generateInitialSolution(){
		for(int i = 1; i <= N; i++)
			X[i] = R.nextInt(N)+1;
	}

	private int violations(){
		int v = 0;
		for(int i = 1; i <= N-1; i++)
			for(int j = i+1; j <= N; j++){
				if(X[i] == X[j]) v++;
				if(X[i] + i == X[j] + j) v++;
				if(X[i] -i == X[j] -j) v++;
			}
		return v;
	}

	private int evaluate(int i, int v){
		// return new violations when X[i] is assigned to v
		int oldValue = X[i];
		X[i] = v;
		int newViolations = violations();
		X[i] = oldValue;// recover
		return newViolations;
	}

	private void localSearch(){
		generateInitialSolution();
		int V = violations();
		System.out.println("init violations = " + V);
		int it = 1;
		ArrayList<Move> cand = new ArrayList<Move>();
		while(it < 10000 && V > 0){
			int minE = Integer.MAX_VALUE;
			cand.clear();
			for(int i = 1; i <= N; i++){
				for(int v = 1; v <= N; v++){
					int e = evaluate(i,v);
					if(e < minE){
						minE = e;
						cand.clear();
						cand.add(new Move(i,v));
					}else if(e == minE){
						cand.add(new Move(i,v));
					}
				}
			}
			int idx = R.nextInt(cand.size());
			Move m = cand.get(idx);
			
			X[m.i] = m.v; // local move
			V = violations();
			System.out.println("Step " + it + " violations = " + V);
			it++;			
		}
	}

	public void solve(int N){
		this.N = N;
		X = new int[N+1];
		localSearch();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		QueenLS app = new QueenLS();
		app.solve(150);
	}

}
