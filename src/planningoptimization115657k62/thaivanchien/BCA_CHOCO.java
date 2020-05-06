package chocoex;

import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BCA_CHOCO {
	public int N, P, L;
	public int[] c, d1, d2; 
	public int alpha, beta, lamda, gamma;
	Model model;
	public IntVar[][] X;
	
	public void input() {
		
		Scanner inp = new Scanner(System.in);
		System.out.println("Nhap du lieu: ");
		N = inp.nextInt();
		P = inp.nextInt();
		
		lamda = inp.nextInt();
		gamma = inp.nextInt();
		alpha = inp.nextInt();
		beta = inp.nextInt();
		
		c = new int[N];
		for(int i = 0; i < N; i++)
			c[i] = inp.nextInt();
		
		L = inp.nextInt();
		d1 = new int[L];
		d2 = new int[L];
		for(int i = 0; i < L; i++) {
			d1[i] = inp.nextInt();
			d2[i] = inp.nextInt();
			
		}
		inp.close();
	}
	
	public void init() {
		model = new Model("BCA");
		X = new IntVar[P][N];
		for(int i = 0; i < P; i++)
			for(int j = 0; j < N; j++)
				X[i][j] = model.intVar("", 0, 1);
		
	}
	public void createConstraint() {
		int[] ones = new int[N];
		for(int i = 0; i < N; i++)
			ones[i] = 1;
		
		for(int i = 0; i < P; i++) {
			model.scalar(X[i], ones, ">=", alpha).post();
			model.scalar(X[i], ones, "<=", beta).post();
			model.scalar(X[i], c, ">=", lamda).post();
			model.scalar(X[i], c, "<=", gamma).post();
		}
		int[] ones2 = new int[P];
		for(int i = 0; i < P; i++)
			ones2[i] = 1;
		for(int i = 0; i < N; i++) {
			IntVar[] y = new IntVar[P];
			for(int j = 0; j < P; j++)
				y[j] = X[j][i];
			model.scalar(y, ones2, "=", 1).post();
			
		}
		for(int i = 0; i < L; i++) {
			for(int p = 0; p < P; p++)
				for(int q = p; q < P; q++)
					model.ifThen(
							model.arithm(X[q][d1[i]], "=", 1),
							model.arithm(X[p][d2[i]], "=", 0)
						);
		}
			
	}
	public void solve() {
		input();
		init();
		createConstraint();
		model.getSolver().solve();
		for(int i = 0; i < P; i++) {
			System.out.println("Hoc ki " + i + ": ");
			for(int j = 0; j < N; j++)
				if (X[i][j].getValue() == 1)
					System.out.print(j + "   ");
			System.out.println();
		}
	}
	public static void main(String args[]) {
		BCA_CHOCO app = new BCA_CHOCO();
		app.solve();
	}
}
