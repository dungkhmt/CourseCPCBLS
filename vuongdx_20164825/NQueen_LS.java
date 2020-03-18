package practice;

import java.util.ArrayList;
import java.util.Random;

import javafx.util.Pair;

public class NQueen_LS {
	
	private int N;
	private int[] X;
	
	private NQueen_LS(int N) {
		this.N = N;
		X = new int[N + 1];
	}
	
	private void generateInitialSolution() {
		for (int i = 1; i <= N; i++) {
			Random r = new Random();
			X[i] = r.nextInt(N) + 1;
		}
	}
	
	private int violations() {
		int v = 0;
		for (int i = 1; i < N; i++) {
			for (int j = i+1; j <= N; j++) {
				if (X[i] == X[j]) v++;
				if (X[i] + i == X[j] + j) v++;
				if (X[i] - i == X[j] - j) v++;
			}
		}
		return v;
	}
	
	private int evaluate(int i, int v, int cur_v) {
		int old_v = X[i];
		X[i] = v;
		int new_v = violations();
		int eval = new_v - cur_v;
		X[i] = old_v;
		return eval;
	}
	
	private void localsearch() {
		generateInitialSolution();
		ArrayList<Pair<Integer, Integer>> cand = new ArrayList<Pair<Integer, Integer>>();
		int it = 1;
		int cur_v = violations();
		System.out.println("Step " + 0 + ", violations = " + cur_v);
		while (it <= 1000000 && cur_v > 0) {
			int minE = Integer.MAX_VALUE;
			int sel_i = -1;
			int sel_v = -1;
			for (int i = 1; i <= N; i++) {
				for (int v = 1; v <= N; v++) {
					int e = evaluate(i, v, cur_v);
					if (e < minE) {
						minE = e;
						cand.clear();
						cand.add(new Pair(i, v));
					} else if (e == minE) {
						cand.add(new Pair(i, v));
					}
				}
			}
			Random rand = new Random();
			int idx = rand.nextInt(cand.size());
			sel_i = cand.get(idx).getKey();
			sel_v = cand.get(idx).getValue();
			X[sel_i] = sel_v;
			cur_v = violations();
			System.out.println("Step " + it + ", violations = " + cur_v);
			it++;
		}
		for (int i = 1; i <= N; i++) {
			System.out.print(X[i] + " ");
		}
	}
	
	public static void main(String[] args) {
		NQueen_LS nQueenLSInstance = new NQueen_LS(200);
		nQueenLSInstance.localsearch();
	}
}
