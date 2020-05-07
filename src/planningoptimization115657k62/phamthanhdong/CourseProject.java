package planningoptimization115657k62.phamthanhdong;

//import planningoptimization115657k62.phamthanhdong.Init;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class CourseProject {

	// declare model
	Model model = new Model("Get the goods in the warehouse");
	IntVar OBJ = model.intVar("objective", 0, 99999999);

	IntVar[] path;
	int[] route;
	IntVar[] distance;
	IntVar[][] P;
	int min_result = 0;

	/* Declare global variable */
	int M = 10; // number of shelves
	int N = 5; // number of products
	int[][] Q; // matrix Q[i][j] is number of product ith in shelf j
	int[][] d; // d[i][j] distance from point i to j
	int q[]; // q[i] is number of product ith employee needs
	int max_units[];
	int[] oneP;
	int rows = M; // the times, because the employee at most visit M shelves
	int columns = M; // the number of shelve

	/* load data from file */
	public void creat() throws Exception {

		String filePath = new File("").getAbsolutePath();
		// read file Q(i,j)
		Scanner sc = new Scanner(new BufferedReader(
				new FileReader(filePath + "/src/planningoptimization115657k62/phamthanhdong/Q.txt")));
		while (sc.hasNextLine()) {
			Q = new int[N + 1][M];
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
		q = new int[N + 1];
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

		for (int i = 0; i < max_units.length; i++)
			System.out.print(max_units[i] + " ");

		System.out.println();

		for (int i = 0; i < q.length; i++)
			System.out.print(q[i] + " ");

		System.out.println();

	}

	public void getMaxUnits() {
		max_units = new int[N];
		for (int k = 0; k < N; k++)
			for (int i = 0; i < M; i++)
				max_units[k] += Q[k][i];

	}

	public void showInfor() {
		System.out.println("Max unit all shelves have:");
		for (int i = 0; i < max_units.length; i++)
			System.out.print(max_units[i] + " ");

		System.out.println();
		System.out.println("The employee need");
		for (int i = 0; i < q.length; i++)
			System.out.print(q[i] + " ");

		System.out.println();
		System.out.println();
	}

	/* make constraint */
	public void creatConstraint() {
		System.gc();

		// make constraint the value of path[i] must be in range[0:M]
		path = new IntVar[rows + 2];
		System.out.println("Length of path:" + path.length);
		for (int i = 0; i < path.length; i++)
			path[i] = model.intVar("path[" + i + "]", 0, M);

		model.arithm(path[0], "=", 0).post(); // the start point
		model.arithm(path[1], "!=", 0).post();
		model.arithm(path[path.length - 1], "=", 0).post(); // the end point

		// make the constraint one shelf must be visited at most one time(EXCEPT 0th
		// point)
		for (int i = 0; i < path.length; i++) {
			for (int j = 0; j < path.length; j++) {
				if (i != j) { // with 2 distinct times
					model.ifThen(model.arithm(path[i], ">", 0), // and greater than 0
							model.arithm(path[i], "!=", path[j])); // must be distinct
				}
			}

		}

		// make constraint to ensure the point 0 in path when the employê
		// visited all need shelves
		for (int i = 2; i < path.length; i++) {
			model.ifThen(model.arithm(path[i], "!=", 0), model.arithm(path[i - 1], "!=", 0));
		}

		// make constraint unit of product
		P = new IntVar[N][path.length];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < path.length; j++) {
				P[i][j] = model.intVar("P[" + i + "," + j + "]", 0, max_units[i]);
			}
		}

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < path.length; j++) {
				model.ifThen(model.arithm(P[i][j], ">=", 0), model.arithm(P[i][j], "=", Q[i][path[j].getValue()]));

			}

			model.sum(P[i], ">=", q[i]).post();

		}

		// make the constraint distance[i]
		distance = new IntVar[path.length - 1]; // add 2 times the start and end point
		for (int i = 0; i < distance.length; i++)
			distance[i] = model.intVar("distance[" + i + "]", 0, 9999999);

		for (int i = 0; i < distance.length; i++)
			model.arithm(distance[i], "=", d[path[i].getValue()][path[i + 1].getValue()]).post();

	}

	/* Solve problem */
	public void Solve() {

		route = new int[rows + 2];// creat array to display route
		for (int i = 0; i < rows + 2; i++)// create
			route[i] = 0;

		Solver solver = model.getSolver();

		int[] scalar_dis = new int[distance.length];
		for (int i = 0; i < scalar_dis.length; i++)
			scalar_dis[i] = 1;

		model.scalar(distance, scalar_dis, "=", OBJ).post();
		model.setObjective(Model.MINIMIZE, OBJ);

		while (solver.solve()) {
			System.out.print("Path: ");
			for (int i = 0; i < path.length; i++) {
				System.out.print(path[i].getValue() + " ");
				route[i] = (int) path[i].getValue();// attributed to

			}

			System.out.println("\n");

			for (int i = 0; i < path.length - 1; i++) {
				min_result += d[path[i].getValue()][path[i + 1].getValue()];
			}
			System.out.println();
			System.out.println("cost_min:" + min_result);

		}

//		for (int i = 0; i < rows + 2; i++) {
//			if ((route[i + 1] == (int) 0) && (route[i + 2] != (int) 0)) {
//				System.out.print(route[i] + " ");
//			}
//		}

		System.out.println();
		System.out.println("                            ---------- Group 7 -------------              ");
		System.out.println("                         Author: Dao Hao Nam - Pham Thanh Dong            ");

	}

	public static void main(String args[]) {

		Init Init = new Init();
		try {
			Init.Gen();
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		CourseProject courseProject = new CourseProject();
		try {
			courseProject.creat();
		} catch (Exception e) {
			e.printStackTrace();
		}

		courseProject.getMaxUnits();
		courseProject.showInfor();
		// courseProject.test();
		try {

			courseProject.creatConstraint();
			courseProject.Solve();
		} catch (OutOfMemoryError oome) {
			System.out.println("oh");
			Runtime.getRuntime().gc();
		}
		Runtime.getRuntime().gc();
	}

}
