package planningoptimization115657k62.phamthanhdong;

import planningoptimization115657k62.phamthanhdong.Init;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;



import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;



public class CourseProject_HillClimbingSearch {
	
	/* Declare global variable */ 
	int M = 3; //  number of shelves
	int N = 3; // number of products
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
		VarIntLS [][] matrix;
		VarIntLS [][] P;
		VarIntLS[] flatten;
		VarIntLS OBJ;
		
		

	/* load data from file */
	public void creat()  throws Exception{
	
		String filePath = new File("").getAbsolutePath();
		// read file Q(i,j) 
		  Scanner sc = new Scanner(new BufferedReader(new FileReader(filePath+"/src/planningoptimization115657k62/phamthanhdong/Q.txt")));
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
		      Scanner sc_d = new Scanner(new BufferedReader(new FileReader(filePath+"/src/planningoptimization115657k62/phamthanhdong/distance.txt")));
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
		      Scanner sc_q = new Scanner(new File(filePath+"/src/planningoptimization115657k62/phamthanhdong/need.txt"));
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
		matrix = new VarIntLS[M][columns];
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0 ; j < columns; j++)
				matrix[i][j] = new VarIntLS(mgr, 0, 1);
		}
		
		// make constraint to ensure the start and end point always 0 be visited after all shelve be visited
		for(int i = 0; i < matrix.length; i++) {
			S.post(new IsEqual(matrix[i][0], 0));
		}
		
		
		// make constraint at a time at most one shelf be visited
		for(int i = 1; i < matrix.length; i++) {
			S.post(new LessOrEqual(new Sum(matrix[i]), 1));
		}
		
		// make constraint at least one shelf be visited
		S.post(new IsEqual(new Sum(matrix[0]), 1));
		
		// make constraint a shelf at most be visited one time
		for(int i = 0; i < columns; i++) {
			for(int j = 0;  j < matrix.length; j ++) {
				for(int k = 0;  k < matrix.length; k ++) {
					if(j != k) {
						S.post(new Implicate(new IsEqual(matrix[j][i], 1), 
								new IsEqual(matrix[k][i], 0)));
					}
				}
			}
		}
		
		// make constraint to ensure shelves be visited from 0,1, ... go on
		for(int i = matrix.length-1; i > 0; i--) {
			S.post(new LessOrEqual(new Sum(matrix[i]),
					new Sum(matrix[i-1])));
			
		}

		// make constraint units of product
		P = new VarIntLS[N][rows];
	    for(int k = 0; k < N; k++)
	       for(int j = 0; j < rows; j++) {
	    	   P[k][j] = new VarIntLS(mgr, 0, max_units[k]);
	       }
	    
	    
	    for(int i = 0; i < N; i++) {
	    	S.post(new LessOrEqual(q[i], new Sum(P[i])));
	    	S.post(new LessOrEqual(new Sum(P[i]),  max_units[i]));
	    }
	    
	    for(int i = 0; i < N; i++) {
	        VarIntLS[][] P_sub = new VarIntLS[rows][columns];
	        for(int o = 0; o < rows; o++)
	            for(int r = 0; r < columns; r++)
	            P_sub[o][r] = new VarIntLS(mgr, 0, 1000);
	        
	        for(int r = 0; r < rows; r++){
	        		S.post(new IsEqual(new Sum(P_sub[r]), P[i][r]));
	        }
	        for(int k = 0; k < rows; k++) {
	            for(int j = 1;  j < columns; j++)
	            S.post(new IsEqual(new FuncMult(matrix[k][j], Q[i][j-1]), P_sub[k][j]));
	           S.post(new IsEqual(P_sub[k][0], 0));
	        
	        }
	    }
	    

		//make constraint to optimizer distance
		
	    
	    
		   flatten = new VarIntLS[(rows+1) * columns];
		   for(int i = 0; i < flatten.length; i++) {
			  flatten[i] = new VarIntLS(mgr, 0, 999);
		  }
		   
		   
		   for(int j = 0; j < columns; j++) {
			   S.post(new IsEqual(new FuncMult(matrix[0][j], d[0][j]), flatten[j]));
		   }
		   
		   
	    for(int i = 0; i < matrix.length ; i++) {
			   for(int j = 0; j < columns; j++) {
					   if(i == matrix.length - 1) {
						  S.post( new IsEqual(new FuncMult(matrix[i][j], d[j][0] ), flatten[(i+1) * columns + j]));
					   }
					   
					   else {
						 
						   VarIntLS b =  new VarIntLS(mgr, 1,2 );
							S.post(new Implicate(new IsEqual( new Sum(matrix[i+1]), 0), new IsEqual(b, 1)));
							S.post(new Implicate(new IsEqual( new Sum(matrix[i+1]), 1), new IsEqual(b, 2)));
							  
							  VarIntLS b_2 =  new VarIntLS(mgr, 1,2);
							  S.post(new Implicate(new IsEqual(matrix[i][j], 0), new IsEqual(b_2, 1)));
							  S.post(new Implicate(new IsEqual(matrix[i][j], 1), new IsEqual(b_2, 2)));
							  
							  VarIntLS c = new VarIntLS(mgr, 0,4);
							  S.post( new IsEqual(new FuncPlus(b, b_2), c));
							  
							  VarIntLS d_ =  new VarIntLS(mgr, 5, 10);
							  S.post(new IsEqual(new FuncMult(b, 5), d_));
							  
							  
							  VarIntLS d2 = new VarIntLS(mgr, 1, 12);
							  S.post( new IsEqual(new FuncPlus(d_, b_2), d2));
							  
						  
						   for(int k = 0; k < columns; k++) {
							 S.post( new Implicate(new IsEqual(c, 4) , 
									 new IsEqual(new FuncMult(matrix[i+1][k], d[j][k] ), flatten[(i+1) * columns + k])));
							   
							   if(k < columns -1)
									 S.post( new Implicate(new IsEqual(d2, 7), new IsEqual(flatten[(i+1) * columns + k], 0)));
							   if(k == columns -1)
								   S.post( new Implicate(new IsEqual(d2, 7), new IsEqual(flatten[(i+1) * columns + k], d[j][0])));
						   }
	 
					   }
				 
			   }
	    }
		
	}
	
	public void Solve() {
		
		OBJ = new VarIntLS(mgr, min_S, max_S);
		S.post(new IsEqual(new Sum(flatten), OBJ));
		mgr.close();
		
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 1000);
		
		for(int i = 0; i < matrix.length; i++) {
			System.out.println();
			for(int j = 0; j < M+1; j++) {
				System.out.print(matrix[i][j].getValue() + " ");
			}
		}
		
		
		 
		 for(int k = 0; k < N; k++) {
			 System.out.println();
		       for(int j = 0; j < rows; j++) {
		    	   System.out.print(P[k][j].getValue() + " ");
		       }
		 }
		
		System.out.println();

	}
	
	public static void main(String[] args) throws Exception {
	CourseProject_HillClimbingSearch CP_HCS = new CourseProject_HillClimbingSearch();
//		Init Init = new Init();
//		try {
//			Init.Gen();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		CP_HCS.creat();
		CP_HCS.getMaxUnits();
		CP_HCS.findMinBound();
		CP_HCS.findMaxBound();
		CP_HCS.makeConstraint();
		CP_HCS.Solve();
		
		
		}

	
	
}
