package planningoptimization115657k62.daohoainam;



import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class CourseProject_ORTools {
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
		
		

		
		
		
		
}
