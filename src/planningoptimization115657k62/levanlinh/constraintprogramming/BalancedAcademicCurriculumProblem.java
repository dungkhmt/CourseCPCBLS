package planningoptimization115657k62.levanlinh.constraintprogramming;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BalancedAcademicCurriculumProblem {

	int N = 9;// number of courses: 0,1,2,...,8
	int P = 4;// number of semesters: 0,1,2,3
	int[] credits = { 3, 2, 2, 1, 3, 3, 1, 2, 2 };
	int alpha = 2;
	int beta = 4;
	int lambda = 3;
	int gamma = 7;
	int[] I = { 0, 0, 1, 2, 3, 4, 3 };
	int[] J = { 1, 2, 3, 5, 6, 7, 8 };// prerequisites

	int[] oneN = { 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	int[] oneP = { 1, 1, 1, 1 };

	public void solve() {
		Model model = new Model("BACP");
		IntVar[][] x = model.intVarMatrix("X", P, N, new int[] {0, 1}); // x[j][i] = mon i hoc ki j
		
		for (int j = 0; j < P; j++) {
			model.scalar(x[j], credits, ">=", lambda).post();
			model.scalar(x[j], credits, "<=", gamma).post();

			model.scalar(x[j], oneN, ">=", alpha).post();
			model.scalar(x[j], oneN, "<=", beta).post();
		}

		for (int i = 0; i < N; i++) {
			IntVar[] y = new IntVar[P];
			for (int j = 0; j < P; j++)
				y[j] = x[j][i];
			model.scalar(y, oneP, "=", 1).post();// each course is assigned to
													// exactly one semester
		}

		for (int k = 0; k < I.length; k++) {
			int i = I[k];
			int j = J[k];
			for (int q = 0; q < P; q++)
				for (int p = 0; p <= q; p++)
					model.ifThen(model.arithm(x[q][i], "=", 1), model.arithm(x[p][j], "=", 0));
		}
		model.getSolver().solve();

		for (int j = 0; j < P; j++) {
			System.out.print("Semester " + (j+1) + ": ");
			int totalCredits = 0;
			int noCourses = 0;
			for (int i = 0; i < N; i++)
				if (x[j][i].getValue() == 1) {
					totalCredits += credits[i];
					noCourses += 1;
					//System.out.println (x[j][i]);
					//System.out.print("[course " + i + ", credit " + credits[i] + "] ");
					System.out.print("\t" + (i+1));
				}
			System.out.println();
			System.out.println("No. Courses: " + noCourses + ";\t\tTotal credits: " + totalCredits);
			System.out.println();
		}
	}
	
	private void importFile(String file) throws IOException {
		FileInputStream f = new FileInputStream(file);
		Scanner S = new Scanner(f, "UTF-8");
		
		N = S.nextInt();
		P = S.nextInt();
		
		oneN = new int[N];
		Arrays.fill(oneN, 1);
		oneP = new int[P];
		Arrays.fill(oneP, 1);
		
		lambda = S.nextInt();
		gamma = S.nextInt();
		
		alpha = S.nextInt();
		beta = S.nextInt();
		
		
		credits = new int[N];
		for (int i = 0; i < N; ++i) {
			credits[i] = S.nextInt();
		}
		
		int l = S.nextInt();
		I = new int[l];
		J = new int[l];
		for (int i = 0; i < l; ++i) {
			I[i] = S.nextInt() - 1;
			J[i] = S.nextInt() - 1;			
		}
		
		S.close();		
	}
	
	public static void main(String[] args) throws IOException {
		BalancedAcademicCurriculumProblem app = new BalancedAcademicCurriculumProblem();
		//app.importFile("D:/data-bacp/bacp.in01"); // duong dan file input
		app.solve();
	}
}
