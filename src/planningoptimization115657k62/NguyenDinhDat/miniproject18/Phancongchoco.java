package miniproject18;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import com.google.ortools.linearsolver.MPSolver.ResultStatus;

import localsearch.functions.basic.FuncMult;
import localsearch.functions.sum.SumFun;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;

import java.util.ArrayList;
import java.util.Scanner;

public class Phancongchoco {
	long start;
	long end;
	int N, D, K;
	// N là nhan vien, D la so ngay lam, K là kíp trong ngày
	public int alpha, beta;

	public void input() {
		Scanner sr = new Scanner(System.in);
		System.out.println("nhap du lieu: ");
		N = sr.nextInt();
		D = sr.nextInt();
		K = 4;

		alpha = sr.nextInt();
		beta = sr.nextInt();
		sr.close();
	}

	public void choco_solve() {
		Model model = new Model("Liquid");
		IntVar[][][] X = new IntVar[N + 5][D + 5][K + 5];
		IntVar y;

		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= D; j++) {
				for (int k = 1; k <= K; k++) {
					X[i][j][k] = model.intVar("X[" + i + "," + j + "][" + k + "]", 0, 1);
				}
			}
		}
		// C1 nhan vien lam nhieu nhat 1 ca trong ngay
		int[] ones = new int[K];
		for (int i = 0; i < K; i++) {
			ones[i] = 1;
		}
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= D; j++) {
				IntVar t[] = new IntVar[K];
				for (int k = 0; k < K; k++) {
					t[k] = X[i][j][k + 1];
				}
				model.scalar(t, ones, "<=", 1).post();
			}
		}
		// C2 ngay hom truoc lam ca dem thi hom sau duoc nghi

		for (int i = 1; i <= N; i++) {
			for (int j = 1; j < D; j++) {
				for (int k = 1; k <= K; k++)
					model.ifThen(model.arithm(X[i][j][4], "=", 1), model.arithm(X[i][j + 1][k], "=", 0));

			}
		}
//c3 moi ngay trong moi ngay co it anpha nhan vien va nhieu nhat beta nhan vien
		int[] ones2 = new int[N];
		for (int i = 0; i < N; i++) {
			ones2[i] = 1;
		}

		for (int j = 1; j <= D; j++)
			for (int k = 1; k <= K; k++) {
				IntVar[] tem = new IntVar[N];
				for (int i = 0; i < N; i++) {
					tem[i] = X[i + 1][j][k];
				}
				model.scalar(tem, ones2, ">=", alpha).post();
				model.scalar(tem, ones2, "<=", beta).post();
			}
		// so ca dem lon nhat ma 1 n/v duoc phan
		y = model.intVar("y", 0, D);

		int[] ones3 = new int[D];
		for (int i = 0; i < D; i++) {
			ones3[i] = 1;
		}
		for (int i = 1; i <= N; i++) {
			IntVar[] tp = new IntVar[D];
			for (int j = 0; j < D; j++)
				tp[j] = X[i][j + 1][4];
			model.scalar(tp, ones3, "<=", y).post();
		}

		model.setObjective(Model.MINIMIZE, y);
		Solver solver = model.getSolver();
		
		if (!solver.solve()) {
			System.out.println("cannot find optimal solution");
		}
		
		while (solver.solve()) {
			for (int i = 1; i <= N; i++) {
				System.out.println("staff" + i + ":");
				int tong = 0;
				for (int j = 1; j <= D; j++) {
					System.out.print(X[i][j][4].getValue() + " ");
				//	System.out.println(
				//			X[i][j][1].getValue() + " " + X[i][j][2].getValue() + " " + X[i][j][3].getValue() + " ");
					tong += X[i][j][4].getValue();
				}
				System.out.print(" Night : " + tong + " ");
				System.out.println(y);
			}
			System.out.println("-----------");
		}
		

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Phancongchoco app = new Phancongchoco();
		app.input();
		System.out.println("Waiting: ");
		app.start = System.currentTimeMillis();
		app.choco_solve();
		app.end = System.currentTimeMillis();
		System.out.println("time: " + (app.end - app.start));

	}

}
