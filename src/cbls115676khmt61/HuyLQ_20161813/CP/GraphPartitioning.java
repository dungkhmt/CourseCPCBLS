package cbls115676khmt61.HuyLQ_20161813.CP;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class GraphPartitioning {
	int n;
	int c[][];
	Model model = new Model("graph partitioning"); 
	IntVar[] vars;
	IntVar cost;
	IntVar weight[][];
	public GraphPartitioning(int n, int[][] c) {
		this.n = n;
		this.c = c;
		vars = new IntVar[n];
		for (int i = 0; i < n; i++) {
			vars[i] = model.intVar("V[" + i + "]" , 0, 1);
		}
		cost = model.intVar("Cost", 0, 1000);
	}
	@SuppressWarnings("static-access")
	public void stateModel() {
		weight= new IntVar[n][n];
		int[] coef = new int[n]; 
		for (int i = 0; i < n; i++) {
			coef[i] = 1;
			for (int j = 0; j < n; j++) {
				weight[i][j] = model.intVar("Weight[" + i + "][" + j + "]", 0, 1000);
				model.ifThen(model.arithm(vars[i], "=", vars[j]), model.arithm(weight[i][j], "=", c[i][j]));
				model.ifThen(model.arithm(vars[i], "!=", vars[j]), model.arithm(weight[i][j], "=", 0));
			}
		}
		IntVar sum1[] = new IntVar[n];
		IntVar sumvars = model.intVar("sumvars", 0, n);
		for (int i = 0; i < n; i++) {
			sum1[i] = model.intVar("sum1", 0, 10000);
			model.scalar(weight[i], coef, "+", sum1[i]).post();;
			
		}
		model.scalar(sum1, coef, "+", cost).post();
		model.scalar(vars, coef, "+", sumvars).post();
		model.arithm(sumvars, "=", n / 2).post();
		model.setObjective(model.MINIMIZE, cost);
	}
	public void solve() {
		model.getSolver().solve();
		for (int i = 0; i < n; i++) {
			System.out.print(vars[i].getValue() + " ");
		}
		System.out.println("\n");
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				System.out.print(this.weight[i][j].getValue() + " ");
			}
			System.out.println();
		}
		System.out.println(cost.getValue() / 2);
	}
	@SuppressWarnings("resource")
	public void readfile() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("../gp-10.txt"));
		n = scanner.nextInt();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				c[i][j] = scanner.nextInt();
			}			
		}
	}
	public static void main(String[] args) throws FileNotFoundException {
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(new File("D:/Document/Java/Test-CBLS/File/gp-10.txt"));
		int n;
		n = scanner.nextInt();
		int c[][] = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				c[i][j] = scanner.nextInt();
			}			
		}
		GraphPartitioning graphPartitioning = new GraphPartitioning(n, c);
		graphPartitioning.stateModel();
		graphPartitioning.solve();
	}
}
