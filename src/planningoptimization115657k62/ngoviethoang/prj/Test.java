package planningoptimization115657k62.ngoviethoang.prj;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Test {
	public void solve() {
		Model model = new Model("Test");
		IntVar x = model.intVar("x", 0, 1);
		IntVar y = model.intVar("y", 0, 1);
		IntVar f = model.intVar(0, 10);
		model.scalar(new IntVar[] {x, y}, new int[] {1,1}, "=", f).post();
		model.setObjective(false, f);
		boolean res = model.getSolver().solve();
		if (!res) {
			System.out.println("Not found");
		} else {
			System.out.println("x = " + x.getValue());
			System.out.println("y = " + y.getValue());
			System.out.println("f = " + f.getValue());
		}
	}
	
	public static void main(String[] args) {
		Test app = new Test();
		app.solve();
	}
}
