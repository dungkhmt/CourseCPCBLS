package practice;

public class NQueen_BnB {
	
	private int N;
	private boolean[] r;
	private boolean[] c;
	private boolean[] a;
	private boolean[] b;
	private int[] res;
	private boolean found;
	
	private NQueen_BnB(int n) {
		this.N = n;
	}
	
	private boolean check(int k, int i) {
		if (r[k]) {
			return false;
		}
		if (c[i]) {
			return false;
		}
		if (a[k + i]) {
			return false;
		}
		if (b[k - i + N]) {
			return false;
		}
		return true;
	}
	
	private void flag(int k, int i) {
		r[k] = true;
		c[i] = true;
		a[k + i] = true;
		b[k - i + N] = true;
	}
	
	private void unflag(int k, int i) {
		r[k] = false;
		c[i] = false;
		a[k + i] = false;
		b[k - i + N] = false;
	}
	
	private void TRY(int k) {
		if (found) {
			return;
		}
		if (k == N) {
			found = true;
			return;
		} else {
			for (int i = 0; i < N; i++) {
				if (check(k, i)) {
					res[k] = i;
					flag(k, i);
					TRY(k + 1);
					if (found) {
						return;
					}
					unflag(k, i);
				}
			}
		}
	}
	
	private void solve() {
		r = new boolean[N];
		c = new boolean[N];
		a = new boolean[2 * N];
		b = new boolean[2 * N];
		res = new int[N];
		for (int i = 0; i < N; i++) {
			r[i] = false;
			c[i] = false;
			a[i] = false;
			a[i + N] = false;
			b[i] = false;
			b[i + N] = false;
		}
		found = false;
		TRY(0);
		if (!found) {
			System.out.print("not found!");
			return;
		}
		for (int i = 0; i < N; i++) {
			System.out.print(res[i] + " ");
		}
	}
	
	public static void main(String args[]) {
		NQueen_BnB solver = new NQueen_BnB(10);
		solver.solve();
	}
}
