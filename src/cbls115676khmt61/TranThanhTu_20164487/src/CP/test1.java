package CP;

import java.util.List;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.objective.ParetoOptimizer;
import org.chocosolver.solver.variables.IntVar;

public class test1 {
	public static void main(String[] args) {
		Model model = new Model("test1");
		IntVar x = model.intVar("x", 0, IntVar.MAX_INT_BOUND);
		IntVar y = model.intVar("y", 0, IntVar.MAX_INT_BOUND);
		model.arithm(x, "+", model.intScaleView(y, 2), "<=", 14).post();
		model.arithm(model.intScaleView(x, 3), "-", y, ">=", 0).post();
		model.arithm(x, "-", y,"<=", 2).post();
		IntVar obj = model.intVar("objective", 0, IntVar.MAX_INT_BOUND);
		model.scalar(new IntVar[]{x, y} ,new int[]{3,4},"+", obj).post();
		model.setObjective(model.MAXIMIZE, obj);
		while(model.getSolver().solve()) {
			System.out.println("gia tri x la: "+x.getValue());
			System.out.println("gia tri y la: "+y.getValue());
			System.out.println("gia tri max la: "+obj.getValue());
			System.out.println();
		};	
		
	}
}
