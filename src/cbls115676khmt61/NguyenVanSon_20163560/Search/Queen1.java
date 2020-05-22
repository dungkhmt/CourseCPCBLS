package cbls115676khmt61.NguyenVanSon_20163560.Search;

import java.util.ArrayList;
import java.util.Random;
import javafx.util.Pair;

public class Queen1 {
	int N;
	int[] X;
	ArrayList<Pair<int a, int b>>
	
	Random R = new Random();
	
	private void init() {
		for(int i = 1; i <= N; i ++) {
			X[i] = R.nextInt(N) +1;
		}
	}
	private int violations(){
		int v = 0; 
		for(int i = 1; i<N; i++) {
			for(int j = i+1; j<=N; j++) {
				if(X[i] == X[j]) v++;
				if(X[i] + i == X[j] + j) v++;
				if(X[i] - i == X[j] -j) v++;
			}
			
			}
		return v;
		
	}
	private int evaluate(int i, int v) {
		int oldV = X[i];
		X[i] = v;
		int newviolations = violations();
		X[i] = oldV;
		return newviolations;
	}
		private void localsearch() {
			init();
			int it = 1;
			int v = violations();
			while( it <= 100000 && v > 0 ) {
				int minE = Integer.MAX_VALUE;
				int sel_i = -1; int sel_v = -1;
				
				
				for(int i = 1; i <= N; i++) {
					for(int val = 1; val <=N; val ++) {
						int e = evaluate(i, val);
						if(e < minE) {
							minE = e;
							sel_i = i; sel_v = val;
						}
					}
				}
			}
			
		}
		public void solve(int N) {
			this.N = N;
			X = new int[N+1];
			localsearch();
		}
		
		public static void main(String[] args) {
			Queen1 app = new Queen1();
			app.solve(30);
		}
		
	}


