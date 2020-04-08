package choco;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BACP {
	public int N = 12;
	public int P = 4;
	public int m1 = 2, m2 = 4, t1 = 5, t2 = 7;
	public int[] credit = {2, 1, 2, 1, 3, 2, 1, 3, 2, 3, 1, 3};
	public int[] I = {1, 5, 4, 4, 3, 5, 1, 2, 4, 7, 3};
	public int[] J = {0, 8, 5, 7, 10, 11, 6, 9, 6, 10, 11};
	
	public void solver() {
		Model m = new Model();
		IntVar[][] X = new IntVar[P][N];
		int[] OneN = new int[N];
		int[] OneP = new int[P];
		
		for(int i = 0; i < N; i++)
			OneN[i] = 1;
		for(int i = 0; i < P; i++)
			OneP[i] = 1;
		
		// X[p,i] = {0, 1}
		for(int i = 0; i< P; i++)
			for(int j=0; j< N; j++)
				X[i][j] = m.intVar(0, 1);
		//m1 <= X[i].sum <= m2, t1 <= so tin chi moi thay co <= t2
		for(int p = 0; p < P; p++) {
			m.scalar(X[p], OneN, ">=", m1).post();
			m.scalar(X[p], OneN, "<=", m2).post();
			
			m.scalar(X[p], credit, ">=", t1).post();
			m.scalar(X[p], credit, "<=", t2).post();
		}
		
		//X[q,i] = 1 voi moi i = 0...N-1
		for(int i = 0; i< N; i++) {
			IntVar[] y = new IntVar[P];
			for(int j=0; j<P; j++)
				y[j] = X[j][i];
			m.scalar(y, OneP, "=", 1).post();
		}
		
		//X[q,i] = 1 => X[p,j] = 0, (i,j) thuoc L, 0 <= p <= q<= P-1
		for(int k = 0; k < I.length; k++) {
			int i = I[k];
			int j = J[k];
			for(int ki = 0; ki < P; ki++)
				for(int ki2 = 0; ki2 <= ki; ki2++)
					m.ifThen(
					m.arithm(X[ki][i], "=", 1), 	
					m.arithm(X[ki2][j], "=", 0));
		}
		
		m.getSolver().solve();
		for(int i = 1; i <= P; i++) {
			System.out.print("Hoc ki " + i + ": ");
			for(int j = 1; j <= N; j++) {
				if(X[i-1][j-1].getValue() == 1)
					System.out.print(j + " ");
			}
			System.out.println("");
		}
	}
		
	public static void main(String[] args) {
		BACP app = new BACP();
		app.solver();
		
	}

}
