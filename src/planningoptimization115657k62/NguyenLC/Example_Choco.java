import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Example_Choco{
	public static void main(String[] args) {
		
		Model model = new Model("Ex1");
		
		IntVar[] x = model.intVarArray("x", 5, 1, 5);
		
		// 3. Post constraints
		model.arithm(model.intOffsetView(x[2],3), "!=", x[1]).post();
		model.arithm(x[3], "<=", x[4]).post();
		model.arithm(x[2], "+", x[3], "=", model.intOffsetView(x[0], 1)).post();
		model.arithm(x[4], "<=", 3).post();
		model.arithm(x[1], "+", x[4], "=", 7).post();
		model.ifThen(
				model.arithm(x[2],"=",1),
				model.arithm(x[4], "!=", 2)
				);
		// 4. Solve the problem
		model.getSolver().solve();
		// 5. Print the solution
		for (int i = 0; i < 5; i++)
		System.out.println(x[i]); // Prints X = 2
	}
}
