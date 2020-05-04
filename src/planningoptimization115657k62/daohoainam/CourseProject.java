package planningoptimization115657k62.daohoainam;




import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class CourseProject {
	
	/* tao model */
	Model model = new Model("Get the goods in the warehouse");
	IntVar[][] roadmap; // bien toi uu la thanh phan mang 2 chieu chua lo trinh duong di 0-1-2-3-0
	IntVar[] P;
	
	
    int min_result = java.lang.Integer.MAX_VALUE;
    int INF = java.lang.Integer.MAX_VALUE;
    
	/* Declare global var */ 
    
	int M = 2 ; // so ke de hang hoa
	int N = 2; // so san pham
	int[][] Q = new int[N+1][M+1];
	
	int [][] d =  new int[M+1][M+1] ; //d(i,j) là khoang cach tu diem i den diem j
		  
	int q[]  = new int[N+1] ;  // q[i] a so luong mat hang can lay tuong ung voi san pham i
	
	

	
	/* khoi tao du lieu */
	public void creat()  throws Exception{
		
		String filePath = new File("").getAbsolutePath();
		// doc file Q(i,j)
		  Scanner sc = new Scanner(new BufferedReader(new FileReader(filePath+"/src/planningoptimization115657k62/daohoainam/Q.txt")));
		      while(sc.hasNextLine()) {
		         for (int i=0; i<Q.length; i++) {
		            String[] line = sc.nextLine().trim().split(" ");
		            for (int j=0; j<line.length; j++) {
		              Q[i][j] = Integer.parseInt(line[j]);
		            }
		         }
		      }
		      sc.close();
		   
		      // doc file d(i, j)
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
		      
		      // doc file q(k)
		     
		      Scanner sc_q = new Scanner(new File(filePath+"/src/planningoptimization115657k62/daohoainam/need.txt"));
		      int i = 0;
		      while(sc_q.hasNextInt()){
		         q[i++] = sc_q.nextInt();
		      }
		   
		      sc_q.close();
   
	}
	
	
	/* Solve problem */
	public void Solve() {
		
	int rows = M+1;
	int columns = M+1;
	
	roadmap = new IntVar[rows][columns];
		
		/* khoi tao scalar cot  va hang */
		int[] one_max_columns = new int[columns];
		for(int i = 0; i < columns; i++)
			one_max_columns[i] = 1;
	
		
		/* Make Constraint */
		
		// cac bien rang buoc trrong khoang [0,1]
		for(int i = 0; i < rows; i ++)
			for(int j = 0; j < columns; j++ )
				roadmap[i][j] = model.intVar(0, 1);
		
		// cac lan di khac nhau thi ke hang phai khac nhau  ( rang buoc hang tong cot<= 1 )
		for(int i = 0; i < rows; i++) {
			IntVar[] y = new IntVar[columns];
			for(int j = 0; j < columns; j++) y[j] = roadmap[j][i];
			model.scalar(y,  one_max_columns, "<=", 1).post(); 
		}
		
		IntVar[] z = new IntVar[rows];
		z= model.intVarArray("z", rows, 0, columns);
		// rang buoc de bat buoc cac ke phai den theo thu tu
		// cac hang o tren phai co tong >= hang o duoi
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				model.ifThen(model.arithm(roadmap[i][j], "=", 1), model.arithm(z[i], "=", model.intOffsetView(z[i],  1)));
				model.ifThen(model.arithm(roadmap[i][j], "=", 0), model.arithm(z[i], "=", model.intOffsetView(z[i],  0)));
			}
			
		}
		
		for(int i = 0; i < rows-1; i++) {
			model.arithm(z[i], ">=", z[i+1]).post();
		
		}

		
		// Moi ke neu duoc di thi chi duoc di 1 lan ( rang buoc tong hang = 1)
			for(int i  = 0; i < columns; i++) {
				for(int k = 0; k < columns; k++) {
					for(int j = 0; j < rows-1; j++)
						if(i != k)
						model.ifThen(model.arithm(roadmap[i][j], "=", 1),
																		model.arithm(roadmap[k][j], "=", 0));
				}
		}
		
		
		// rang buoc ve so luong cac san pham
		P = new IntVar [N+1];
		// khoi tao gia tri 0 cho cac bien
		P = model.intVarArray(N+1, 0, 1000000);
		for(int k = 0; k <= N; k++) {	
			for(int i = 0; i <  rows; i++ ) {
				for(int j = 0; j < columns; j++) {               //                  P[k] = P[k] + Q[k][j]
					 model.ifThen(model.arithm(roadmap[i][j], "=", 1), model.arithm(P[k], "=", model.intOffsetView(P[k],  Q[k][j]))); // quet qua tat ca cac ke, tinh tong tat cac cac khoi luong san pham co the co cua ke voi san pham i  neu ke duoc den
					 model.ifThen(model.arithm(roadmap[i][j], "=", 0), model.arithm(P[k], "=", model.intOffsetView(P[k], 0)));
				}
			}
		}
		
		for(int k = 0; k < N+1; k++) 
			model.arithm(P[k], ">=", q[k]).post();
		
				
		
		// giai bai toan lay duoc dung cac constraint truoc		
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
		try {
		courseProject.Solve();
		} 
		catch(OutOfMemoryError oome){
			System.out.println("oh");
			System.gc();
		}
	}
}
