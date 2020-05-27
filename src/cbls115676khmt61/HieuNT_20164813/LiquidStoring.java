import java.util.ArrayList;

import cbls115676khmt61.HieuNT_20164813.search.AssignMove;
import cbls115676khmt61.HieuNT_20164813.search.TabuSearch;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.basic.OR;
import localsearch.model.IFunction;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;


public class LiquidStoring {
	LocalSearchManager mgr;
	ConstraintSystem CS;
	VarIntLS X[];
	int N = 5;
	int M = 20;
	int[] V = { 60, 70, 80, 90, 100 };
	int[] W = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10 };
	private ArrayList<ArrayList<Integer>> conflicts;
	// 0, 1
	// 7, 8
	// 12, 17
	// 8, 9
	// 1, 2, 9
	// 0, 9, 12
	
	

	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[M];
		for (int i = 0; i < M; i++) {
			X[i] = new VarIntLS(mgr, 0, N);
		}
		
		conflicts = new ArrayList<ArrayList<Integer> >(6);
		ArrayList<Integer> tmp0 = new ArrayList<Integer>();
		tmp0.add(0);
		tmp0.add(1);
		conflicts.add(tmp0);
		ArrayList<Integer> tmp1 = new ArrayList<Integer>();
		tmp1.add(7);
		tmp1.add(8);
		conflicts.add(tmp1);
		ArrayList<Integer> tmp2 = new ArrayList<Integer>();
		tmp2.add(12);
		tmp2.add(17);
		conflicts.add(tmp2);
		ArrayList<Integer> tmp3 = new ArrayList<Integer>();
		tmp3.add(8);
		tmp3.add(9);
		conflicts.add(tmp3);
		ArrayList<Integer> tmp4 = new ArrayList<Integer>();
		tmp4.add(1);
		tmp4.add(2);
		tmp4.add(9);
		conflicts.add(tmp4);
		ArrayList<Integer> tmp5 = new ArrayList<Integer>();
		tmp5.add(0);
		tmp5.add(9);
		tmp5.add(12);
		conflicts.add(tmp5);

		for (int i = 0; i < conflicts.size(); i++) {
      IConstraint[] c = new IConstraint[conflicts.get(i).size()-1];
      for (int j = 1; j < conflicts.get(i).size(); j++) {
          c[j-1] = new NotEqual(X[conflicts.get(i).get(j)], X[conflicts.get(i).get(j-1)]);
      }
      CS.post(new OR(c));
  }

		
		CS = new ConstraintSystem(mgr);
		for (int j = 0; j < N; j++) {
			IFunction f = new ConditionalSum(X, W, j);
			CS.post(new LessOrEqual(f, V[j]));
		}
		mgr.close();
	}
	
	public void printConflicts() {
		System.out.println(conflicts.size());
		for(int i = 0; i < conflicts.size(); i ++) {
			for(int j = 0; j < conflicts.get(i).size(); j ++) {
				System.out.print(conflicts.get(i).get(j) + " ");
			}
			System.out.println("");
		}
	}
	
	void printResults() {
    for (int i = 0; i < M; i++)
        System.out.printf("%d ", X[i].getValue());
    System.out.println();
    for (int i = 0; i < N; i++) {
        System.out.printf("Bin %d:", i);
        for (int j = 0; j < M; j++) {
            if ( X[j].getValue() == i ) 
                System.out.printf("%d ", j);
        }
        System.out.println();
    }
}

	public static void main(String[] args) {
		LiquidStoring app = new LiquidStoring();
		int seed = 8;
    int max_iter = 1000;
    int tabu_size = 100;
    int max_stable = 10;
    app.stateModel();
    

    // HillClimbingSearch searcher1 = new HillClimbingSearch(max_iter, seed);
    // searcher1.satisfy_constraint(prob.S);
    
    TabuSearch searcher2 = new TabuSearch(tabu_size, max_stable, max_iter, seed);
    searcher2.satisfy_constraint(app.CS, new AssignMove());
    // prob.verify();

    app.printResults();
	}
}
