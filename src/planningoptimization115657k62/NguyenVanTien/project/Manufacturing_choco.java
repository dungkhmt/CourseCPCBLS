package project;

import java.util.ArrayList;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.limits.FailCounter;
import org.chocosolver.solver.search.loop.lns.INeighborFactory;
import org.chocosolver.solver.search.loop.lns.neighbors.INeighbor;
import org.chocosolver.solver.search.loop.lns.neighbors.RandomNeighborhood;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class Manufacturing_choco {

//	int C = 2000, A = 2000, N = 5;
//	int c[] = { 1, 1, 2, 4, 5 };
//	int a[] = { 2, 3, 4, 10, 15 };
//	int f[] = { 2, 1, 1, 1, 1 };
//	int m[] = { 5, 3, 4, 2, 1 };
    long start;
	long end;

	public void solver(int N, int[] c, int C, int A, int[] a, int[] m, int[] f) {
		Model model = new Model("Project 6");

//		IntVar[] choose = model.intVarArray(N,0, 1);
		BoolVar[] choose = model.boolVarArray(N);
		// can tren cho x[i]
		ArrayList<Integer> min_u = new ArrayList<Integer>();
		for (int i = 0; i < N; i++) {
			min_u.add(Math.min((int) A / a[i], (int) C / c[i]));
		}

//		IntVar[] x = model.intVarArray(N, 0, 40000000);
		IntVar[] x = new IntVar[N];
		for (int i = 0; i < N; i++) {
			x[i] = model.intVar("x[" + i + "]", 0, min_u.get(i));
//			choose[i] = model.intVar("choose[" + i + "]", 0, 1);
		}

		for (int i = 0; i < N; i++) {
			model.ifThenElse(model.arithm(choose[i],"=",1), model.arithm(x[i], ">=", m[i]), model.arithm(x[i], "=", 0));
//			model.ifThen(model.arithm(choose[i], "=", 0), model.arithm(x[i], "=", 0));
//			model.ifThen(model.arithm(choose[i], "=", 1), model.arithm(x[i], ">=", m[i]));
			
//			model.arithm(model.intScaleView(choose[i], m[i]), "<=", x[i]).post();
//			model.arithm(model.intScaleView(choose[i], min_u.get(i)), ">=", x[i]).post();
		}
		
//khac biet:
		/*
		 * boolvar vs intvar
		 * ifthenelse vs ifthen vs intscaleview
		 * tu do va trong model //new intVar[]
		 * mien khai bao obj
		 * if (choose[i]) vs model.arithm
		 */
		
		// rang buoc voi dien tich A
		model.scalar(x, a, "<=", A).post();
//2733032
		// rang buoc voi chi phi C
		model.scalar(x, c, "<=", C).post();

		// ham muc tieu
		IntVar OBJ = model.intVar("objective", 0, IntVar.MAX_INT_BOUND);
		model.scalar(x, f, "=", OBJ).post();

		model.setObjective(Model.MAXIMIZE, OBJ);
		int tong = 0;
		while(model.getSolver().solve()) {
			for (int i = 0; i < N; i++) {
				System.out
						.println("x[" + i + "] = " + x[i].getValue() + " choose[" + i + "] = " + choose[i].getValue());
			}
			System.out.println(OBJ.getValue());
			end = System.currentTimeMillis();
			System.out.println("time: "+ (end-start) );
//			break;
		}
		
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Manufacturing_choco app = new Manufacturing_choco();
		read_data.N = 50;
		read_data.a = new int[read_data.N];
		read_data.c = new int[read_data.N];
		read_data.m = new int[read_data.N];
		read_data.f = new int[read_data.N];
		read_data.readFile(read_data.N);
		app.start  = System.currentTimeMillis();
        app.solver(read_data.N, read_data.c, read_data.C, read_data.A, read_data.a, read_data.m, read_data.f);
	}
//2785754
}
