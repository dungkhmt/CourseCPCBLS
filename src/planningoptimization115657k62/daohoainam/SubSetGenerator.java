package planningoptimization115657k62.daohoainam;

import java.util.HashSet;

public class SubSetGenerator {
	int n;
	int[] x;
	public SubSetGenerator(int n) {
		this.n = n;
	}
	public HashSet<Integer> first() {
		x = new int[n];
		for (int i = 0; i < n; i++) {
			x[i] = 0;
		}
		HashSet<Integer> s = new HashSet<Integer>();
		return s;
	}
	public HashSet<Integer> next() {
		int j = n - 1;
		while (j >= 0 && x[j] == 1  ) {
			x[j] = 0;
			j -= 1;
		}
		if (j >= 0) {
			x[j] = 1;
			HashSet<Integer> s = new HashSet<Integer>();
			for (int i = 0; i < n; i++) {
				if (x[i] == 1) {
					s.add(i);
				}
			}
			return s;
		} else {
			return null;
		}
	}
}