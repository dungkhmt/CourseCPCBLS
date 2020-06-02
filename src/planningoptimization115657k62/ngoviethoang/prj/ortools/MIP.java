package planningoptimization115657k62.ngoviethoang.prj.ortools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public class MIP {
	long startTime;
	long endTime;
	static {
		System.loadLibrary("jniortools");
	}
	String dataFolder = "data/";
	int N;
	int K;
	int[][] c;
	MPSolver solver;
	MPVariable[][] X;
	MPVariable[] R;
	MPVariable[] Y;
	MPVariable Z;
	int bigNum1;
	int bigNum2;
	
	public File[] listFile(String folder) {
		File _folder= new File(folder);
		File[] files = _folder.listFiles();
		return files;
	}
	
	public void readFile(File file) {
		BufferedReader br = null;
        String curLine = null;
        try {
            br = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MIP.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
        	curLine = br.readLine();
        	String[] str = curLine.split(" ");
        	N = Integer.parseInt(str[0]);
        	K = Integer.parseInt(str[1]);
        	curLine = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(MIP.class.getName()).log(Level.SEVERE, null, ex);
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
               Logger.getLogger(MIP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            br.close();
        } catch (IOException ex) {
        	Logger.getLogger(MIP.class.getName()).log(Level.SEVERE, null, ex);
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
	public int getDis(int x, int y) {	
		if (x < N) {
			if (y < N) {
				return c[x][y];				
			} else {
				return c[x][0];
			}
		} else {
			if (y < N) {
				return c[0][y];
			} else {
				return c[0][0];
			}
		}
	}
	public void vrp() {
		startTime = System.currentTimeMillis();
		int INF = Integer.MAX_VALUE;
		bigNum1 = K+10;
		bigNum2 = 0;
		for (int i = 0; i <= N; i++) {
			for (int j = 0; j <= N; j++) {
				bigNum2 += c[i][j];
			}
		}
		bigNum2 += 10000;
		//Variable
		solver = new MPSolver("VRP", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		X = new MPVariable[N+2*K][N+2*K];
		R = new MPVariable[N+2*K];
		Y = new MPVariable[N+2*K];
		
		for (int i = 0; i < N+2*K; i++) {
			X[i] = solver.makeIntVarArray(N+2*K, 0, 1);
		}
		R = solver.makeIntVarArray(N+2*K, 0, K-1);
		Y = solver.makeIntVarArray(N+2*K, 0, 1000);
		
		//Constraint
		for (int i = 0; i < N; i++) {
			MPConstraint c1 = solver.makeConstraint(1,1);
			for (int j = 0; j < N+K; j++) {
				c1.setCoefficient(X[j][i], 1);
			}
			MPConstraint c2 = solver.makeConstraint(1,1);
			for (int j = 0; j < N+2*K; j++) {
				if (i < N || i >= N+K) {
					c2.setCoefficient(X[i][j], 1);
				}
			}
		}
		for (int i = N; i < N+K; i++) {
			MPConstraint c1 = solver.makeConstraint(0,0);
			for (int j = 0; j < N+2*K; j++) {
				c1.setCoefficient(X[j][i], 1);
			}
			MPConstraint c2 = solver.makeConstraint(1,1);
			for (int j = 0; j < N; j++) {
				c2.setCoefficient(X[i][j], 1);
			}
		}
		for (int i = N+K; i < N+2*K; i++) {
			MPConstraint c1 = solver.makeConstraint(1,1);
			for (int j = 0; j < N; j++) {
				c1.setCoefficient(X[j][i], 1);
			}
			MPConstraint c2 = solver.makeConstraint(0,0);
			for (int j = 0; j < N+2*K; j++) {
				c2.setCoefficient(X[i][j], 1);
			}
		}
		if (true) {
			MPConstraint c = solver.makeConstraint(0,0);
			for (int i = 0; i < N+2*K; i++) {
				c.setCoefficient(X[i][i], 1);
			}
		}
		for (int i = 0; i < K; i++) {
			MPConstraint c1 = solver.makeConstraint(i, i);
			c1.setCoefficient(R[N+i], 1);
			MPConstraint c2 = solver.makeConstraint(i, i);
			c2.setCoefficient(R[N+K+i], 1);
		}
		for (int i = 0; i < N+2*K; i++) {
			for (int j = 0; j < N+2*K; j++) {
				if (i != j) {
					MPConstraint c1 = solver.makeConstraint(-bigNum1, INF);
					c1.setCoefficient(R[i], 1);
					c1.setCoefficient(R[j], -1);
					c1.setCoefficient(X[i][j], -bigNum1);
					MPConstraint c2 = solver.makeConstraint(-INF, bigNum1);
					c2.setCoefficient(R[i], 1);
					c2.setCoefficient(R[j], -1);
					c2.setCoefficient(X[i][j], bigNum1);
				}
			}
		}
		for (int i = N; i < N+K; i++) {
			MPConstraint c = solver.makeConstraint(0,0);
			c.setCoefficient(Y[i], 1);
		}
		for (int i = 0; i < N+2*K; i++) {
			for (int j = 0; j < N+2*K; j++) {
				if (i != j) {
					MPConstraint c1 = solver.makeConstraint(-bigNum2 + getDis(i,j), INF);
					c1.setCoefficient(Y[j], 1);
					c1.setCoefficient(Y[i], -1);
					c1.setCoefficient(X[i][j], -bigNum2);
					MPConstraint c2 = solver.makeConstraint(-INF, bigNum2 + getDis(i,j));
					c2.setCoefficient(Y[j], 1);
					c2.setCoefficient(Y[i], -1);
					c2.setCoefficient(X[i][j], bigNum2);
				}
			}
		}
		for (int i = N+K; i < N+2*K; i++) {
			MPConstraint c = solver.makeConstraint(0, INF);
			c.setCoefficient(Z, 1);
			c.setCoefficient(Y[i], -1);
		}
		MPObjective obj = solver.objective();
		obj.setCoefficient(Z, 1);
		obj.setMinimization();
		ResultStatus rs = solver.solve();
		System.out.println(rs);
		if (rs != ResultStatus.OPTIMAL) {
			System.out.println("Solution not found");
		} else {
			for (int i = 0; i < K; i++) {
				int x = N+i;
				System.out.print("Route " + (i+1) + ": 0 ");
				while (x != N+K+i) {
					boolean found = false;
					for (int j = 0; j < N; j++) {
						if (X[i][j].solutionValue() == 1) {
							System.out.print("-> " + (j+1) + " ");
							x = j;
							found = true;
							break;
						}
					}
					if (found == false) {
						for (int j = N+K; j < N+2*K; j++) {
							if (X[i][j].solutionValue() == 1) {
								System.out.print("-> " + (j+1) + " ");
								x = j;
								break;
							}
						}
					}
				}
				System.out.println("-> 0");
			}
			System.out.println("Objective: " + Z.solutionValue());
		}
		endTime = System.currentTimeMillis();
		System.out.println("Run time: " + (endTime - startTime)/1000.0 +  "s");
	}
	
	public void solve() {
		File[] dataset = listFile(dataFolder);
		//File _dataFolder= new File(dataFolder);
		//System.out.println("File(s) in folder " + _dataFolder.getAbsolutePath());
		for (File file : dataset) {
			//String filename = file.getName();
			//System.out.println(filename);
			readFile(file);
			vrp();
			//printData();
		}
	}
	
	public static void main(String[] args) {
		MIP app = new MIP();
		app.solve();
	}
}

	
	