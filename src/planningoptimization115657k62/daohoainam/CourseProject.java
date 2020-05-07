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
	IntVar[] path;
	IntVar[] distance;
	IntVar[] P;
	
    int min_result = 0;
    
	/* Declare global variable */ 
	int M = 5; //  number of shelves
	int N = 10; // number of products
	int[][] Q; // matrix Q[i][j] is number of product ith in shelf j
	int [][] d; //d[i][j] distance from point i to j 
	int q[];  // q[i] is number of product ith employee needs
	int max_units[];
	int rows =  M ; //  the times, because the employee at most visit M shelves 
	int columns = M; // the number of shelve
	

	
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
	
	public void showInfor() {
		System.out.println("Max unit all shelves have:");
		for(int i = 0; i < N; i++)
			System.out.print(max_units[i] + " ");
		
		System.out.println();
		System.out.println("The employee need");
		for(int i = 0; i < N; i++)
			System.out.print(q[i] + " ");
		
		System.out.println();
		System.out.println();
	}
	/* make constraint */
	public void creatConstraint() {
		System.gc();
		
		// make constraint the value of path[i] must be in range[0:M]
		path = new IntVar[rows+2];
		for(int i = 0; i < path.length; i++)
			path[i] = model.intVar(0, M);
		
		model.arithm(path[0], "=", 0).post(); // the start pont
		model.arithm(path[rows+1], "=", 0).post(); // the end point
		
		
		// make the constraint one shelf must be visited at most one time(EXCEPT 0th point)
		for(int i = 0; i < path.length; i++) {
			for(int j = 0; j < path.length; j++) {
				if(i != j) { // with 2 distinct times
					model.ifThen(model.arithm(path[i], "!=", 0 ),  // and greater than 0
							model.arithm(path[i], "!=", path[j])); // must be distinct
				}
			}
			
		}	
		
		// make constraint to ensure the point 0 in path when the employrr
		// visited all need shelves
		model.arithm(path[1], "!=", 0).post();
		for(int i = 2; i < path.length; i++) {
			model.ifThen(model.arithm(path[i],"!=" , 0),
					model.arithm(path[i-1], "!=", 0));
		}
		
		// make constraint unit of product
		for(int i = 0; i < N; i++) {
			IntVar[] p_sub = new IntVar[path.length];
			for(int j = 0; j < p_sub.length; j++)
				p_sub[j] = model.intVar(0, 99999);
			
			model.sum(p_sub, ">=", q[i]).post();
			
			for(int j = 0; j < path.length; j++) {
				model.ifThen(model.arithm(path[j], "=", 0), model.arithm(p_sub[j], "=", 0));
				model.ifThen(model.arithm(path[j], "!=", 0), model.arithm(p_sub[j], "=", Q[i][path[j].getValue()]-1));
			}
		
		}
		
		// make the constraint distance[i]
		distance = new IntVar[rows+1]; // add 2 times the start and end point
		for(int i = 0; i < distance.length; i++)
			distance[i] = model.intVar(0, 9999999);
		
		for(int i = 0; i < distance.length; i++)
			model.arithm(distance[i], "=", d[path[i].getValue()][path[i+1].getValue()]).post();		

	}
	
	/* Solve problem */
	public void Solve() {
		Solver solver = model.getSolver();
		
		int[] scalar_dis = new int[distance.length];
		for(int i = 0; i < scalar_dis.length; i++)
			scalar_dis[i] = 1;
		
		
		model.scalar(distance, scalar_dis,"<=", OBJ).post();
		model.setObjective(Model.MINIMIZE, OBJ);
		

		while(solver.solve()) {
			 System.out.print("Path: ");
			 for(int i = 0; i < path.length; i++) {
				 System.out.print(path[i].getValue() + " ");
				  
				 
			 }
				 for(int i = 0; i < path.length - 1; i++) {
						min_result += d[path[i].getValue()][path[i+1].getValue()];
				 }
				 System.out.println();
				 System.out.println("cost_min:" + min_result);
				// System.out.println(OBJ);
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
		courseProject.showInfor();
		//courseProject.test();
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
