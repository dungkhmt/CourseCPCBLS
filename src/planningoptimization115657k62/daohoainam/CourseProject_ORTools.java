package planningoptimization115657k62.daohoainam;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class CourseProject_ORTools {
	  static {
	      System.loadLibrary("jniortools");
	  }

	 int min_result = java.lang.Integer.MAX_VALUE;
	    
		/* Declare global var */ 
	    
		int M = 2 ; // so ke de hang hoa
		int N = 2; // so san pham
		int rows = M + 1;
		int columns = M + 1;
		
		
		int[][] Q = new int[N+1][columns];
		int [][] d =  new int[rows][columns] ; //d(i,j) là khoang cach tu diem i den diem j
		int q[]  = new int[N+1] ;  // q[i] a so luong mat hang can lay tuong ung voi san pham i
		
		
		MPSolver solver;
		MPVariable[][] X;



		/* load data from file */
		public void creat()  throws Exception{	
		}
		
		public void test() {
			for(int i = 0; i < N+1; i++)
				for(int j = 0; j < M+1; j++)
					System.out.println(Q[i][j]);
					
		}
		
		public void solve() {
			double INF = java.lang.Double.POSITIVE_INFINITY;
			MPSolver solver = new MPSolver("Warehouse Delivery", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
			MPVariable[][] x = new MPVariable[rows][columns];
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if (i != j) {
						x[i][j] = solver.makeIntVar(0, 1, "x["+i+"]["+j+"]");
					}
				}
			}
									
			for (int i = 0; i < rows; i++) {
				MPConstraint c = solver.makeConstraint(0,1);
				for (int j = 0; j < columns; j++) {
					if (i != j) {
						c.setCoefficient(x[i][j], 1);
					}
				}
			}
			for (int i = 0; i < rows; i++) {
				MPConstraint c = solver.makeConstraint(0,1);
				for (int j = 0; j < columns; j++) { 
					if (i != j) {
						c.setCoefficient(x[j][i], 1);
					}
				}
			}
			
			SubSetGenerator gen = new SubSetGenerator(rows);
			HashSet<Integer> s = gen.first();
			while (s != null) {
				if (s.size() > 1 && s.size() < rows) {
					MPConstraint c = solver.makeConstraint(0,  s.size()-1);
					for (int i: s) {
						for (int j : s) {
							if (i != j) {
								c.setCoefficient(x[i][j], 1);
							}
						}
					}
				}
				s = gen.next();
			}
			
			// constraint unit product 
			int numConstraints = N + 1;
			MPConstraint[] constraint = new MPConstraint[numConstraints];
			
			// tong khoi luong san pham phai >= tong so mat hang can lay
			for(int num = 0; num < numConstraints; num++ ) {
			constraint[num] = solver.makeConstraint(q[num], INF, "");
				
				for (int i = 0; i < rows; i++) {
					for (int j = 0; j < columns; j++) { 
						constraint[num].setCoefficient(x[i][j], Q[num][j]);
					}
				}
			}
			
			// constraint to creat the shelves to be visited always starts from 0 to the end
			
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < columns; j++) {
					
				}
			}


			
			
			
			// objective model
			MPObjective obj = solver.objective();
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					if (i != j) {
						obj.setCoefficient(x[i][j], d[i][j]);
					}				
				}			
			}
			
			obj.setMinimization();
			ResultStatus rs = solver.solve();
			if (rs != ResultStatus.OPTIMAL) {
				System.out.println("Solution not found");
			} else {
				System.out.println("obj = " + obj.value());
				int j = 0;
				for (int k = 0; k < rows; k++) {
					for (int i = 0; i < columns; i++) {
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
		}
		
		
		public static void main(String[] args) {
			CourseProject_ORTools app = new CourseProject_ORTools();
			try {
				app.creat();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			app.test();
			//app.solve();
		}


}
