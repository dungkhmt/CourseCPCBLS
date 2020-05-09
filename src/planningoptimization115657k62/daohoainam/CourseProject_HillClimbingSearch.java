package planningoptimization115657k62.daohoainam;

import planningoptimization115657k62.daohoainam.GeneralData;
import java.util.Scanner;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



public class CourseProject_HillClimbingSearch {
	
	/* Declare global variable */ 
	int M = 15; //  number of shelves
	int N = 5; // number of products
	int[][] Q; // matrix Q[i][j] is number of product ith in shelf j
	int [][] d; //d[i][j] distance from point i to j 
	int q[];  // q[i] is number of product ith employee needs
	int max_units[];
	int[] units_have_optimizer;
	int[] oneP;
	int rows =  M ; //  the times, because the employee at most visit M shelves 
	int columns = M + 1; // the number of shelves
	int max_S = - 1;
	int min_S = 99999999;
	
	
	// declare model
		LocalSearchManager mgr = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(mgr);
		VarIntLS []path = new VarIntLS[M+2];
		IFunction [] P = new IFunction[N];
		

	/* load data from file */
	public void creat()  throws Exception{
	
		String filePath = new File("").getAbsolutePath();
		// read file Q(i,j) 
		  Scanner sc = new Scanner(new BufferedReader(new FileReader(filePath+"/src/planningoptimization115657k62/daohoainam/Q.txt")));
		      while(sc.hasNextLine()) {
		    	  Q = new int[N][M];
		         for (int i = 0; i < Q.length; i++) {
		            String[] line = sc.nextLine().trim().split(" ");
		            for (int j = 0; j < line.length; j++) {
		              Q[i][j] = Integer.parseInt(line[j]);
		            }
		         }
		      }
		      sc.close();
		   
		      // read file d(i, j)
		      d = new int[M+1][M+1];
		      Scanner sc_d = new Scanner(new BufferedReader(new FileReader(filePath+"/src/planningoptimization115657k62/daohoainam/distance.txt")));
		      while(sc_d.hasNextLine()) {
		         for (int i = 0; i < d.length; i++) {
		            String[] line = sc_d.nextLine().trim().split(" ");
		            for (int j = 0; j < line.length; j++) {
		              d[i][j] = Integer.parseInt(line[j]);
		            }
		         }
		      }
		      sc_d.close();
		      
		      // read file q(k)
		      q = new int[N];
		      Scanner sc_q = new Scanner(new File(filePath+"/src/planningoptimization115657k62/daohoainam/need.txt"));
		      int i = 0;
		      while(sc_q.hasNextInt()){
		         q[i++] = sc_q.nextInt();
		      }
		      sc_q.close();
	}
	
	public void test() {
		for(int i = 0; i < Q.length; i++) {
			System.out.println();
			for(int j = 0; j < M; j++)
				System.out.print(Q[i][j] + " ");
			
		}
		
		System.out.println();
	}
	
	public void getMaxUnits() {
		max_units = new int[Q.length];
		for(int k = 0; k < Q.length; k++)
			for(int i = 0; i < M; i++ )
				max_units[k] += Q[k][i];
			
	}
	
	public void showInfor() {
		System.out.println("Max unit all shelves have:");
		for(int i = 0; i < max_units.length; i++)
			System.out.print(max_units[i] + " ");
		
		System.out.println();
		System.out.println("The employee need");
		for(int i = 0; i < q.length; i++)
			System.out.print(q[i] + " ");
		
		
		System.out.println();
		System.out.println();
	}
	
	public void checkNeed() {
		for(int i = 0; i < q.length; i++) {
			if(q[i] > max_units[i]) {
				System.out.println(" The need is greater than warehouse have :( ");
				return;
			}
		}
		System.out.println("Oh good! We have more than need !");
	}
	
	public void findMaxBound() {
		for(int i = 0; i < d.length; i++) {
			for(int j = 0; j < columns; j ++)
				max_S = Math.max(max_S, d[i][j] );
		}
		
		max_S = max_S * (M+1);
	}
	
	public void findMinBound() {
		for(int i = 0; i < d.length; i++) {
			for(int j = 0; j < columns; j ++) {
				if(d[i][j] != 0)
				min_S = Math.min(min_S, d[i][j] );
				
			}
		}
		
		min_S = min_S * 2;
		System.out.println("Min S:" + min_S);
		System.out.println();
	}
	
	public void makeConstraint() {
		
		// make constraint range of VarIntLS
		for(int i = 0; i < path.length; i++) {
			path[i] = new VarIntLS(mgr, 0, M);
		}
		
		// make constraint to ensure the start and end point always
		S.post(new IsEqual(path[0], 0));
		S.post(new IsEqual(path[path.length-1], 0));
		
		// make the constraint to ensure always at least one shelf be visited
		S.post(new NotEqual(path[1], 0));
		
		// make constraint to ensure the value difference 0 be visited at most one time
		for(int i = 0; i < path.length; i++) {
			for(int j = 0; j < path.length; j++) {
				if(i != j) {
				S.post(new Implicate(new NotEqual(path[i], 0),
						new NotEqual(path[i], path[j])));
				}
			}
		}
		
		// make constraint to ensure the  0 point at start and end  
		//all shelve be visited 
		for(int i = 2; i < path.length; i++) {
			S.post(new Implicate(new NotEqual(path[i], 0),
					new NotEqual(path[i-1], 0)));
		}
		
		// make constraint units of product
		for(int k = 0; k < N; k++) {
			for(int i = 1; i < path.length; i++) {
				if(path[i].getValue() != 0) {
				P[k] = new FuncPlus(P[k], Q[k][path[i].getValue()-1]);
				}
			}
			
		}
		
		//
		
		
		
	}
	
	public void Solve() {
		mgr.close();
	}
	
	public static void main(String[] args) throws Exception {
		CourseProject_HillClimbingSearch CP_HCS = new CourseProject_HillClimbingSearch();
		CP_HCS.creat();
		CP_HCS.test();
		
		
		
		}

	
	
}
