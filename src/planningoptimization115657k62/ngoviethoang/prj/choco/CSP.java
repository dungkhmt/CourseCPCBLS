package planningoptimization115657k62.ngoviethoang.prj.choco;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.selectors.variables.FirstFail;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;

public class CSP {
	
	public String dataFolder = "data/";
	public int N;
	public int K;
	public int[][] c;
	
	public IntVar[] X;
	public IntVar[] L;
	public IntVar[] R;
	Model model;
	int INF = Integer.MAX_VALUE; 	
	
	public File[] listFile(String folder) {
		File _folder= new File(folder);
		File[] files = _folder.listFiles();
		return files;
	}
	
	public int distance(int i, int j) {
		if (i > N) i = 0;
		if (j > N) j = 0;
		return c[i][j];
	}
	
	public void readFile(File file) {
		BufferedReader br = null;
        String curLine = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CSP.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
        	curLine = br.readLine();
        	String[] str = curLine.split(" ");
        	N = Integer.parseInt(str[0]);
        	K = Integer.parseInt(str[1]);
        	curLine = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(CSP.class.getName()).log(Level.SEVERE, null, ex);
        }

        String[] str = null;
        int i = 0;
        int j = 0;
        c = new int[N+1][N+1];
        while (curLine != null) {
            str = curLine.split(" ");
            j = 0;
            for (String s : str) {
            	c[i][j] = Integer.parseInt(s);
            	j++;
            }
            try {
                curLine = br.readLine();
                i++;
            } catch (IOException ex) {
               Logger.getLogger(CSP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            br.close();
        } catch (IOException ex) {
        	Logger.getLogger(CSP.class.getName()).log(Level.SEVERE, null, ex);
        }
	}
	
	public void printData() {
		System.out.println(N + " " + K);
		for (int i = 0; i <= N; i++) {
			for (int j = 0; j <= N; j++) {
				System.out.print(c[i][j] + " ");
			}
			System.out.println();
		}
	}
	
	public void solve() {
		model = new Model("vrp");
		X = model.intVarArray(N+2*K, 0, N+2*K-1);
		L = model.intVarArray(N+2*K, 0, 10000);
		R = model.intVarArray(N+2*K, 0, K-1);
		
		//Constraint
		for (int i = N; i < N+K; i++) {
			model.arithm(L[i], "=", 0).post();
		}
		
		for (int i = 0; i < K; i++) {
			model.arithm(R[N+i], "=", i).post();
			model.arithm(R[N+K+i], "=", i).post();
		}
		
		for (int i = 0; i < N+2*K-1; i++) {
			for (int j = i+1; j < N+2*K; j++) {
				model.arithm(X[i], "!=", X[j]).post();
			}
		}
		
		for (int i = 0; i < N+2*K; i++) model.arithm(X[i], "!=", i).post();
		
		for (int i = 0; i < N+K; i++) {
			for (int j = 0; j < N+2*K; j++) {
				if (i != j) {
					Constraint c1 = model.arithm(R[i], "=", R[j]);
					Constraint c2 = model.arithm(L[j], "=", model.intOffsetView(L[i], distance(i+1, j+1)));
					model.ifThen(model.arithm(X[i], "=", j), 
							model.and(c1,c2));
				}
			}
		}
		
		int[] ones = new int[K];		
		for (int i = 0; i < K; i++) {
			ones[i] = 1;
		}
		IntVar[] y = new IntVar[K];
		for (int i = 0; i < K; i++) {
			y[i] = L[i+N+K];
		}
		IntVar f = model.intVar(0, 1000);
		model.scalar(y, ones, "=", f).post();	
		
		//model.setObjective(Model.MINIMIZE, f);
		System.out.println("Solving");
		boolean res = model.getSolver().solve();
		if (!res) {
			System.out.println("No solution");
		} else {
			System.out.println("Founded");
			for (int i = 0; i < K; i++) {
				int x = N+i;
				System.out.print("Route " + (i+1) + ": 0 ");
				while (x != N+K+i) {
					x = X[x].getValue();
					if (x >= N) {
						x = 0;
						break;
					}		
					System.out.print("-> " + (x+1) + " ");
				}
				System.out.println("-> 0");
			}
			System.out.println("Objective: " + f.getValue());
		}
	}
	
	public static void main(String[] arg) {
		CSP app = new CSP();
		app.solve();
	}
}
