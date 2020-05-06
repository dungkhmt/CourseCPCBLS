package planningoptimization115657k62.hoangthanhlam;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class MiniProjectHillClimbing {
	int N = 6;
	int K = 2;
    
    int[] _d, d;
    int[][] _t, t;
    
    LocalSearchManager mgr;
	VarIntLS[] X;
	VarIntLS[] Route;
	VarIntLS[] T;
	VarIntLS y;
	ConstraintSystem S;
	
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
				if (i == j) _t[i][j] = 0;
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
		for (int i = 0; i < N+2*K; i++) {
			Route[i] = new VarIntLS(mgr, 0, K-1);
			T[i] = new VarIntLS(mgr, 0, total);
		}
		
		y = new VarIntLS(mgr, 0, total);
	}
	
	public void createConstraint() {
		S.post(new AllDifferent(X));
		
		for (int i = 0; i < N+K; i++) {
			S.post(new NotEqual(X[i], i));
			for (int j = N; j < N+K; j++) {
				S.post(new NotEqual(X[i], j));
			}
		}
		
		for (int i = 0; i < K; i++) {
			Route[N+i].setValue(i);
			Route[N+K+i].setValue(i);
		}
		
		for (int i = 0; i < K; i++) {
			T[N+i].setValue(0);
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
		
		for (int i = 0; i < K; i++) {
			S.post(new LessOrEqual(T[N+K+i], y));
		}
		// Error
		// S.post(new );
		mgr.close();
	}
	
	class Move {
        int i; int j;
        public Move(int i, int j) {
            this.i = i; this.j = j;
        }
    }
	
	public void generateInitSolution() {
		expandDataInput();
		createModel();
		createConstraint();
		for (int i = 0; i < N+K; i++) {
			int v = i;
			if (i >= N) v = i + K;
			X[i].setValuePropagate(v);
		}
		
	}
	
	public void satisfyConstraint() {
		for (int i = 0; i < K; i++) {
    		int _pre = N+i;
    		while (_pre < N+K) {
    			int _next = X[_pre].getValue();
    			Route[_next].setValuePropagate(i);
    			int _time = T[_pre].getValue() + t[_pre][_next] + d[_next];
    			T[_next].setValuePropagate(_time);
    			_pre = _next; 
    		}
    	}
	}
	
    public void search(int maxIter) {
    	generateInitSolution();
        ArrayList<Move> cand = new ArrayList<Move>();
        Random R = new Random();
        int it = 0;
        while(it < maxIter && S.violations() > 0) {
        	// satisfyConstraint();
            cand.clear();
            int minDelta = Integer.MAX_VALUE;
            for (int i = 0; i < N+K; i++) {
                for (int j = i+1; j < N+K; j++) {
                    int delta = S.getSwapDelta(X[i], X[j]);
                    System.out.println("\tDelta = " + delta);
                    if (delta < minDelta) {
                        cand.clear();
                        cand.add(new Move(i, j));
                        minDelta = delta;
                    } else if (delta == minDelta) {
                        cand.add(new Move(i, j));
                    }
                }
            }
            int idx = R.nextInt(cand.size());   
            Move m = cand.get(idx);
            X[m.i].swapValuePropagate(X[m.j]);
            satisfyConstraint();
            System.out.println("Step " + it + ", violations = " + S.violations());
            it++;
        }
    }
	
	public void test(int maxIter) {
		expandDataInput();
		createModel();
		createConstraint();
		search(maxIter);
	}
	
	public static void main (String[] args) {
		MiniProjectHillClimbing mini = new MiniProjectHillClimbing(6, 2);
		mini.search(1000);
	}
}
