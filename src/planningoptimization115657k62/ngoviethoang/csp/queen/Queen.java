package planningoptimization115657k62.ngoviethoang.csp.queen;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Queen {
	public void queen(int n) {
		Model model = new Model("Queen");
		IntVar[] x = model.intVarArray("x",n, 0,n-1);
		IntVar[] bslash = new IntVar[n];
		IntVar[] fslash = new IntVar[n];
		for (int i = 0; i < n; i++) {
			bslash[i] = model.intOffsetView(x[i], i);
			fslash[i] = model.intOffsetView(x[i], -i);
		} 
		
		model.allDifferent(x).post();
		model.allDifferent(bslash).post();
		model.allDifferent(fslash).post();
		
		model.getSolver().solve();
		
		for (int i = 0; i < n; i++) {
			System.out.println("X["+(i+1)+"] = "+x[i].getValue());
		}
	}
	
	public static void main(String args[]) {
		Queen app = new Queen();
		app.queen(100);
	}
}
