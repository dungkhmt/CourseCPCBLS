package planningoptimization115657k62.daohoainam;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;


public class FirstChoco {
	public static void main(String[] args) {
		Model model = new Model("my first problem");
		IntVar[] x = new IntVar[5];
		for(int i = 0; i < 5; i++) {
			x[i] = model.intVar("x[" + i + "]", 1, 5);
		}
		
		// constrant 1
		model.arithm(model.intOffsetView(x[2], 3), "!=", x[1]).post();
		
		// constraint 2
		model.arithm(x[3], "<=", x[4]).post();
		// costraint 3
		model.arithm(model.intOffsetView(x[0], 1), "=", x[2], "+", x[3]  ).post();
		
		// constraint 4
		model.arithm(x[4], "<=" ,3).post();
		
		//constraint 5
		model.arithm(x[1], "+", x[4], "=", 7).post();
		
		// constraint 6
		model.ifThen(
				model.arithm(x[2],"=",0),
				model.arithm(x[4],"!=", 2)
				);
		
		model.getSolver().solve();
		
		for(int i = 0; i < 5; i++) {
			System.out.println(x[i]);
		}
		
		
	}
	
}
