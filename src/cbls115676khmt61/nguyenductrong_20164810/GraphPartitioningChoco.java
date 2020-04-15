package cbls115676khmt61.nguyenductrong_20164810;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class GraphPartitioningChoco {

	int n;
	Model model;
	int[] c;
	IntVar[] vars;
	IntVar[] Z;
	IntVar cost;

	public void readInput() throws FileNotFoundException {
		Scanner scanner = new Scanner(new File("F:\\Study\\Java workspace\\BT\\src\\data\\gp-10.txt"));
		n = scanner.nextInt();
		c = new int[n * n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				c[i * n + j] = scanner.nextInt();
			}
		}
		scanner.close();
	}

	public void stateModel() {
		model = new Model("Graph Partitioning");

		vars = new IntVar[n];
		for (int i = 0; i < n; i++)
			vars[i] = model.intVar("vars_" + i, 0, 1);

		Z = new IntVar[n * n];
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++)
				Z[i * n + j] = model.intVar("Z_" + i + "_" + j, new int[] { 0, c[i * n + j] });

		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				model.ifThen(model.arithm(vars[i], "!=", vars[j]), model.arithm(Z[i * n + j], "=", c[i * n + j]));
				model.ifThen(model.arithm(vars[i], "=", vars[j]), model.arithm(Z[i * n + j], "=", 0));
			}
		cost = model.intVar("cost", 0, 1000000);

		model.sum(vars, "=", n / 2).post();
		model.sum(Z, "=", cost).post();
		model.setObjective(Model.MINIMIZE, cost);
	}

	public void search() {
		Solver solver = model.getSolver();

		int[] x = new int[n];
		int totalCost = -1;

		while (solver.solve()) {
			for (int i = 0; i < n; i++) {
				x[i] = vars[i].getValue();
			}
			totalCost = cost.getValue();
		}

		for (int i = 0; i < n; i++)
			System.out.print(x[i] + " ");
		System.out.println("\nCost = " + totalCost / 2);
	}

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		GraphPartitioningChoco solve = new GraphPartitioningChoco();
		solve.readInput();
		solve.stateModel();
		solve.search();
	}
}
