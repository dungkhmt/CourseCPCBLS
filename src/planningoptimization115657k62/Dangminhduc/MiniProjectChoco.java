package planningoptimization115657k62.Dangminhduc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;

import org.chocosolver.solver.Model;
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
	
	public MiniProjectChoco () {
		
	}
	
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
	
	public void readData(String filename) {
		File file = new File(filename);
		try {
			Scanner sc = new Scanner(file);
			N = sc.nextInt();
			K = sc.nextInt();
			
			_d = new int[N+1];
			_t = new int[N+1][N+1];
			for (int i = 0; i <= N; i++) {
				_d[i] = sc.nextInt();
			}
			
			for (int i = 0; i <= N; i++) {
				for (int j = 0; j <= N; j++) {
					_t[i][j] = sc.nextInt();
				}
			}
			sc.close();
		} catch (FileNotFoundException err) {
			System.out.println(err);
		}
	}
	
	public void printDataInput() {
		System.out.println("t[][] = {");
		for (int i = 0; i <= N; i++) {
			System.out.print("\t");
			for (int j = 0; j <= N; j++) {
				System.out.print(_t[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println("}");
		
		System.out.print("d[] = {");
		for (int i = 0; i <= N; i++) {
			System.out.print(_d[i] + " ");
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
		
		int total = 0;
		for (int i = 0; i <= N; i++) {
			total += _d[i];
			int _max = 0;
			for (int j = 0; j <= N; j++) {
				if (_t[i][j] > _max) _max = _t[i][j];
			}
			total += _max;
		}
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
		
		while (s.solve()) {
			System.out.println("y = " + y.getValue());
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
	}

	public static void main(String[] args) {
		MiniProjectChoco mini = new MiniProjectChoco();
		mini.readData("D:\\eclipse\\workspace\\CourseCPCBLS\\src\\planningoptimization115657k62\\hoangthanhlam\\data_50_5.txt");
		mini.printDataInput();
		mini.expandDataInput();
		mini.createModel();
		mini.createConstraint();
		mini.solve();
		System.out.println("__Group 12__");

	}

}
