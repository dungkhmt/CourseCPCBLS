package planningoptimization115657k62.phamthanhdong;

import planningoptimization115657k62.phamthanhdong.Init;
import java.util.*;
import java.io.*;
import java.lang.*;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

/*
 * oh ! I'm feed unhappy !
 * Because I think I can't do it.
 * :(
 * 
 * */

public class CourseProject2 {

	// declare model
	// "Get the goods in the warehouse";

	int min_result = 0;

	/* Declare global variable */
	int M = 15; // number of shelves
	int N = 5; // number of products
	int[][] Q; // matrix Q[i][j] is number of product ith in shelf j
	int[][] d; // d[i][j] distance from point i to j
	int q[]; // q[i] is number of product ith employee needs
	int max_units[];
	int[] units_have_optimizer;
	int[] oneP;
	int rows = M; // the times, because the employee at most visit M shelves
	int columns = M + 1; // the number of shelves
	int max_S = -1;
	int min_S = 987654321;
	
	int route = M + 2;
	LocalSearchManager mgr = new LocalSearchManager();
	VarIntLS[] path = new VarIntLS[M + 2];

	FuncPlus[] P = new FuncPlus[N];
	ConstraintSystem S = new ConstraintSystem(mgr);

	/* load data from file */
	public void creat() throws Exception {

		String filePath = new File("").getAbsolutePath();
		// read file Q(i,j)
		Scanner sc = new Scanner(new BufferedReader(
				new FileReader(filePath + "/src/planningoptimization115657k62/phamthanhdong/Q.txt")));
		while (sc.hasNextLine()) {
			Q = new int[N][M];
			for (int i = 0; i < Q.length; i++) {
				String[] line = sc.nextLine().trim().split(" ");
				for (int j = 0; j < line.length; j++) {
					Q[i][j] = Integer.parseInt(line[j]);
				}
			}
		}
		sc.close();

		// read file d(i, j)
		d = new int[M + 1][M + 1];
		Scanner sc_d = new Scanner(new BufferedReader(
				new FileReader(filePath + "/src/planningoptimization115657k62/phamthanhdong/distance.txt")));
		while (sc_d.hasNextLine()) {
			for (int i = 0; i < d.length; i++) {
				String[] line = sc_d.nextLine().trim().split(" ");
				for (int j = 0; j < line.length; j++) {
					d[i][j] = Integer.parseInt(line[j]);
				}
			}
		}
		sc_d.close();

		// read file q(k)
		q = new int[N];
		Scanner sc_q = new Scanner(new File(filePath + "/src/planningoptimization115657k62/phamthanhdong/need.txt"));
		int i = 0;
		while (sc_q.hasNextInt()) {
			q[i++] = sc_q.nextInt();
		}
		sc_q.close();
	}

	public void test() {
		for (int i = 0; i < Q.length; i++) {
			System.out.println();
			for (int j = 0; j < M; j++)
				System.out.print(Q[i][j] + " ");

		}

		System.out.println();
	}

	// max of sum product
	public void getMaxUnits() {
		max_units = new int[Q.length];
		for (int k = 0; k < Q.length; k++)
			for (int i = 0; i < M; i++)
				max_units[k] += Q[k][i];

	}

	// ok ! this is "need" and sum "product of one"
	public void showInfor() {
		System.out.println("Max unit all shelves have:");
		for (int i = 0; i < max_units.length; i++)
			System.out.print(max_units[i] + " ");

		System.out.println();
		System.out.println("The employee need");
		for (int i = 0; i < q.length; i++)
			System.out.print(q[i] + " ");

		System.out.println();
		System.out.println("----");
	}

	// ok
	public void checkNeed() {
		for (int i = 0; i < q.length; i++) {
			if (q[i] > max_units[i]) {
				System.out.println(" The need is greater than warehouse have :( ");
				return;
			}
		}
		System.out.println("Oh good! We have more than need !");
	}

	// max route of one road
	public void findMaxBound() {
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < columns; j++)
				max_S = Math.max(max_S, d[i][j]);
		}

		max_S = max_S * (M + 1);
	}

	// min route of one road
	public void findMinBound() {
		for (int i = 0; i < d.length; i++) {
			for (int j = 0; j < columns; j++) {
				if (d[i][j] != 0)
					min_S = Math.min(min_S, d[i][j]);

			}
		}

		min_S = min_S * 2;
		System.out.println("Min S:" + min_S);
		System.out.println();
	}

	/* Solve problem */

//khoi tao lo trinh can toi uu
	public void create() {
		LocalSearchManager mrg = new LocalSearchManager();

		VarIntLS[] x = new VarIntLS[M + 2];// create

		for (int i = 0; i < route; i++) {
			x[i] = new VarIntLS(mrg, 0, M);
		}

	}

	public void makeConstraint() {
		for (int i = 0; i < path.length; i++) {
			path[i] = new VarIntLS(mgr, 0, M);
		}

		path[0].setValue(0);
		path[path.length - 1].setValue(0);

		S.post(new IsEqual(path[0], 0));
		S.post(new IsEqual(path[path.length - 1], 0));

		S.post(new NotEqual(path[1], 0));

		// khong the co 2 kho giong nhau
		for (int i = 0; i < path.length; i++) {
			for (int j = 0; j < path.length; j++) {
				if (i != j) {
					S.post(new Implicate(new NotEqual(path[i], 0), new NotEqual(path[i], path[j])));
				}
			}
		}

		// tao contrain thoa man cac rang buoc khac nhau
		for (int i = 2; i < path.length; i++) {
			S.post(new Implicate(new NotEqual(path[i], 0), new NotEqual(path[i - 1], 0)));

		}

		// so luong mat hang

		for (int k = 0; k < N; k++) {
			for (int i = 0; i < path.length; i++)
				if (path[i].getValue() != 0) {
					P[k] = new FuncPlus(P[k], Q[k][i]);
				}
//			S.post(new NotEqual(path[i],0),new);
//			if(new NotEqual(path[i], 0)
//				S.post(new  )

			S.post(new LessOrEqual(q[k], P[k]));
		}
		
		

	}

	
	
	
	
	public void Solve() {

		mgr.close();

		System.out.println();
		System.out.println("----------");

		System.out.println();

		System.out.println();

		System.out.println("                            ---------- Group 7 -------------              ");

	}

	public static void main(String args[]) {

		Init Init = new Init();
		try {
			Init.Gen();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		CourseProject2 CourseProject2 = new CourseProject2();
		try {
			CourseProject2.creat();
		} catch (Exception e) {
			e.printStackTrace();
		}

		CourseProject2.findMaxBound();
		CourseProject2.findMinBound();
		CourseProject2.getMaxUnits();
		CourseProject2.showInfor();
		CourseProject2.checkNeed();
		CourseProject2.create();

		// CourseProject2.test();
//		try {
//
//			CourseProject2.Solve();
//		} catch (OutOfMemoryError oome) {
//			System.out.println("oh");
//			Runtime.getRuntime().gc();
//		}
//		Runtime.getRuntime().gc();
	}

}
