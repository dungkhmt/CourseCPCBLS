package planningoptimization115657k62.Dangminhduc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.max_min.Max;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import planningoptimization115657k62.hoangthanhlam.HillClimbing.AssignMove;

public class MiniProjectHillClimbing {
	int N = 6;
	int K = 2;
    
    int[] d;
    int[][] t;
    
    int[] _d = {0, 9, 6, 7, 6, 7, 9};
    int[][] _t = {
    		{0, 2, 8, 3, 7, 5, 3},
    		{0, 0, 3, 5, 5, 9, 1},
    		{0, 3, 0, 9, 9, 9, 7},
    		{0, 9, 7, 0, 8, 3, 2},
    		{0, 7, 8, 9, 0, 8, 2},
    		{0, 8, 4, 7, 6, 0, 3},
    		{0, 9, 3, 6, 8, 2, 0},
    };
    
    LocalSearchManager mgr;
	VarIntLS[] X;
	VarIntLS[] Route;
	VarIntLS[] T;
	IFunction obj;
	ConstraintSystem S;
	
	public MiniProjectHillClimbing () {
		
	}
	
	public MiniProjectHillClimbing (int N, int K) {
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
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		
		X = new VarIntLS[N+K];
		for (int i = 0; i < N+K; i++) {
			X[i] = new VarIntLS(mgr, 0, N+2*K-1);
		}
		
		int total = 0;
		for (int i = 0; i <= N; i++) {
			total += _d[i];
			for (int j = 0; j <= N; j++) {
				total += _t[i][j];
			}
		}
		
		Route = new VarIntLS[N+2*K];
		T = new VarIntLS[N+2*K];
		for (int i = 0; i < N; i++) {
			Route[i] = new VarIntLS(mgr, 0, K-1);
			T[i] = new VarIntLS(mgr, 0, total);
		}
		
		for (int i = 0; i < K; i++) {
			Route[N+i] = new VarIntLS(mgr, i, i);
			Route[N+K+i] = new VarIntLS(mgr, i, i);
			T[N+i] = new VarIntLS(mgr, 0, 0);
			T[N+K+i] = new VarIntLS(mgr, 0, total);
		}
	}
	
	public void createConstraint() {
		S.post(new AllDifferent(X));
		
		for (int i = 0; i < N+K; i++) {
			S.post(new NotEqual(X[i], i));
			for (int j = N; j < N+K; j++) {
				S.post(new NotEqual(X[i], j));
			}
		}
		
		for (int i = 0; i < N+K; i++) {
			for (int j = 0; j < N+2*K; j++) {
				IConstraint c1 = new IsEqual(X[i], j);
				IConstraint c2 = new IsEqual(Route[i], Route[j]);
				IConstraint c3 = new IsEqual(new FuncPlus(T[i], t[i][j]+d[j]), T[j]);
				
				S.post(new Implicate(c1, c2));
				S.post(new Implicate(c1, c3));
			}
		}
		
		obj = new Max(T);
		mgr.close();
	}
	
	class AssignMove {
		int i;
		int v;
		public AssignMove(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	
	Random R = new Random();
	
	private void explorNeighborhood(VarIntLS[] x, ArrayList<AssignMove> candidate) {
		int minDelta = Integer.MAX_VALUE;
		double minObj = minDelta;
		candidate.clear();
		for (int i = 0; i < x.length; i++){
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				if (v == x[i].getValue()) continue;
				int delta = S.getAssignDelta(x[i], v);
				double deltaObj = obj.getAssignDelta(x[i], v);
				// if (!(delta < 0 || delta == 0 && deltaObj <= 0)) continue;
				if (delta < minDelta || delta == minDelta && deltaObj < minObj) {
					candidate.clear();
					candidate.add(new AssignMove(i,v));
					minDelta = delta;
					minObj = deltaObj;
				} else if (delta == minDelta && deltaObj == minObj) {
					candidate.add(new AssignMove(i,v));
				}
			}
		}
	}
	
	private void generateInitialSolution(VarIntLS[] x) {
		for (int i = 0; i < x.length; i++) {
			int v = R.nextInt(x[i].getMaxValue() - x[i].getMinValue() + 1) + x[i].getMinValue();
			x[i].setValuePropagate(v);
		}
	}
	
	public void search(int maxIter) {
		VarIntLS[] x = S.getVariables();
		
		generateInitialSolution(x);
		
		int it = 0;
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		while (it < maxIter && S.violations() > 0) {
			explorNeighborhood(x, candidate);
			if (candidate.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			}
			
			AssignMove m = candidate.get(R.nextInt(candidate.size()));
			x[m.i].setValuePropagate(m.v);
			it++;
			System.out.println("Step " + it + ": violations = " + S.violations() + ", y = " + obj.getValue());
		}
	}
	
	public static void main (String[] args) {
		MiniProjectHillClimbing mini = new MiniProjectHillClimbing();
		mini.readData("D:\\eclipse\\workspace\\CourseCPCBLS\\src\\planningoptimization115657k62\\hoangthanhlam\\data_50_5.txt");
		mini.expandDataInput();
		mini.printDataInput();
		mini.createModel();
		mini.createConstraint();
		mini.search(200);
		System.out.println("__Group 12__");
	}
}
