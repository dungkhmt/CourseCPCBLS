package planningoptimization115657k62.ngoviethoang.mip.tsp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class tsp2 {
	static {
		System.loadLibrary("jniortools");
	}
//	int n =
//	int [][] c = new int[][] {{0,4,2,5,6},
//							  {2,0,5,2,7},
//							  {1,2,0,6,3},
//							  {7,5,8,0,3},
//							  {1,2,4,3,0}};
	int n;
	int [][] c;
	MPSolver solver;
	MPVariable[][] x;
	MPObjective obj;
	public void createConstraint() {
		solver = new MPSolver("TSP", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		x = new MPVariable[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					x[i][j] = solver.makeIntVar(0, 1, "x["+i+"]["+j+"]");
				}
			}
		}
								
		for (int i = 0; i < n; i++) {
			MPConstraint c = solver.makeConstraint(1,1);
			for (int j = 0; j < n; j++) {
				if (i != j) {
					c.setCoefficient(x[i][j], 1);
				}
			}
		}
		for (int i = 0; i < n; i++) {
			MPConstraint c = solver.makeConstraint(1,1);
			for (int j = 0; j < n; j++) { 
				if (i != j) {
					c.setCoefficient(x[j][i], 1);
				}
			}
		}
		obj = solver.objective();
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					obj.setCoefficient(x[i][j], c[i][j]);
				}				
			}			
		}
		obj.setMinimization();
	}
	
	private int findNext(int s) {
		for (int i = 0; i < n; i++) {
			if (i != s && x[s][i].solutionValue() > 0) {
				return i;
			}
		}
		return -1;
	}
	
	public ArrayList<Integer> detectCycle(int s) {
		ArrayList<Integer> l = new ArrayList<Integer>();
		int x = s;
		boolean f = false;
		while (true) {
			if (f == true) {
				break;
			}
			l.add(x); 
			x = findNext(x);
			int rep = -1;
			for (int i = 0; i < l.size(); i++) {
				if(x == l.get(i)) {
					rep = i;
					return l;
				}
			}
			if (rep != -1) {
				break;
			}
		}
		return l;
	} 
	
	public void createSEC(ArrayList<Integer> s) {
		MPConstraint c = solver.makeConstraint(0, s.size()-1);
		for (int i : s) {
			for (int j : s) {
				if (i != j) {
					c.setCoefficient(x[i][j], 1);
				}
			}
		}
	}
	
	public void printResult() {
		System.out.println("obj = " + obj.value());
		int j = 0;
		for (int k = 0; k < n; k++) {
			for (int i = 0; i < n; i++) {
				if (i != j) {
					if (x[j][i].solutionValue() == 1) {
						System.out.print(j + " ");
						j = i;
						break;
					}
				}
			}
			
		}
	}
	
	private void readData() throws IOException {
		File f= new File("tsp-100");
		Scanner s = new Scanner(f);
		n = s.nextInt();
		System.out.println("n = " + n);
		c = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
					c[i][j] = s.nextInt();
					System.out.print(c[i][j] + " ");
			}
			System.out.println();
		}
		s.close();
	}
	
	public void solve() throws IOException {	
		readData();
		createConstraint();
		boolean found = false;
		while(!found) {
			ResultStatus rs = solver.solve();
			if (rs != ResultStatus.OPTIMAL) {
				System.out.println("Solution not found");
				break;
			} else {
				boolean[] mark = new boolean[n];
				for (int i = 0; i < n; i++) 
					mark[i] = false;
				for (int i = 0; i < n; i++) {
					if (!mark[i]) {
						ArrayList<Integer> s = detectCycle(i);
						if (s.size() < n) {
							for (int j = 0; j < s.size(); j++) {
								mark[s.get(j)] = true;
							}
							System.out.print("Found cycle: ");
							for (int j = 0; j < s.size(); j++) {
								System.out.print(s.get(j) + " ");								
							}
							System.out.println();
							createSEC(s);
							break;
						} else {
							printResult();
							found = true;
							break;
						}
					}
				}			
			}
		}
	}
	public static void main(String[] args) throws IOException {
		tsp2 app = new tsp2();
		app.solve();
	}
}
