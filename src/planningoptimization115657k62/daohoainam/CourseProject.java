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
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;


public class CourseProject {
	
	// declare model 
	Model model = new Model("Get the goods in the warehouse");
	IntVar[][] roadmap; 
	IntVar[] z;
	IntVar[] P;
	IntVar[] path;
	IntVar[] distance;
	
    int min_result = 0;
    
	/* Declare global variable */ 
	int M = 10; //  number of shelves
	int N = 5; // number of products
	int[][] Q; // matrix Q[i][j] is number of product ith in shelf j
	int [][] d; //d[i][j] distance from point i to j 
	int q[];  // q[i] is number of product ith employee needs
	int max_units[];
	int rows = 2 * M - 1; // because the point 0 can visited many time so at most have total  2 * M - 1  times ( except default the first and end point is always 0th
	int columns = M + 1; // have M shelf and point 0
	

	
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
		for(int i = 0; i < N; i++) {
			System.out.println();
			for(int j = 0; j < M; j++)
				System.out.print(Q[i][j] + " ");
			
		}
		
		System.out.println();
		
		for(int i = 0; i < N; i++)
			System.out.print(max_units[i] + " ");
		
		System.out.println();
		
		for(int i = 0; i < N; i++)
			System.out.print(q[i] + " ");
		
		System.out.println();
	
		

	}
	
	public void getMaxUnits() {
		max_units = new int[N];
		for(int k = 0; k < N; k++)
			for(int i = 0; i < M; i++ )
				max_units[k] += Q[k][i];
			
	}
	
	/* make constraint */
	public void creatConstraint() {
		System.gc();
		roadmap = new IntVar[rows][columns];
		
		// create scalar columns and rows
		int[] one_max_rows = new int[columns];
		
		for(int i = 0; i < columns; i++)
			one_max_rows[i] = 1;

		/* Make Constraint */
		
		//make constraint IntVar in range[0,1]
		for(int i = 0; i < rows; i ++)
			for(int j = 0; j < columns; j++ )
				roadmap[i][j] = model.intVar(0, 1);
		
		// make constraint  at a time only a shelf be visited
		//  IntVar sum in a row in range [0, 1] 
		for(int i = 0; i <  rows; i++) {
			model.scalar(roadmap[i],  one_max_rows, "<=", 1).post(); 
		}

		// make constraint one shelf be visited at most one time EXCEPT point 0th
		for(int k = 1; k < columns; k++){
			for(int q = 0; q < rows; q++)
			    for(int p = 0; p < rows; p++)
			    	if( p != q)
			    		model.ifThen(model.arithm(roadmap[q][k], "=", 1), 
			                                          model.arithm(roadmap[p][k], "=", 0));
			}

		model.arithm(roadmap[0][0], "=", 0).post();
		

		// because we don't want have continous many number 1 in column 0th 
		// so we make constraint to prevent it
		
		for(int i = 1; i < rows-1; i++) {
			model.ifThen(model.arithm(roadmap[i][0], "=", 1), 
				model.arithm(roadmap[i+1][0], "=", 0));
		}
		
		// In order to the order visit shelves start from 0 and continue  ***
		// make constraint sum row ith >= sum row ith-1
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


		// make IntVar to get path
		path = new IntVar[rows+2];
		
		for(int i = 0; i < rows+2; i++) {
			path[i] = model.intVar(0, M);
		}
	
		
		model.arithm(path[0], "=", 0).post(); // the start point is always 0
	//	model.arithm(path[rows+1], "=", 0).post(); // the max end point always 0

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
		
		// constraint units of product     ***
		P = new IntVar[N];
		for(int p = 0; p < N; p++)
				P[p] = model.intVar(0, max_units[p]);
		
		
		for(int i = 0; i < N; i++) {
			IntVar[] p_sub = new IntVar[path.length];
			for(int j = 0; j < path.length; j++)
				p_sub[j] = model.intVar(0, max_units[i]);
			
			model.sum(p_sub, "=", P[i]).post();
			model.arithm(P[i], ">=", q[i]).post();
			
			for(int pa = 0; pa < path.length; pa++) {
				model.ifThen(model.arithm(path[pa], ">", 0), 
						model.arithm(p_sub[pa], "=", Q[i][path[pa].getValue()]));
				model.ifThen(model.arithm(path[pa], "=", 0), 
						model.arithm(p_sub[pa], "=", 0));
			}

		}
		
		
		// now we have path be like 0-1-2-3-5-0
		// so that we make constraint to optimizer distance between them
		
		distance = new IntVar[rows+1]; // at most have rows+1 distance
		for(int i = 0; i < rows+1; i++)
			distance[i] = model.intVar(0, 999999);
		
		for(int i = 0; i < rows+1; i++) {
			int j = i+1;
			model.ifThen(model.arithm(distance[i], ">=", 0), model.arithm(distance[i], "=", d[ path[i].getValue() ][ path[j].getValue() ]));
		}
			
		
		
	}
	
	/* Solve problem */
	public void Solve() {
		Solver solver = model.getSolver();
		
		int[] scalar_dis = new int[rows+1];
		for(int i = 0; i < rows+1; i++)
			scalar_dis[i] = 1;
		
		IntVar OBJ = model.intVar("objective", 0, 99999999);		
		model.scalar(distance, scalar_dis,"<=", OBJ).post();
		model.setObjective(Model.MINIMIZE, OBJ);
		
		
		while(solver.solve()) {
			 System.out.print("Path: ");
			 for(int i = 0; i < path.length; i++) {
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
				 for(int i = 0; i < rows-1; i++) {
						min_result += d[path[i].getValue()][path[i+1].getValue()];
				 }
				 System.out.println();
				 System.out.println("cost_min:" + min_result);
				 for(int i = 0; i < N; i++)
					 System.out.println(P[i].getValue());
			

				
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
			
		}
		System.gc();
	}
	
	
}
