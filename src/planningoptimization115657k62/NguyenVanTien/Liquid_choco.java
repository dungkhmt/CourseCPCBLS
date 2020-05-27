package planningoptimization115657k62.NguyenVanTien;

import java.util.ArrayList;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Liquid_choco {

	int N = 5;
	int liquid = 20;
	int[] limit = { 60, 70, 80, 90, 100 };
	int[] V = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10 };
//	int[][] constraint;
	ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>();

	public void solver() {
		Model model = new Model("liquid");
		IntVar[][] x = new IntVar[N][liquid];// x[i][j] = 1 chat long j nam trong thung i

		for (int j = 0; j < N; j++)
			for (int i = 0; i < liquid; i++)
				x[j][i] = model.intVar("x[" + j + "," + i + "]", 0, 1);

		for (int i = 0; i < N; i++) {
			model.scalar(x[i], V, "<=", limit[i]).post();
		}

		//cac thung bi rang buoc
		for (int y = 0; y < a.size(); y++) {
			int[] one1 = new int[a.get(y).size()];
			for (int i = 0; i < a.get(y).size(); i++) {
				one1[i] = 1;
			}
			for (int k = 0; k < N; k++) {
				IntVar[] temp = new IntVar[a.get(y).size()];
				for (int i = 0; i < a.get(y).size(); i++) {
					temp[i] = x[k][a.get(y).get(i)];
				}
				model.scalar(temp, one1, "<", 3);
			}
		}

		int[] one = new int[N];
		for (int i = 0; i < N; i++) {
			one[i] = 1;
		}
		for (int i = 0; i < liquid; i++) {
			IntVar[] temp = new IntVar[N];
			for (int j = 0; j < N; j++) {
				temp[j] = model.intVar("temp[" + j + "]", 0, 1);
				model.arithm(temp[j], "=", x[j][i]).post();
			}
			model.scalar(temp, one, "=", 1).post();
		}

		model.getSolver().solve();

		for (int j = 0; j < N; j++) {
			System.out.print("thung " + j + " chua chat long: " );
			for (int i = 0; i < liquid; i++)
				if (x[j][i].getValue() == 1) {
					System.out.print(i+ " ");
//					System.out.println();
				}
			System.out.println();
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Liquid_choco app = new Liquid_choco();
		int m, i = 0;
		ArrayList<Integer> thung1 = new ArrayList<Integer>();

		thung1.add(0);
		thung1.add(1);

		app.a.add(thung1);
		
		ArrayList<Integer> thung2 = new ArrayList<Integer>();

		thung2.add(7);
		thung2.add(8);

		app.a.add(thung2);
		
		ArrayList<Integer> thung3 = new ArrayList<Integer>();

		thung3.add(12);
		thung3.add(17);

		app.a.add(thung3);
		
		ArrayList<Integer> thung4 = new ArrayList<Integer>();

		thung4.add(8);
		thung4.add(9);

		app.a.add(thung4);
		ArrayList<Integer> thung5 = new ArrayList<Integer>();

		thung5.add(1);
		thung5.add(2);
		thung5.add(9);
		
		app.a.add(thung5);
		
		ArrayList<Integer> thung6 = new ArrayList<Integer>();

		thung6.add(0);
		thung6.add(9);
		thung6.add(12);

		app.a.add(thung6);
		
		app.solver();
	}

}