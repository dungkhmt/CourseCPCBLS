package planningoptimization115657k62.hoangthanhlam;

import java.util.Random;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class MiniProjectChoco {
	
	int N = 6;
	int K = 2;
    
    int[] _d, d;
    int[][] _t, t;
    
	Model model;
	IntVar[] X;
	IntVar[] Route;
	IntVar[] T;
	IntVar y;
	
	public MiniProjectChoco (int N, int K) {
		this.N = N;
		this.K = K;
		
		Random rd = new Random();
		int r = 9;
		
		this._d = new int[N+1];
		this._t = new int[N+1][N+1];
		for (int i = 0; i <= N; i++) {
			_d[i] = 1 + rd.nextInt(r);
			for (int j = 0; j <= N; j++) {
				if (i == j || j == 0) _t[i][j] = 0;
				else {
					_t[i][j] = 1 + rd.nextInt(r);
				}
			}
		}
		_d[0] = 0;
	}
	
	public void printDataInput() {
		System.out.println("t[][] = {");
		for (int i = 0; i <= N; i++) {
			System.out.print("\t");
			for (int j = 0; j <= N; j++) {
				System.out.print(_t[i][j] + ", ");
			}
			System.out.println();
		}
		System.out.println("}");
		
		System.out.print("d[] = {");
		for (int i = 0; i <= N; i++) {
			System.out.print(_d[i] + ", ");
		}
		System.out.println("}");
	}
	
	public void expandDataInput() {
		/*
		 * 
		 */
		t = new int[N+2*K][N+2*K];
		
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				t[i][j] = _t[i+1][j+1];
			}
		}
		
		for (int i = N; i < N+K; i++) {
			for (int j = 0; j < N; j++) {
				t[i][j] = _t[0][j+1];
			}
		}
		
		for (int i = N+K; i < N+2*K; i++) {
			for (int j = 0; j < N; j++) {
				t[i][j] = t[0][j+1];
			}
		}
		
		for (int j = N; j < N+K; j++) {
			for (int i = 0; i < N; i++) {
				t[i][j] = _t[i+1][0];
			}
		}
		
		for (int j = N+K; j < N+2*K; j++) {
			for (int i = 0; i < N; i++) {
				t[i][j] = _t[i+1][0];
			}
		}
		
		d = new int[N+2*K];
		for (int i = 0; i < N; i++) {
			d[i] = _d[i+1];
		}
		for (int i = N; i < N+2*K; i++) {
			d[i] = 0;
		}
	}
	
	public void createModel() {
		model = new Model("MiniProject");
		X = model.intVarArray(N+K, 0, N+2*K-1);
		
		Route = model.intVarArray(N+2*K, 0, K-1);
		
		int totalD = 0;
		int totalT = 0;
		for (int i = 0; i <= N; i++) {
			totalD += _d[i];
			for (int j = 0; j <= N; j++) {
				totalT += _t[i][j];
			}
		}
		int total = totalD + totalT;
		T = model.intVarArray(N+2*K, 0, total);
		
		y = model.intVar(0, total);
	}
	
	public void createConstraint() {
		model.allDifferent(X).post();
		
		for (int i = 0; i < N+K; i++) {
			model.arithm(X[i], "!=", i).post();
			 for (int j = N; j < N+K; j++) {
			 	model.arithm(X[i], "!=", j).post();
			 }
		}
		
		for (int i = 0; i < K; i++) {
			model.arithm(Route[N+i], "=", i).post();
			model.arithm(Route[N+K+i], "=", i).post();
		}
		
		for (int i = N; i < N+K; i++) {
			model.arithm(T[i], "=", 0).post();
		}
		
		for (int i = 0; i < N+K; i++) {
			for (int j = 0; j < N+2*K; j++) {
				model.ifThen(model.arithm(X[i], "=", j), model.arithm(Route[i], "=", Route[j]));
				model.ifThen(model.arithm(X[i], "=", j), 
						model.arithm(T[j], "=", model.intOffsetView(T[i], t[i][j] + d[j])));
			}
		}
		
		for (int i = 0; i < K; i++) {
			model.arithm(T[N+K+i], "<=", y).post();
		}
		
		model.setObjective(Model.MINIMIZE, y);
	}
	
	public void solve() {
		Solver s = model.getSolver();
		// Solution solution = new Solution(model);
		
		while (s.solve()) {
			// solution.record();
			
			System.out.println("Obj = " + y.getValue());
			for (int i = 0; i < K; i++) {
				System.out.print("Time " + (i+1) + ": " + T[N+K+i].getValue() + "\t");
				System.out.print("Route " + (i+1) + ": 0 ");
				int next = X[N+i].getValue();
				while (next < N) {
					System.out.print(" -> " + (next + 1));
					next = X[next].getValue();
				}
				System.out.println();
			}
			System.out.println("---------------oOo--------------");
			
		}
		/*
		System.out.println("Obj_solution = " + solution.getIntVal(y));
		for (int i = 0; i < K; i++) {
			System.out.print("Time " + (i+1) + ": " + solution.getIntVal(T[N+K+i]) + "\t");
			System.out.print("Route " + (i+1) + ": 0 -> ");
			int next = solution.getIntVal(X[N+i]);
			while (next < N) {
				System.out.print((next + 1) + " -> ");
				next = solution.getIntVal(X[next]);
			}
			System.out.println(0);
		}
		System.out.println("---------------oOo--------------");
		*/
	}

	public static void main(String[] args) {
		MiniProjectChoco mini = new MiniProjectChoco(6, 2);
		mini.printDataInput();
		mini.expandDataInput();
		mini.createModel();
		mini.createConstraint();
		mini.solve();
		System.out.println("__Group 12__");

	}

}
