package chocoSolver;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class miniProject {
	public static void chocoExample() {
		Model model = new Model("example");
		IntVar[] x = new IntVar[5];
		for(int i = 0; i<5; i++) {
			x[i] = model.intVar("x" + i, 1, 5);
		}
		model.arithm(model.intOffsetView(x[2], 3), "!=", x[1]).post();
		model.arithm(x[3], "<=", x[4]).post();
		model.arithm(model.intOffsetView(x[0], 1), "=", x[2],"+", x[3]).post();
		model.arithm(x[4], "<=", 3).post();
		model.arithm(x[1], "+", x[4], "=", 7).post();
		model.ifThen(model.arithm(x[2], "=", 1), model.arithm(x[4], "!=", 2));
		
		model.getSolver().solve();
		for(int i=0; i<5; i++) {
			System.out.println("x[" + i + "] = " + x[i].getValue());
		}
	}
	public static void main(String[] args) {
		miniProject.chocoExample();
	}
}
