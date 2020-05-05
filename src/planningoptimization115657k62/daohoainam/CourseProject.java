package planningoptimization115657k62.daohoainam;




import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import choco.Choco;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;

public class CourseProject {
	// declare model 
	Model model = new Model("Get the goods in the warehouse");
	IntVar[][] roadmap; 
	
	
    int min_result = java.lang.Integer.MAX_VALUE;
    int INF = java.lang.Integer.MAX_VALUE;
    
	/* Declare global var */ 
	int M = 2 ; //  number of shelves
	int N = 2; // number of products
	int[][] Q; // matrix Q[i][j] is number of product ith in shelf j
	int [][] d; //d[i][j] distance from point i to j 
	int q[];  // q[i] is number of product ith employee needs
	int rows = M+1;
	int columns = M+1;
	

	
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
		      d = new int[M+1][N+1];
		      Scanner sc_d = new Scanner(new BufferedReader(new FileReader(filePath+"/src/planningoptimization115657k62/daohoainam/distance.txt")));
		      while(sc_d.hasNextLine()) {
		         for (int i=0; i<d.length; i++) {
		            String[] line = sc_d.nextLine().trim().split(" ");
		            for (int j=0; j<line.length; j++) {
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
		for(int i = 0; i < M; i++) {
			System.out.println();
			for(int j = 0; j < N; j++)
				System.out.println(Q[i][j]);
			
		}
	}
	
	/* Solve problem */
	public void Solve() {
		IntVar[] P;
		IntVar[] z;
	

	
	roadmap = new IntVar[rows][columns];
		
		// creat scalar columns and rows
		int[] one_max_rows = new int[rows];
		int[] one_max_columns = new int[columns];
		for(int i = 0; i < columns; i++)
			one_max_rows[i] = 1;
		
		for(int i = 0; i < rows; i++)
			one_max_columns[i] = 1;
		

		/* Make Constraint */
		
		//make constrainst var in range[0,1]
		for(int i = 0; i < rows; i ++)
			for(int j = 0; j < columns; j++ )
				roadmap[i][j] = model.intVar(0, 1);
		
		// make constrainst  at a time only a shelf be visited
		// var sum in a row in range [0, 1] 
		for(int i = 0; i <  rows; i++) {
			model.scalar(roadmap[i],  one_max_columns, "<=", 1).post(); 
		}

		// make constraint one shelf be visited at most one time
		for(int k = 0; k < columns; k++){
			for(int q = 0; q < rows; q++)
			    for(int p = 0; p < rows; p++)
			    	if( p != q)
			    		model.ifThen(model.arithm(roadmap[q][k], "=", 1), 
			                                          model.arithm(roadmap[p][k], "=", 0));
			}

		model.arithm(roadmap[0][0], "=", 0).post();
		
		for(int i = 0; i < rows-1; i++)
			model.arithm(roadmap[i][0], "<=", roadmap[i+1][0]).post();
		// In order to the order visit shelves start from 0 and continue
		// make constrainst sum row ith >= sum row ith-1
		// first get sum all rows
		z = new IntVar[rows];
		for(int i = 0; i < rows; i++)
			z[i] = model.intVar(0, 1);
		
		for(int k = 0; k < rows; k++) {
			for(int i = 0; i < rows; i++) {
				model.ifThen(model.arithm(z[k], ">=", 0), model.sum(roadmap[i], "=", z[k]));
			
			}	
		}
		model.arithm(z[0], "=", 1).post();
		for(int i = 0; i < rows - 1; i++)
			model.arithm(z[i], ">=", z[i+1]).post();
	
	// constraint units of product
		P = new IntVar[N];
		for(int k = 0; k < N; k++)
			P[k] = model.intVar(0, 1000);
		
		for(int i = 0; i < N; i++) {
			IntVar[][] P_sub = new IntVar[rows][columns];
			for(int o = 0; o < rows; o++)
				for(int r = 0; r < columns; r++)
				P_sub[o][r] = model.intVar(0, 1000);
			
			model.ifThen(model.arithm(P[i], ">=", 0), model.sum(P_sub[i], "=", P[i]));

			for(int temp = 0; temp < rows; temp++) // ke ao 0 luon co nhu cau lay bang 0
				model.arithm(P_sub[i][0], "=", 0).post();
			
			for(int k = 0; k < rows; k++)
				for(int j = 1;  j < columns; j++)
					P_sub[i][j] = model.intScaleView(roadmap[i][j], Q[i][j-1] );	
			model.arithm(P[i], ">=", q[i]).post();
					
		}
			
		
		
		
		// solve the problem	
		
		while( model.getSolver().solve() ) {
			 ArrayList<Integer> path = new ArrayList<Integer>();
			for(int i = 0; i < rows; i++ ) {
				System.out.println();
				for(int j = 0; j < columns; j++) {
                    System.out.print(roadmap[i][j].getValue() + " ");
                    if(roadmap[i][j].getValue() == 1) {
                    	path.add(j);
                    }
				}
			}
			System.out.println();
			System.out.println("Path:");
			for(int k = 0; k < path.size(); k++) {
				System.out.print(k + "  ");
			}
			
        
		}
     
        System.out.println();
        System.out.println("----------Group 7-------");
        
	}
	
	public static void main(String args[]) {
		CourseProject  courseProject= new CourseProject();
		try {
			courseProject.creat();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//courseProject.test();
		
		try {
		courseProject.Solve();
		} 
		catch(OutOfMemoryError oome){
			System.out.println("oh");
			System.gc();
		}
	}
}
