package cbls115676khmt61.HuyLQ_20161813.CP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class BACP {
	int N, P, pre[][], a, b, c, d, credits[];
	Model  model;
	IntVar[] X, numberCoursePreiod, numberCreditsPeriod;
	
	public void stateModel() {
		X = new IntVar[N];
		numberCoursePreiod = new IntVar[P];
		numberCreditsPeriod = new IntVar[P];
		IntVar[][] temp; //temp[i][j]: mon j co xuat hien trong ki i hay khong
		temp = new IntVar[P][N];
		for (int i = 0; i < N; i++) {
			X[i] = model.intVar("X[" + i + "]", 0, P-1);
		}
		
		//dieu kien tien quyet
		for (int i = 0; i < pre.length; i++) {
			model.arithm(X[pre[i][0]], "<", X[pre[i][1]]).post();
		}
		
		//dieu kien conditional sum
		for (int i = 0; i < P; i++) {
			for (int j = 0; j < N; j++) {
				temp[i][j] = model.intVar("temp[" + i + "][" + j + "]", 0, 1);
				model.ifThen(model.arithm(X[j], "=", i), model.arithm(temp[i][j], "=", 1));
				model.ifThen(model.arithm(X[j], "!=", i), model.arithm(temp[i][j], "=", 0));
			}
		}
		int[] coeff = new int[N];
		for (int i = 0; i < N; i++) {
			coeff[i] = 1;
		}
		int max_credit = Integer.MIN_VALUE;
		for (int i = 0; i < N; i++) {
			if (max_credit < credits[i]) max_credit = credits[i];
		}
		//dieu kien so mon hoc va so tin chi
		for (int i = 0; i < P; i++) {
			numberCoursePreiod[i] = model.intVar("number Courses Period " + i, 0, N);
			numberCreditsPeriod[i] = model.intVar("number Credits Period " + i, 0, N * max_credit);
			model.scalar(temp[i], coeff, "=", numberCoursePreiod[i]).post();
			model.scalar(temp[i], credits, "=", numberCreditsPeriod[i]).post();
			model.arithm(numberCoursePreiod[i], ">=", a).post();
			model.arithm(numberCoursePreiod[i], "<=", b).post();
			model.arithm(numberCreditsPeriod[i], ">=", c).post();
			model.arithm(numberCreditsPeriod[i], "<=", d).post();
		}
	}
	
	public void solve() {
		Solver solver = model.getSolver();
		if (solver.solve()) {
			for (int i = 0; i < P; i++) {
				System.out.print("Ki " + i + ": ");
				for (int j = 0; j < N; j++) {
					if (X[j].getValue() == i) {
						System.out.print(j + " ");
					}
				}
				System.out.println("number courses = " + numberCoursePreiod[i].getValue() + ", number credits = " + numberCreditsPeriod[i].getValue());
			}
		}
		else {
			System.out.println("No solution");
		}
	}
	
	public void printResult() {
		
	}
	
	@SuppressWarnings("resource")
	public void readFile(String name) throws FileNotFoundException {
		//dong 1: so mon hoc N, so hoc ki P, cac bien a, b, c, d 
		//		  gioi han a < so mon moi ki < b, gioi han c < so tin chi moi ki < d
		//dong 2: so tin chi moi mon hoc
		//dong 3: so dieu kien tien quyet
		//dong 4 -> : mang tien quyet hoc phan: X[j][0] < X[j][1] 
		model = new Model();
		Scanner scanner = new Scanner(new File(name));
		N = scanner.nextInt();
		P = scanner.nextInt();
		a = scanner.nextInt();
		b = scanner.nextInt();
		c = scanner.nextInt();
		d = scanner.nextInt();
		
		credits = new int[N];
		for (int i = 0; i < N; i++) {
			credits[i] = scanner.nextInt();
		}
		
		int prelength = scanner.nextInt();
		pre = new int[prelength][2];
		for (int i = 0; i < prelength; i++) {
			pre[i][0] = scanner.nextInt();
			pre[i][1] = scanner.nextInt();
		}
		
		
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		BACP bacp = new BACP();
		bacp.readFile("D:/Document/Java/Test-CBLS/File/bacp-12.txt");
		bacp.stateModel();
		bacp.solve();
	}
}
