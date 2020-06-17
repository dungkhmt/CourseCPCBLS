package planningoptimization115657k62.levanlinh.Exercise;

import java.util.ArrayList;
import java.util.Random;

import org.chocosolver.solver.variables.IntVar;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.constraints.basic.OR;
import localsearch.constraints.multiknapsack.MultiKnapsack;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class LiquidCBLS {
	int N = 20; // so loai chat long
	int M = 5; 	// so thung 
	
	int[] v = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[] V = {60, 70, 80, 90, 100};
	
	ArrayList<int[]> conflict = new ArrayList<>();
	
	VarIntLS[] X;
	LocalSearchManager mgr;
	ConstraintSystem CS;
	
	void initialData() {
		conflict.add(new int[] {0, 1});
		conflict.add(new int[] {7, 8});
		conflict.add(new int[] {12, 17});
		conflict.add(new int[] {8, 9});
		conflict.add(new int[] {1, 2, 9});
		conflict.add(new int[] {0, 9, 12});
	}
	
	void buildModel() {
		Random R = new Random();
		mgr = new LocalSearchManager();
		CS = new ConstraintSystem(mgr);
		
		X = new VarIntLS[N];
		for (int i = 0; i < N; ++i) {
			X[i] = new VarIntLS(mgr, 0, M-1);
			X[i].setValue(R.nextInt(M));
		}
		
		/*
		 * for (int i = 0; i < M; ++i) { IFunction sum = new ConditionalSum(X, v, i);
		 * IConstraint c = new LessOrEqual(sum, V[i]); CS.post(c); }
		 */
		CS.post(new MultiKnapsack(X, v, V));
		
		for (int index = 0; index < conflict.size(); ++index) {
			int[] mem = conflict.get(index);
			int len = mem.length;
			
			IConstraint[] c = new IConstraint[len - 1];
			for (int j = 1; j < len; ++j) {
				c[j - 1] = new NotEqual(X[mem[0]], X[mem[j]]);
			}
			CS.post(new OR(c));
		}
		
		mgr.close();
	}
	
	void solve() {
		HillClimbingSearch searcher = new HillClimbingSearch(CS);
		searcher.search(100000); // max iteration = 10000
		
		if (CS.violations() == 0) {
			for (int i = 0; i < M; ++i) {
				System.out.println("Thung " + (i + 1) + ": ");
				int sum = 0;
				for (int j = 0; j < N; ++j) if (X[j].getValue() == i) {
					sum += v[j];
					System.out.print(j + "\t");
				}
				System.out.println();
				System.out.println("Dung tich = " + V[i] + ", chua " + sum + " lits");
			}
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LiquidCBLS app = new LiquidCBLS();
		app.initialData();
		app.buildModel();
		app.solve();
	}
}
