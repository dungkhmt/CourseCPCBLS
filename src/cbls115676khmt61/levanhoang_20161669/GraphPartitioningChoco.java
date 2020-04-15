package cbls115676khmt61.levanhoang_20161669;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class GraphPartitioningChoco {
	public static void main(String[] args) {
		Model model = new Model("my model");
		IntVar x = model.intVar("X", 0, 5);
		IntVar y = model.intVar("Y", new int[] {2,3,8});
		
		model.arithm(x, "+", y, "<", 5).post();
		model.times(x, y, 4).post();
		
		model.getSolver().solve();
		
		System.out.println(x);
		System.out.println(y);
		
	}

}
