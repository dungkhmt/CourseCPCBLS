package planningoptimization115657k62.NguyenLC;

import java.util.ArrayList;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.values.IntDomainMin;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.*;

public class Liquid {
		
		// input data structures
		int liquid = 20; // so chat long 0,1,...,19
		int N = 5; // so thung 0,1,2,3,4
		int limit[] = {60,70,90,80,100}; // dung tich cac thung
		int V[] = {20,15,10,20,20,25,30,15,10,10,20,25,20,10,30,40,25,35,10,10}; // the tich cac chat long
		ArrayList<ArrayList<Integer>> a = new ArrayList<ArrayList<Integer>>();
	
	public void buildModel() {
		Model model = new Model("Liquid");
		IntVar x[][] = new IntVar[N][liquid];
		
		// X[j][i] = 1 neu chat long j nam trong thung i
		
		for(int i=0; i<N; i++) {
			for(int j=0; j<liquid; j++) {
				X[j][i] = model.IntVar("X[" + j + "," + i + "]", 0, 1);
			}
		}
		
		for(int i=0; i<N; i++) {
				model.scalar(x[i], V, "<=",limit[i]).post();
			}
		
		for(int i=0; i<a.size();i++) {
			int[] b = new int[a.get(i).size()];
			for(int j=0; j<a.get(i).size();j++) {
				b[j] = 1;
			}
			for(int k=0; k<N; k++) {
				IntVar[] c = new IntVar[N];
				for(int m=0; m<a.get(i).size(); m++) {
					b[m] = x[k][a.get(i).size()];
				}
				model.scalar(b, a, "<", get[i].size());
			}
		}
		
		int[] d = new int[N];
		for(int i=0; i<N; i++) {
			d[i] = 1;
		}
		
		for(int i=0; i<liquid; i++) {
			IntVar[] n = new IntVar[N];
			for(int j=0; j<N; j++) {
				n[j] = model.intVar("n["+ j + "]", 0, 1);
				model.arithm(n[j], "=", x[j][i]).post();
			}
			model.scalar(n, d, "=", 1).post();
		}
		
		model.getSolver().solve();
		
		for (int i=0; i<N; i++) {
			System.out.println("thung " + i + ":");
			for(int j=0; j<liquid; j++) {
				if(x[j][i].getValue() == 1) {
					System.out.println(i);
				}
			System.out.println();
			}
		}
	
	public static void main(String[] args) {
		Liquid app = new Liquid();
		
		ArrayList<Integer> thung1 = new ArrayList<Integer>();

		thung1.add(0);
		thung1.add(1);
		app.a.add(thung1);
		
		ArrayList<Integer> thung2 = new ArrayList<Integer>();

		thung2.add(7);
		thung2.add(8);
		app.a.add(thung2);
		
		ArrayList<Integer> thung3 = new ArrayList<Integer>();

		thung3.add(12);
		thung3.add(17);
		app.a.add(thung3);
		
		ArrayList<Integer> thung4 = new ArrayList<Integer>();

		thung4.add(8);
		thung4.add(9);
		app.a.add(thung4);
		ArrayList<Integer> thung5 = new ArrayList<Integer>();

		thung5.add(1);
		thung5.add(2);
		thung5.add(9);
		app.a.add(thung5);
		
		ArrayList<Integer> thung6 = new ArrayList<Integer>();

		thung6.add(0);
		thung6.add(9);
		thung6.add(12);
		app.a.add(thung6);
	
		app.solver();
	}

}
