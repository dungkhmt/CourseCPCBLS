package planningoptimization115657k62.daohoainam;

import planningoptimization115657k62.daohoainam.GeneralData;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;


public class CourseProject {
	
	// declare model 
	Model model = new Model("Get the goods in the warehouse");
	IntVar OBJ = model.intVar("objective", 0, 99999999);	
	
	IntVar[][] matrix;
	IntVar[] z;
	IntVar[][] P;
	IntVar[] path;
	IntVar[] distance;
    int min_result = 0;
    
	/* Declare global variable */ 
	int M = 2; //  number of shelves
	int N = 1; // number of products
	int[][] Q; // matrix Q[i][j] is number of product ith in shelf j
	int [][] d; //d[i][j] distance from point i to j 
	int q[];  // q[i] is number of product ith employee needs
	int max_units[];
	int[] oneP;
	int rows =  M ; //  the times, because the employee at most visit M shelves 
	int columns = M + 1; // the number of shelves
	

	
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
	
	/* make constraint */
	public void creatConstraint() {		
		matrix = new IntVar[M][M+1];
		
		// make constraint the value of matrix must be in range[0:1]
		for(int i = 0; i < matrix.length; i++) {
			for(int j = 0; j < M+1; j++) {
				matrix[i][j] = model.intVar("matrix[" + j + "," + i + "]", 0, 1);
			}
		}
		
		

		  // creat scalar columns and rows
	    int[] one_max_rows = new int[rows];
	    int[] one_max_columns = new int[columns];
	    for(int i = 0; i < rows; i++)
	        one_max_rows[i] = 1;
	    
	    for(int i = 0; i < columns; i++)
	        one_max_columns[i] = 1;
	    
	    
	    // make constraint  at a time only a shelf be visited
	    // var sum in a row in range [0, 1] 
	    for(int i = 0; i <  rows; i++) {
	        model.scalar(matrix[i],  one_max_columns, "<=", 1).post(); 
	    }



	    // make constraint one shelf be visited at most one time
	    for(int k = 0; k < columns; k++){
	        for(int q = 0; q < rows; q++)
	            for(int p = 0; p < rows; p++)
	                if( p != q)
	                    model.ifThen(model.arithm(matrix[q][k], "=", 1), 
	                                                  model.arithm(matrix[p][k], "=", 0));
	        }
	    
	    // make constraint to ensure the point 0 be not visited  EXCEPT the start and end point
	    for(int i = 0; i < rows; i++) {
	    	model.arithm(matrix[i][0], "=", 0).post();
	    }
	    
	    // In order to the order visit shelves start from 0 and continue
	    // make constraint sum row ith >= sum row ith-1
	    // first get sum all rows
	    z = new IntVar[rows];
	    for(int i = 0; i < rows; i++)
	        z[i] = model.intVar(0, 1);
	    
	    for(int k = 0; k < rows; k++) {
	        for(int i = 0; i < columns; i++) {
	            model.ifThen(model.arithm(z[k], ">=", 0), model.sum(matrix[k], "=", z[k]));
	        
	        }    
	    }
	    model.arithm(z[0], "=", 1).post();
	    for(int i = 0; i < rows - 1; i++)
	        model.arithm(z[i], ">=", z[i+1]).post();
	

	// make constraint units of product
	    P = new IntVar[N][rows];
	    for(int k = 0; k < N; k++)
	       for(int j = 0; j < rows; j++) {
	    	   P[k][j] = model.intVar(0, max_units[k]);
	       }
	    for(int i = 0; i < N; i++) {
	    	model.sum(P[i], ">=", q[i]).post();
	    	model.sum(P[i], "<=", max_units[i]).post();
	    }
	    
	    for(int i = 0; i < N; i++) {
	        IntVar[][] P_sub = new IntVar[rows][columns];
	        for(int o = 0; o < rows; o++)
	            for(int r = 0; r < columns; r++)
	            P_sub[o][r] = model.intVar(0, 1000);
	        
	        for(int r = 0; r < rows; r++){
	        		model.sum(P_sub[r], "=", P[i][r]).post();
	        }
	        for(int k = 0; k < rows; k++)
	            for(int j = 1;  j < columns; j++)
	                P_sub[k][j] = (model.intScaleView(matrix[k][j], Q[i][j-1]));    
	                
	    }
	    
	  // make constraint to get path
	    path = new IntVar[rows+2];
	    for(int i = 0; i < path.length; i++)
	    	path[i] = model.intVar(0, M);
	    
	    for(int i = 0; i < path.length; i++) {
	    	for(int j = 0; j < path.length-1; j++) {
	    		if(i!= j) {
	    			model.ifThen(model.arithm(path[i], ">", 0),
	    					model.arithm(path[i], "!=", path[j]));
	    		}
	    	}
	    }
	    
	    
	    for(int i = 2; i < path.length; i++) {
	    	model.ifThen(model.arithm(path[i], "!=", 0), 
	    			model.arithm(path[i-1], "!=", 0));
	    }
	    
	    model.arithm(path[0], "=", 0).post();
	    model.arithm(path[path.length-1], "=", 0).post();
	    
	    for(int i = 0; i < rows; i++)
	    	for(int j = 0; j < columns-1; j++) {
	    		model.ifThen(model.arithm(matrix[i][j], ">", matrix[i][j+1]),
	    				model.arithm(path[i+1], "=", j));
	    		model.ifThen(model.arithm(matrix[i][j+1], ">", matrix[i][j]),
	    				model.arithm(path[i+1], "=", j+1));

	    	}
	    
	    // make constraint distance **********************
	    distance = new IntVar[path.length-1];
	    for(int i = 0; i < distance.length; i++) {
	    	distance[i] = model.intVar(0, 9999);
	    }
	    

	  
	   
	   //*******************************
	}

    
    /////////////////////////////////
	
	/* Solve problem */
	public void Solve() {
		Solver solver = model.getSolver();
//		
//		int[] scalar_dis = new int[distance.length];
//		for(int i = 0; i < scalar_dis.length; i++)
//			scalar_dis[i] = 1;
//		
//		
//		model.scalar(distance, scalar_dis,"=", OBJ).post();
//		model.setObjective(Model.MINIMIZE, OBJ);
		solver.findAllSolutions();

//		
//			 while(solver.solve()) {
//			 System.out.print("Path: ");
//			 for(int i = 0; i < path.length; i++) {
//				 System.out.print(path[i].getValue() + " ");
//				  
//			 }
//				 for(int i = 0; i < path.length - 1; i++) {
//						min_result += d[path[i].getValue()][path[i+1].getValue()];
//				 }
//				 System.out.println();
//				 System.out.println("cost_min:" + min_result);
//				 System.out.println(OBJ);
//				 
//				 for(int i = 0; i < distance.length; i++) {
//					 System.out.println(distance[i].getValue());
//				 }
//		 
//		
//			 }

		System.out.println();
        System.out.println("                            ---------- Group 7 -------------              ");
        
	} 
	
	public static void main(String args[]) {
		
//		GeneralData generalData = new GeneralData();
//		try {
//			generalData.Gen();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
		
		CourseProject  courseProject= new CourseProject();
		try {
			courseProject.creat();
		} catch (Exception e) {
			e.printStackTrace();
		}

		courseProject.getMaxUnits();
		courseProject.showInfor();
		courseProject.checkNeed();
		courseProject.test();
		try {
	
	courseProject.creatConstraint();
	courseProject.Solve();
		} 
		catch(OutOfMemoryError oome){
			System.out.println("oh");
			Runtime.getRuntime().gc();			
		}
		Runtime.getRuntime().gc();			
	}
	
}
