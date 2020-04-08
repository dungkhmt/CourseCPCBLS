package cp;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class NQueenConstraintProgramming {
	int n = 8;
	Model model = new Model(n + "-queens problem");
	IntVar[] vars = new IntVar[n];
}
