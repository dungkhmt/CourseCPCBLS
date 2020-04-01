package chocoex;

import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class Binpacking2D_CHOCO {
	
	public IntVar[] X, Y;
	public Model model;
	public int H, W, N;
	public int[] h, w;
	public IntVar[] o;
	
	public void readData() {
		Scanner input = new Scanner(System.in);
		N = input.nextInt();
		H = input.nextInt();
		W = input.nextInt();
		h = new int[N];
		w = new int[N];
		for(int i = 0; i < N; i++) {
			h[i] = input.nextInt();
			w[i] = input.nextInt();
		}
		input.close();
	}
	public void init() {
		model = new Model("2DBinpacking");
		X = new IntVar[N];
		Y = new IntVar[N];
		o = new IntVar[N];
		for(int i = 0; i < N; i++) {
			X[i] = model.intVar("", 0, W);
			Y[i] = model.intVar("", 0, H);
			o[i] = model.intVar("", 0, 1);
		}
	}
	public void createConstraint() {
		for(int i = 0; i < N; i++)  {
			model.ifThen(
					model.arithm(o[i], "=", 0),
					model.and(model.arithm(X[i], "<=", W - w[i]), model.arithm(Y[i], "<=", H - h[i]))
				);
			model.ifThen(
					model.arithm(o[i], "=", 1),
					model.and(model.arithm(X[i], "<=", W - h[i]), model.arithm(Y[i], "<=", H - w[i]))
				);
		}
		for(int i = 0; i < N; i++)
			for(int j = 0; j < N; j++)
				if (i != j) {
					model.ifThen(
							model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0)),
							model.or(model.arithm(X[i], "-", X[j], "<=", -w[i]), model.arithm(X[j], "-", X[i], "<=", -w[j]), model.arithm(Y[i], "-", Y[j], "<=", -h[i]), model.arithm(Y[j], "-", Y[i], "<=", -h[j]))
						);
					model.ifThen(
							model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1)),
							model.or(model.arithm(X[i], "-", X[j], "<=", -w[i]), model.arithm(X[j], "-", X[i], "<=", -h[j]), model.arithm(Y[i], "-", Y[j], "<=", -h[i]), model.arithm(Y[j], "-", Y[i], "<=", -w[j]))
						);
					model.ifThen(
							model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0)),
							model.or(model.arithm(X[i], "-", X[j], "<=", -h[i]), model.arithm(X[j], "-", X[i], "<=", -w[j]), model.arithm(Y[i], "-", Y[j], "<=", -w[i]), model.arithm(Y[j], "-", Y[i], "<=", -h[j]))
						);
					model.ifThen(
							model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1)),
							model.or(model.arithm(X[i], "-", X[j], "<=", -h[i]), model.arithm(X[j], "-", X[i], "<=", -h[j]), model.arithm(Y[i], "-", Y[j], "<=", -w[i]), model.arithm(Y[j], "-", Y[i], "<=", -w[j]))
						);
				}
		Solver solver = model.getSolver();
		if (solver.solve()) 
			for(int i = 0; i < N; i++) {
				System.out.println("Goc trai cua goi hang " + i + " o vi tri: (" + Y[i].getValue() + ", " + X[i].getValue() + ")   " + o[i].getValue());
			}
		else System.out.println("Khong co ket qua");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Binpacking2D_CHOCO app = new Binpacking2D_CHOCO();
		app.readData();
		app.init();
		app.createConstraint();
	}

}
