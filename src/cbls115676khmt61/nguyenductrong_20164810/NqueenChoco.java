package cbls115676khmt61.nguyenductrong_20164810;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

public class NqueenChoco {

	public static void main(String[] args) {
		int n = 8;
		Model model = new Model(n + "-queens problem");

		IntVar[] vars = new IntVar[n];

		for (int q = 0; q < n; q++) {
			vars[q] = model.intVar("Q_" + q, 1, n);
		}
		/*
		for (int i = 0; i < n - 1; i++) {
			for (int j = i + 1; j < n; j++) {
				model.arithm(vars[i], "!=", vars[j]).post();
				model.arithm(vars[i], "!=", vars[j], "-", j - i).post();
				model.arithm(vars[i], "!=", vars[j], "+", j - i).post();
			}
		}
		*/
		
		model.allDifferent(vars).post();
		
		IntVar[] dia1 = new IntVar[n];
		IntVar[] dia2 = new IntVar[n];
		
		for(int i=0;i<n;i++) dia1[i] = model.intVar("dia1_" + i, 0, 2*n);
		for(int i=0;i<n;i++) dia2[i] = model.intVar("dia2_" + i, -n, n);
		for(int i=0;i<n;i++) {
			model.arithm(dia1[i],"=",vars[i],"+",i).post();
			model.arithm(dia2[i],"=",vars[i],"-",i).post();
		}
		
		model.allDifferent(dia1).post();
		model.allDifferent(dia2).post();
		
		Solution solution = model.getSolver().findSolution();
		if (solution != null) {
			System.out.println(solution.toString());
		}
	}
}
