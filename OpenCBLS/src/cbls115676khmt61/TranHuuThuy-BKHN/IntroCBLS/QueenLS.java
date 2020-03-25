package com.thuy.bachkhoa;

import java.util.ArrayList;
import java.util.Random;

class Pair {
	int i, v;
	
	Pair(int i, int v) {
		this.i = i;
		this.v = v;
	}
}

public class QueenLS {
	int N;
	int[] X;
	
	Random R = new Random();
	private void generateInitialSolution() {
		for (int i=1; i<=N; i++)
			X[i] = R.nextInt(N) + 1;
	}
	
	private int violations() {
		int v = 0;
		for (int i=1; i<=N; i++)
			for (int j=i+1; j<=N; j++) {
				if (X[i] == X[j]) v++;
				if (X[i] + i == X[j] + j) v++;
				if (X[i] - i == X[j] - j) v++;
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
		generateInitialSolution();
		int it = 1;
		int v = violations();
		ArrayList<Pair> cand = new ArrayList<Pair>();
		
		while (it <= 1000000 && v > 0) {
			int minE = Integer.MAX_VALUE;
			for (int i=1; i<=N; i++) {
				for (int val=1; val<=N; val++) {
					int e = evaluate(i,val);
					if (e < minE) {
						minE = e;
						cand.clear();
						cand.add(new Pair(i,val));
					}
					else if (e == minE)
						cand.add(new Pair(i,val));
				}
			}
			
			int idx = R.nextInt(cand.size());
			Pair p = cand.get(idx);
			
			X[p.i] = p.v;
			v = violations();
			System.out.println("Step " + it + ", new violations = " + v);
			it++;
		}
	}
	
	public void solve(int N) {
		this.N = N;
		X = new int[N+1];
		localsearch();
	}
	
	public static void main(String[] args) {
		QueenLS app = new QueenLS();
		app.solve(200);
	}
}
