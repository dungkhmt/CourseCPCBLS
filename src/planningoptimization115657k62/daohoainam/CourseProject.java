package planningoptimization115657k62.daohoainam;




import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class CourseProject {
	// declare model 
	Model model = new Model("Get the goods in the warehouse");
	IntVar[][] roadmap; 
	IntVar[] P;
	
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
		for(int i = 0; i < rows; i++) {
			IntVar[] y = new IntVar[columns];
			for(int j = 0; j < columns; j++) y[j] = roadmap[i][j];
			model.scalar(y,  one_max_rows, "<=", 1).post(); 
			if(i == 0) { // alway at least one shelf be visisted
				model.scalar(y,  one_max_rows, "=", 1);
			}
		}
		

		// make constraint one shelf be visited at most ont time
		int temp = 0;
		for(int i = 0; i < columns; i++) {
			IntVar[] y = new IntVar[rows];
			for(int j = 0; j < rows; j++) y[j] = roadmap[j][i];
			model.scalar(y,  one_max_columns, "<=", 1).post(); 
			if(temp == 0) { // the end of road always point 0
				model.scalar(y,  one_max_columns, "=", 1);
			}
			temp += 1;
		}
		
		
		
		IntVar[] z = new IntVar[rows];
		z = model.intVarArray("z", rows, 0, columns);
		// In order to the order visit shelves start from 0 and continue
		// make constrainst sum row ith >= sum row ith-1
		// first get sum all rows
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				model.ifThen(model.arithm(roadmap[i][j], "=", 1), model.arithm(z[i], "=", model.intOffsetView(z[i],  1)));
				model.ifThen(model.arithm(roadmap[i][j], "=", 0), model.arithm(z[i], "=", model.intOffsetView(z[i],  0)));
			}
		}
		// then make constraint
		for(int i = 0; i < rows-1; i++) {
			model.arithm(z[i], ">=", z[i+1]).post();
		}
		
		
		
		// constraint about units of product
		P = new IntVar [N];
		P = model.intVarArray("P", N+1, 0, 100); // assign value 0 for every var 
		
		for(int k = 0; k < N; k++) {	// loop for all product
			for(int i = 0; i <  rows; i++ ) {
				for(int j = 1; j < columns; j++) {       
					 model.ifThen(model.arithm(roadmap[i][j], "=", 1), model.arithm(P[k], "=", model.intOffsetView(P[k],  Q[k][j-1]))); 
					 model.ifThen(model.arithm(roadmap[i][j], "=", 0), model.arithm(P[k], "=", model.intOffsetView(P[k], 0)));
				}
			}
		}
		
		for(int k = 0; k < N; k++) 
			model.arithm(P[k], ">=", q[k]).post();
		
//		// rang buoc de hang luot di dau tien ko di vao ke 0 
//				model.arithm(roadmap[0][0], "=", 0);
				
				
		
		// solve the problem	
		model.getSolver().solve();
        for(int i = 0; i < rows; i++ ) {
        	System.out.println();
            for(int j = 0; j < columns; j++) {
                    System.out.print(roadmap[i][j].getValue() + " ");
            }
        }
        System.out.println();
        System.out.println("-----");
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
