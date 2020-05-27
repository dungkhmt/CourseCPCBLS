package cbls115676khmt61.NamThangNguyen_20163848;

public class Queen {
	int N;
	int[] X;
	
	private void solution() {
		for (int i=1; i<=N; i++)
			System.out.print(X[i] + " ");
		System.out.println();
	}
	
	private boolean check(int v, int k) {
		for (int i=1; i<k; i++) {
			if (X[i] == v) return false;
			if (X[i] + i == v + k) return false;
			if (X[i] - i == v - k) return false;
		}
		return true;
	}
	
	private void TRY(int k) {
		for (int v=1; v<=N; v++)
			if (check(v,k)) {
				X[k] = v;
				if (k==N) solution();
				else TRY(k+1);
			}
	}
	
	public void solve(int N) {
		this.N = N;
		X = new int[N+1];
		TRY(1);
	}
	
	public static void main(String[] args) {
		Queen app = new Queen();
		app.solve(8);
	}
}
