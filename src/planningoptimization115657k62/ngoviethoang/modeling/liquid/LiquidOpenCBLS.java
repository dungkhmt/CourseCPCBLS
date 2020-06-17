package planningoptimization115657k62.ngoviethoang.modeling.liquid;

import java.util.ArrayList;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class LiquidOpenCBLS {
	
	long startTime;
	long endTime;
	
	int K = 5;
	int N = 20;
	int[] cap = new int[] {60, 70, 80, 90, 100};
	int[] vol = new int[] {20, 15, 10, 20, 20, 25, 30, 15, 10, 10,
					   20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[][] conf;
	LocalSearchManager mgr;
	VarIntLS[][] X;
	VarIntLS[] Z;
	ConstraintSystem cs;
	int[] oneN;
	int[] oneK;
	public void process( ) {
		int [][] t = new int[][] {{0,1},{7,8},{12,17},{8,9}, {1,2},{1,9},{2,9},{0,9},{0,12},{9,12}};
		conf = new int[N][N];
		for (int i = 0; i < t.length; i++) {
			conf[t[i][0]][t[i][1]] = 1;
			conf[t[i][1]][t[i][0]] = 1;
		}
		oneN = new int[N];
		for (int i = 0; i < N; i++) {
			oneN[i] = 1;
		}
		oneK = new int[K];
		for (int i = 0; i < K; i++) {
			oneK[i] = 1;
		}
	}
	public void solve() {
		process();
		

		mgr = new LocalSearchManager();
		X = new VarIntLS[K][N];
		cs = new ConstraintSystem(mgr);
		for (int k = 0; k < K; k++) {
			for (int i = 0; i < N; i++) {
				X[k][i] = new VarIntLS(mgr, 0, 1); 
			}
		}
		for (int k = 0; k < K; k++) {
			for (int i = 0; i < N; i++) {
				for (int j = i+1; j < N; j++) {
					if (conf[i][j] == 1) {
						IConstraint c = new LessOrEqual(new FuncPlus(X[k][i], X[k][j]), 1);
						cs.post(c);			
					}			
				}				
			}
		}
			
		if (true) {
			System.out.println("No solution");
		} else {
			ArrayList<Integer> a = new ArrayList<Integer>();
			for (int k = 0; k < K; k++ ) {
				a.clear();
				int sum = 0;
				for (int i = 0; i < N; i++) {
					if (X[k][i].getValue() == 1) {
						a.add(i);
						sum += vol[i];
					}
				}	
				System.out.print("Thung " + k + ": ");
				for (int i = 0; i < a.size(); i++) {
					System.out.print(a.get(i) + " ");
				}
				System.out.println("-> sum = " + sum);
			}
		}
		endTime = System.currentTimeMillis();
		System.out.print("Run time: " + (endTime - startTime)/1000.0 +  "s");
	}
					   
	public static void main(String[] args) {
		LiquidOpenCBLS app = new LiquidOpenCBLS();
		app.solve();
	}
}
