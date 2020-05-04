import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
public class Example {
	public static void main(String[] args) {
		Model model = new Model("my first problem");
		IntVar x0 = model.intVar("X0", 1, 5); // x in [0,5]
		IntVar x1 = model.intVar("X1", 1, 5); // x in [0,5]
		IntVar x2 = model.intVar("X2", 1, 5); // x in [0,5]
		IntVar x3 = model.intVar("X3", 1, 5); // x in [0,5]
		IntVar x4 = model.intVar("X4", 1, 5); // x in [0,5]
		
	model.arithm(x3, "<=", x4).post();
	model.arithm(x4, "<=", 3).post();
	model.arithm(x1,"+",x4,"=",7).post();
	model.arithm(model.intOffsetView(x2, 3),"!=",x1).post();
	model.arithm(model.intOffsetView(x0, 1),"=",x2,"+",x3).post();
	model.ifThen(model.arithm(x2,"=",1),model.arithm(x4,"!=",2));
	model.getSolver().solve();
	System.out.println(x0); // Prints X = 2
	System.out.println(x1); // Prints X = 2
	System.out.println(x2); // Prints X = 2
	System.out.println(x3); // Prints X = 2
	System.out.println(x4); // Prints X = 2
	}
}
