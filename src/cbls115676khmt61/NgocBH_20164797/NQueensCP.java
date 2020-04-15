package cbls115676khmt61.ngocbh_20164797;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class NQueensCP {
	public void modelAndSolve() {
		int n = 8;
		Model model = new Model(n+"-queens problem");
		IntVar[] vars = new IntVar[n];
		for(int q = 0; q < n; q++) {
			vars[q] = model.intVar("Q_"+q, 1, n);
			
		}
		for(int i = 0; i < n-1; i++) {
			for(int j = i + 1; j < n; j++) {
				model.arithm(vars[i], "!=", vars[j]).post();
				model.arithm(vars[i], "!=", vars[j], "-", j - i).post();
				model.arithm(vars[i], "!=", vars[j], "+", j - i).post();
			}
		}
		Solver solver = model.getSolver();
		Solution solution = solver.findSolution();
		if(solution != null) {
			System.out.println(solution.toString());
		}
	}
	
	public static void main(String[] args) {
		new NQueensCP().modelAndSolve();
	}
}
