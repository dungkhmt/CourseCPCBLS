package miniproject18;

import java.util.Scanner;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class Phuongphaportool {
	// input data structures
	static {
		System.loadLibrary("jniortools");
	}
	long start;
	long end;
	public int N, D, K;
	// N là nhan vien, D la so ngay lam, K là kíp trong ngày
	public int alpha, beta;
	public MPVariable[][][] X;
	public MPVariable[][] load;

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

	public void solve() {

		MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		X = new MPVariable[N + 5][D + 5][K + 5];
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= D; j++) {
				for (int k = 1; k <= K; k++) {
					X[i][j][k] = solver.makeIntVar(0, 1, "X[" + i + "][" + j + "][" + k + "]");
				}
			}
		}
		// nhan vien lam nhieu nhat 1 ca trong ngay
		for (int i = 1; i <= N; i++)
			for (int j = 1; j <= D; j++) {
				MPConstraint c = solver.makeConstraint(0, 1);
				for (int k = 1; k <= K; k++) {
				
					c.setCoefficient(X[i][j][k], 1);
				}
			}
		// ngay hom truoc lam ca dem thi hom sau duoc nghỉ
		for (int i = 1; i <= N; i++)
			for (int j = 1; j < D; j++) {
			//	if (X[i][j][4].solutionValue() == 1) {
					MPConstraint c = solver.makeConstraint(0, 1);
					for (int k = 1; k <= 4; k++) {
						c.setCoefficient(X[i][j + 1][k], 1);
					}
					c.setCoefficient(X[i][j][4], 1);
			//	}

			}

		// moi ngay trong moi ngay co it anpha nhan vien va nhieu nhat bata nhan vien
//		load = new MPVariable[D][K];// bieu thi so nguoi trong ngay cua buoi


		for (int j = 1; j <= D; j++)
			for (int k = 1; k <= K; k++) {
				MPConstraint c = solver.makeConstraint(alpha, beta);
				for (int i = 1; i <= N; i++) {
					c.setCoefficient(X[i][j][k], 1);
					// load[j][k] =load[j][k] + X[i][j][k]; // lam sao de xac định hàm tính tổng của
					// số nhân viên như này
				}
//				c.setCoefficient(load[j][k], 1);
			}
		MPVariable y = solver.makeIntVar(0, D, "y");
		// so ca dem lon nhat ma 1 n/v được phân
		for (int i = 1; i <= N; i++) {
			MPConstraint c = solver.makeConstraint(0, D);
			for (int j = 1; j <= D; j++) {
				c.setCoefficient(X[i][j][4], -1);
				// y = X[i][j][4] làm sao để xác định tổng số nhân viên ca tối
				// X[1][D][4] = 3
			}
			c.setCoefficient(y, 1);
		}

		MPObjective obj = solver.objective();
		obj.setCoefficient(y, 1);
		obj.setMinimization();
		ResultStatus rs = solver.solve();
		if (rs != ResultStatus.OPTIMAL) {
			System.out.println("cannot find optimal solution");
		} else {
			System.out.println("obj= " + obj.value());
			for (int i = 1; i <= N; i++) {
				System.out.println("staff " + i + ":");
				double tong = 0;
				for (int j = 1; j <= D; j++) {
		//			System.out.print(X[i][j][4].solutionValue() + " ");
		//			System.out.println(X[i][j][1].solutionValue() + " "+X[i][j][2].solutionValue()+" "+X[i][j][3].solutionValue()+" ");
					tong += X[i][j][4].solutionValue();
				}
				System.out.println("btoi = " + tong); // can toi uu ham so buoi toi
			}
		}

	}

	public static void main(String[] arg) {
		// TODO Auto-generated method stub
		Phuongphaportool app = new Phuongphaportool();
		app.input();
		System.out.println("waiting: ");
		app.start  = System.currentTimeMillis();
		app.solve();
		app.end = System.currentTimeMillis();
		System.out.println("time: "+ (app.end-app.start) );
//    100 300
//    30 
//    40

	}

}
