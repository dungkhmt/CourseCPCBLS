package mip;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import com.google.ortools.constraintsolver.FirstSolutionStrategy.Value;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public class TSP_Ortool {
	static {
	    System.loadLibrary("jniortools");
	}
	int n = 4;
	int [][] c = { {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
          };
	MPSolver solver = new MPSolver("TSP", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
	MPVariable[][] X;
	
	public ArrayList<Integer> sub(int k, int[] danhdau, int[][] local) {
		ArrayList<Integer> S = new ArrayList<Integer>();
		int i = k;
		danhdau[i] = 1;
		S.add(k);
		for(int j=0; j<n; j++)
			if ((local[i][j] == 1) && (j != i)) {
				i = j;
				break;
			}
		
		while(i != k) {
			S.add(i);
			danhdau[i] = 1;
			for(int j=0; j<n; j++)
				if ((local[i][j] == 1) && (j != i)) {
					i = j;
					break;
				}			
		}
		return S;
	}
	
	
	public void solve() {
		// Tao bien
		X = new MPVariable[n][n];
		for (int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "][" + j + "]");
			}
		}
		
		//rang buoc
		for (int i = 0; i < n; i++) {
			MPConstraint c1 = solver.makeConstraint(1,1);
			MPConstraint c2 = solver.makeConstraint(1,1);
			for (int j = 0; j < n; j++) {
				if(j != i)
					c1.setCoefficient(X[i][j], 1);
					c2.setCoefficient(X[j][i], 1);
			}	
		}
		
		MPObjective Obj = solver.objective();
		for (int i = 0; i < n; i++) {
			for(int j = 0; j < n; j++) {
				Obj.setCoefficient(X[i][j], c[i][j]);
			}
		}
		
		Obj.setMinimization();
		while(1>0) {
			ResultStatus rs = solver.solve();
			if(rs == ResultStatus.OPTIMAL) {
				int[] danhdau = new int[n];
				ArrayList<Integer> ds = new ArrayList<Integer>();
				
				//save to local optimization
				int[][] local = new int[n][n];
				for (int i = 0; i < local.length; i++)
					for (int j = 0; j < local.length; j++)
						local[i][j] = (int)X[i][j].solutionValue();
				//ds.clear();
				for(int k=0; k<n; k++)
					if(danhdau[k] == 0) {
						ds = sub(k, danhdau, local);
						if(ds.size() != n) {
							solver.reset();
							MPConstraint c = solver.makeConstraint(0, ds.size()-1);
							for(int i=0; i<ds.size()-1; i++) {
								c.setCoefficient(X[ds.get(i)][ds.get(i+1)], 1);
							}
							c.setCoefficient(X[ds.get(ds.size()-1)][ds.get(0)], 1);
						}
					}
				
				if(ds.size() == n) {
					System.out.println("Gia tri ham muc tieu la: " + Obj.value());
					int dem = 0;
					for (int x : ds) {
						dem++;
						System.out.print(x + " --> ");
						if(dem == 15) {
							dem = 0;
							System.out.println();
						}
					}
					System.out.println(0);
					return;
				}
				
			}
			else {
				System.out.println("Can not solver");
				return;
			}
		}
	}

	public void khoitao() {
		Scanner scanner = new Scanner(System.in);
		System.out.print("Nhap n: ");
		n = scanner.nextInt();
		System.out.println("Nhap khoang cach: ");
		c = new int[n][n];
		for(int i = 0; i<n; i++)
			for(int j = 0; j<n; j++)
				c[i][j] = Integer.parseInt(scanner.next());
		
		return;
	}
	
	public static void main(String[] args) {
		TSP_Ortool app = new TSP_Ortool();
		//app.khoitao();
		app.solve();
		return;
	}
}
