package planningoptimization115657k62.daohoainam;




import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import choco.kernel.model.variables.integer.IntegerVariable;



public class CourseProject {
    int min_result = java.lang.Integer.MAX_VALUE;
    
	/* Declare global var */ 
    
	int M = 2 ; // so ke de hang hoa
	int N = 1; // so san pham
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
	
		
		/* tao model */
		Model model = new Model("Get the goods in the warehouse");
		IntVar[][] roadmap =  new IntVar[M+2][M+1]; // bien toi uu la thanh phan mang 2 chieu chua lo trinh duong di 0-1-2-3-0
		
		int rows = M+2;
		int columns = M+1;
		
		/* khoi tao scalar cot */
		int[] one_max_columns = new int[columns];
		
		for(int i = 0; i < columns; i++)
			one_max_columns[i] = 1;
	
		
		/* Make Constraint */
		
		// cac bien rang buoc trrong khoang [0,1]
		for(int i = 0; i < rows; i ++)
			for(int j = 0; j < columns; j++ )
				roadmap[i][j] = model.intVar(0, 1);
		
		// cac lan di khac nhau thi ke hang phai khac nhau  ( rang buoc hang tong cot<= 1 )
		for(int i = 1; i < rows-1; i++) {
			IntVar[] y = new IntVar[columns];
			y[0] = model.intVar(0);
			for(int j = 1; j < columns; j++) y[j] = roadmap[j][i];
			model.scalar(y,  one_max_columns, "<=", 1).post(); 
		}
		
		
		// Moi ke neu duoc di thi chi duoc di 1 lan ( rang buoc tong hang = 1)
		
		
			for(int i  = 1; i < columns; i++) {
				for(int k = 1; k < columns; k++) {
					for(int j = 1; j < rows-1; j++)
						if(i != k)
						model.ifThen(model.arithm(roadmap[i][j], "=", 1),
																		model.arithm(roadmap[k][j], "=", 0));
				}
		}
		
		
		// rang buoc ve so luong cac san pham
		
		IntVar[] P = new IntVar [N+1];
		
		for(int i = 0;  i < N+1; i++) {
			P[i] = model.intVar(0, 1000000000);
		}
		
	

		for(int k = 1; k <= N; k++) {	
			for(int i = 1; i <  rows-1; i++ ) {
				for(int j = 1; j < columns; j++) { //                  P[k] = P[k] + Q[k][j]
					model.ifThen(model.arithm(roadmap[i][j], "=", 1), model.arithm(P[k], "=", model.intOffsetView(P[k],  Q[k][j]))); // quet qua tat ca cac ke, tinh tong tat cac cac khoi luong san pham co the co cua ke voi san pham i  neu ke duoc den

					                                                      
				}
			}
			
			model.arithm(P[k], ">=", q[k]).post();
		}
		
			
		// rang buoc cac diem bat dau va ket thuc deu la 0
		roadmap[0][0] = model.intVar(1);
		roadmap[rows-1][0] = model.intVar(1);
		
		// o hang cuoi va dau thi ngoai ke 0  = 1 tat ca cac ke khac bang 0
		for(int i = 1; i < columns; i++){
			roadmap[0][i] = model.intVar(0);
			roadmap[rows-1][i] = model.intVar(0);
			
		}
		
		// o dau hang cua ke 0 thi ngoai tru dau va cuoi tat ca bang 0
		for(int i = 1; i < rows-1; i++)
			roadmap[0][i] = model.intVar(0);
	
		
		// giai bai toan lay duoc dung cac constraint truoc
		
		
		int count = 0;
		
		model.getSolver().solve();
        for(int i = 0; i < rows; i++ )
            for(int j = 0; j < columns; j++) {
                if (roadmap[i][j].getValue() == 1) {
                    System.out.println("j:" +  j);
                }
            }
        
		
		/*
		


		
		while(model.getSolver().solve()) {
			count += 1;
			ArrayList<Integer> path = new ArrayList<Integer>();
			for(int i = 0; i < rows; i++ )
				for(int j = 0; j < columns; j++) {
					if (roadmap[i][j].getValue() == 1) {
						path.add(j);
					} 
				}
			
			int temp = 0;
			int len = path.size();
			for(int k = 0; k < len-1; k++ ) {
				int shelf_1 = path.get(k);
				int shelf_2 = path.get(k+1);
				
				temp += d[shelf_1][shelf_2];
			
				
			}
			
			if(min_result > temp) {
				min_result = temp;
				System.out.println("The best solution now is:");
				for(int k = 0; k < 	rows; k ++) {
					System.out.print(path.get(k) +"-");
				}
			}
			
		}

		*/
		
	}
	
	
	public static void main(String args[]) {
		CourseProject  courseProject= new CourseProject();
		try {
			courseProject.creat();
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
