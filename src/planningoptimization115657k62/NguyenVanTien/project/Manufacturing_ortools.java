// Y tuong:   them mot bien nhi phan z_i de the hien xem san pham thu i co duoc san xuat hay khong
// Rang buoc: khi z = 0  => x = 0; khi z = 1 => x thuoc [m, min(A/a, C/c)]
// Chuyen qua tuyen tinh: goi L, U la can tren, can duoi cua x
//           U * z <= x <= L * z

package project;

import java.util.ArrayList;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class Manufacturing_ortools {
	static {
		System.loadLibrary("jniortools");
	}
	MPObjective obj;
	int C = 2000, A = 2000, N = 5;
	int c[] = { 1, 2, 4, 5, 1 };
	int a[] = { 3, 4, 10, 15, 2 };
	int f[] = { 1, 1, 1, 1, 2 };
	int m[] = { 3, 4, 2, 1, 5 };

	public void solve(int N, int[] c, int C, int A, int[] a, int[] m, int[] f) {
		MPSolver solver = new MPSolver("Project 6", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

		MPVariable[] z = new MPVariable[N]; // z = 0 neu san pham i khong duoc san xuat, z = 1 neu nguoc lai
		MPVariable[] x = new MPVariable[N]; // so luong don vi san pham cua moi san pham i

		ArrayList<Integer> min_u = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			min_u.add(Math.min((int) A / a[i], (int) C / c[i]));
		}

		// khoi tao mien gia tri cho cac bien
		for (int i = 0; i < N; i++) {
			z[i] = solver.makeIntVar(0, 1, "z[" + i + "]");
			x[i] = solver.makeIntVar(0, min_u.get(i), "x[" + i + "]"); // co the dat can tren theo chi phi C
		}

		// x[i] >= m[i] neu z[i] = 1 va x[i] = 0 neu z[i] = 0
		for (int i = 0; i < N; i++) {
			MPConstraint c0 = solver.makeConstraint(0, Math.max(min_u.get(i), 0));
			c0.setCoefficient(x[i], 1);
			c0.setCoefficient(z[i], -m[i]);
		}

		// x[i] <= min(A / a[i], C / c[i]) neu z[i] = 1 va x[i] = 0 neu z[i] = 0
		for (int i = 0; i < N; i++) {
			MPConstraint c1 = solver.makeConstraint(0, Math.max(min_u.get(i), 0));
			c1.setCoefficient(x[i], -1);
			c1.setCoefficient(z[i], min_u.get(i));
		}

		MPConstraint c2 = solver.makeConstraint(0, A);
		for (int i = 0; i < N; i++) {
			c2.setCoefficient(x[i], a[i]);
		}

		MPConstraint c3 = solver.makeConstraint(0, C);
		for (int i = 0; i < N; i++) {
			c3.setCoefficient(x[i], c[i]);
		}

		// obj func
		obj = solver.objective();
		for (int i = 0; i < N; i++) {
			obj.setCoefficient(x[i], f[i]);
		}
		obj.setMaximization();

		// solve
		MPSolver.ResultStatus rs = solver.solve();
		if (rs != MPSolver.ResultStatus.OPTIMAL) {
			System.out.println("No solutions found");
		} else {
			System.out.println("Max = " + obj.value());
			for (int i = 0; i < N; i++) {
				if (z[i].solutionValue() == 1)
					System.out.println(
							"x[" + i + "] = " + x[i].solutionValue() + " z[" + i + "] = " + z[i].solutionValue());
			}
		}
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Manufacturing_ortools app = new Manufacturing_ortools();
		read_data.N = 3;
		read_data.a = new int[read_data.N];
		read_data.c = new int[read_data.N];
		read_data.m = new int[read_data.N];
		read_data.f = new int[read_data.N];
		read_data.readFile(read_data.N);
        long start = System.currentTimeMillis();
		app.solve(read_data.N, read_data.c, read_data.C, read_data.A, read_data.a, read_data.m, read_data.f);
		long end = System.currentTimeMillis();
		long t = end - start;
		System.out.println(read_data.N + " " + t);
	}

}
