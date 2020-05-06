package planningoptimization115657k62.daohoainam;

import planningoptimization115657k62.daohoainam.GeneralData;
import java.util.ArrayList;
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
	IntVar[][] roadmap; 
	IntVar[] P;
	IntVar[] z;
	IntVar[] path;
	IntVar[] distance;
	
	
    int min_result = 0;
    
	/* Declare global var */ 
	int M = 2; //  number of shelves
	int N = 2; // number of products
	int[][] Q; // matrix Q[i][j] is number of product ith in shelf j
	int [][] d; //d[i][j] distance from point i to j 
	int q[];  // q[i] is number of product ith employee needs
	int max_units[];
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
		      d = new int[M+1][M+1];
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
		
		for(int i = 0; i < N; i++)
			System.out.println(max_units[i]);

	}
	
	public void getMaxUnits() {
		max_units = new int[N];
		for(int k = 0; k < N; k++)
			for(int i = 0; i < M; i++ )
				max_units[k] += Q[k][i];
			
	}
	
	/* make constraint */
	public void creatConstraint() {

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
		

		
		// In order to the order visit shelves start from 0 and continue  ***
		// make constrainst sum row ith >= sum row ith-1
		// first get sum all rows
		z = new IntVar[rows];
		for(int i = 0; i < rows; i++)
			z[i] = model.intVar(0, 1);
		
		for(int k = 0; k < rows; k++) {
			model.ifThen(model.arithm(z[k], ">=", 0), model.sum(roadmap[k], "=", z[k]));
		}
		
		model.arithm(z[0], "=", 1).post();
		for(int i = 0; i < rows - 1; i++)
			model.arithm(z[i], ">=", z[i+1]).post();
		
	
	// constraint units of product     ***
		P = new IntVar[N];
		for(int k = 0; k < N; k++)
			P[k] = model.intVar(0, max_units[k]);
		
		for(int i = 0; i < N; i++) {
			IntVar[][] P_sub = new IntVar[rows][columns];
			for(int o = 0; o < rows; o++)
				for(int r = 0; r < columns; r++)
				P_sub[o][r] = model.intVar(0, 100);
			
			model.ifThen(model.arithm(P[i], ">=", 0), model.sum(P_sub[i], "=", P[i]));
			model.arithm(P[i], ">=", q[i]).post();
			
			for(int temp = 0; temp < rows; temp++) // virtual oth shelf always have no need 
				model.arithm(P_sub[i][0], "=", 0).post();
			
			for(int k = 0; k < rows; k++)
				for(int j = 1;  j < columns; j++)
					P_sub[i][j] = model.intScaleView(roadmap[i][j], Q[i][j-1] );	
					
		}
		
		// make Intvar to get path
		path = new IntVar[M+2];
		for(int i = 0; i < M+2; i++) {
			path[i] = model.intVar(0, M);
		}
	
		
		model.arithm(path[0], "=", 0).post(); // the start point is always 0

		for(int k = 1; k < rows; k++) {
			IntVar[][] Path_sub = new IntVar[rows][columns];
			for(int o = 0; o < rows; o++)
				for(int r = 0; r < columns; r++)
				Path_sub[o][r] = model.intVar(0, M);
			
			
			model.sum(Path_sub[k-1], "=", path[k]).post();
			
			
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < columns; j++) {
					model.arithm(Path_sub[i][j], "=", model.intScaleView(roadmap[i][j], j)).post();	
				}
		
		}
		
		// now we have path be like 0-1-2-3-5-0
		// so that we make constraint to optimizer distance between them
		
		distance = new IntVar[M+1]; // at most have M+1 distance
		for(int i = 0; i < M+1; i++)
			distance[i] = model.intVar(0, 999999);
		
		for(int i = 0; i < M+1; i++) {
			int j = i+1;
			model.ifThen(model.arithm(distance[i], ">=", 0), model.arithm(distance[i], ">=", d[ path[i].getValue() ][ path[j].getValue() ]));
		}
			
		
		
	}
	
	/* Solve problem */
	public void Solve() {
		Solver solver = model.getSolver();
		
		int[] scalar_dis = new int[M+1];
		for(int i = 0; i < M+1; i++)
			scalar_dis[i] = 1;
		
		IntVar OBJ = model.intVar("objective", 0, 99999999);		
		model.scalar(distance, scalar_dis,"=", OBJ).post();
		model.setObjective(Model.MINIMIZE, OBJ);
		
		
		while(solver.solve()) {
			 System.out.print("Path: ");
			 System.out.print(path[0].getValue() + " ");
			 for(int i = 1; i < path.length; i++) {
				 System.out.print(path[i].getValue() + " ");
				  
				 
			 }
			 System.out.println();
			 
			 for(int i = 0; i < rows; i++) {
				 System.out.println();
				 for(int j = 0; j < columns ; j++) {
					 System.out.print(roadmap[i][j].getValue() + " ");
					 
				 }
			 }
			 
			 System.out.println();
			 int min_result = d[path[0].getValue()][path[1].getValue()];
				 for(int i = 1; i < M+1; i++) {

						 min_result += d[path[i].getValue()][path[i+1].getValue()];
				 }
				 System.out.println();
				 System.out.println("cost_min:" + min_result);
				 System.out.println(OBJ);
				
		}

		
		
		 
		System.out.println();
        System.out.println("                            ---------- Group 7 -------------              ");
        
	} 
	
	public static void main(String args[]) {
		
		GeneralData generalData = new GeneralData();
		try {
			generalData.Gen();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		CourseProject  courseProject= new CourseProject();
		try {
			courseProject.creat();
		} catch (Exception e) {
			e.printStackTrace();
		}

		courseProject.getMaxUnits();
	courseProject.test();
		try {
	courseProject.creatConstraint();
	courseProject.Solve();
		} 
		catch(OutOfMemoryError oome){
			System.out.println("oh");
			System.gc();
		}
	}
}
