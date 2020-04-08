package nqueen;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Nqueen {
	public void solve(int n) {
		Model m = new Model();
		IntVar[] x = m.intVarArray(n, 0, n-1);
		IntVar[] c1 = new IntVar[n];
		IntVar[] c2 = new IntVar[n];
		
		for(int i=0; i<n; i++) {
			c1[i] = m.intOffsetView(x[i], i);
			c2[i] = m.intOffsetView(x[i], -i);
		}
		
		m.allDifferent(x).post();
		m.allDifferent(c1).post();
		m.allDifferent(c2).post();
		
		m.getSolver().solve();
		for(int i=0; i<n; i++)
			System.out.println("X[" + i + "] = " + x[i].getValue());
		
	}
	public static void main(String[] args) {
		Nqueen model = new Nqueen();
		model.solve(10);
	}
}
