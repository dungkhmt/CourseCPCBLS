package planningoptimization115657k62.levanlinh.mixedintegerprogramming;

import java.util.HashSet;

public class SubSetGenerator {
	int N;
	int [] X; // represents binary sequence
	
	public SubSetGenerator (int N) {
		this.N = N;
	}
	
	public HashSet<Integer> first() {
		X = new int[N];
		for (int i = 0; i < N; ++i) {
			X[i] = 0;
		}
		
		HashSet<Integer> S = new HashSet<>();
		
		for (int i = 0; i < N; ++i) {
			if (X[i] == 1) S.add(i);
		}
		return S;
	}
	
	public HashSet<Integer> next() {
		int j = N - 1;
		while (j >= 0 && X[j] == 1) {
			X[j] = 0;
			--j;
		}
		if (j >= 0) {
			X[j] = 1;
			HashSet<Integer> S = new HashSet<>();
			for (int i = 0; i < N; ++i) {
				if (X[i] == 1) S.add(i);
			}
			return S;
		} else {
			return null;
		}
		
	}
}