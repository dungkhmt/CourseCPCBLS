package planningoptimization115657k62.daohoainam;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Assingment_1_openCBLS {
	// declare model
		LocalSearchManager mgr = new LocalSearchManager();
		ConstraintSystem S = new ConstraintSystem(mgr);
		
		VarIntLS[][]matrix;
		VarIntLS[][] temp;
		VarIntLS[][] element;
		
		int V[];
		int liquid[];
		ArrayList<ArrayList<Integer>> conflict;
		
		
		
		/* Declare global variable */ 
		int numBucket = 5;
		int numLiquid = 20;
		int rows = numBucket;
		int columns = numLiquid;
		int bound_max = -1;
		
		
		
		public void create() throws FileNotFoundException {
			V = new int[numBucket];
			liquid = new int[numLiquid];
			
			  String filePath = new File("").getAbsolutePath();
			  
		      // read file V(k)
		      Scanner sc_V = new Scanner(new File(filePath+"/src/planningoptimization115657k62/daohoainam/V.txt"));
		      int i = 0;
		      while(sc_V.hasNextInt()){
		         V[i++] = sc_V.nextInt();
		      }
		      sc_V.close();
		      
		      
		      // read file liquid
		      Scanner sc_liquid = new Scanner(new File(filePath+"/src/planningoptimization115657k62/daohoainam/liquid.txt"));
		      int j = 0;
		      while(sc_liquid.hasNextInt()){
		         liquid[j++] = sc_liquid.nextInt();
		      }
		      sc_liquid.close();
		      
		      
		      // read file conflict
		      ArrayList<Integer> t_0 = new ArrayList<Integer>();
		      t_0.add(0);
		      t_0.add(1);
		      ArrayList<Integer> t_1 = new ArrayList<Integer>();
		      t_1.add(7);
		      t_1.add(8);
		      ArrayList<Integer> t_2 = new ArrayList<Integer>();
		      t_2.add(12);
		      t_2.add(17);
		      ArrayList<Integer> t_3 = new ArrayList<Integer>();
		      t_3.add(8);
		      t_3.add(9);
		      ArrayList<Integer> t_4 = new ArrayList<Integer>();
		      t_4.add(1);
		      t_4.add(2);
		      t_4.add(9);
		      ArrayList<Integer> t_5 = new ArrayList<Integer>();
		      t_5.add(0);
		      t_5.add(9);
		      t_5.add(12);
		      
		      conflict = new ArrayList<ArrayList<Integer>>();
		      
		      conflict.add(t_0);
		      conflict.add(t_1);
		      conflict.add(t_2);
		      conflict.add(t_3);
		      conflict.add(t_4);
		      conflict.add(t_5);
		      
		      
		      
		     
		}

		public void test() {
			for(int i = 0; i < columns; i++) {
				System.out.print(liquid[i] + " ");
				
			}
			
			System.out.println();
			
			for(int i = 0; i < rows; i++) {
				System.out.print(V[i] + " ");
				
			}
			
		}
		
		public void get_max() {
			int t = -1;
			for(int i = 0; i < numLiquid; i++) {
				if(t < liquid[i]) {
					t = liquid[i];
				}
			}
			bound_max = t;
		
			System.out.println(bound_max);
		}
		
		public void creatConstraint() {
			matrix = new VarIntLS[rows][columns];
			
			// make constraint one liquid must in one bucket
			// in other way sum in a row always = 1
			
			for(int i = 0; i < rows; i++) {
				for(int k = 0; k < columns; k++) {
					matrix[i][k] = new VarIntLS(mgr, 0, 1);
				}
			}
			
			for(int i = 0; i < rows; i++) {
				for(int k = 0; k < rows; k++) {
					for(int j = 0; j < columns; j++) {
						if(i != k)
							S.post(new Implicate(new IsEqual(matrix[i][j], 1), 
									new IsEqual(matrix[k][j], 0)));
					}
				}
			}	
			
			
			temp = new VarIntLS[columns][rows];
			
			for(int i = 0; i < columns; i++) {
				for(int j = 0; j < rows; j++) {
					temp[i][j] = new VarIntLS(mgr, 0, 1);
				}
			}
				
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < columns; j++) {
					S.post(new IsEqual(temp[j][i], 0));
				}
				
			
			}
			
		
			for(int j = 0; j < columns; j++) {
				S.post(new IsEqual(new Sum(temp[j]), 1));
			}
		
			// no conflict if some liquid in one bucket
			for(int i = 0; i < rows; i++) {
					for(int k = 0; k < conflict.size(); k++) {
						ArrayList<Integer> t = conflict.get(k);
						
						int t_size = t.size();
						VarIntLS element_[] = new  VarIntLS[t_size];
						for(int j= 0; j < t_size; j++)
							element_[j] = new VarIntLS(mgr, 0, 1);
						
						for(int s = 0; s < t_size; s++) {
							S.post(new IsEqual(new FuncMult(matrix[i][t.get(s)], 1), element_[s]));
						}
						
						S.post(new LessOrEqual(new Sum(element_), t_size-1));
					}
			}
			
			// one bucket have at most V[i]	
			VarIntLS[][] element = new VarIntLS[rows][columns];
			for(int i = 0; i < rows; i++)
				for(int j = 0; j < columns; j++) {
					element[i][j] = new VarIntLS(mgr, 0, bound_max);
				}
			
			for(int i = 0; i < rows; i++) {
				for(int j = 0; j < columns; j++) {
					S.post(new IsEqual(new FuncMult(matrix[i][j], liquid[j]),element[i][j]));
				}
				S.post(new LessOrEqual(new Sum(element[i]),  V[i]));
			}
		}
		
		public void Solve() {
			mgr.close();
			HillClimbingSearch searcher = new HillClimbingSearch();
			searcher.hillClimbing(S, 10000);
	
			for(int i = 0; i < rows; i++) {
				System.out.println();
				for(int j = 0; j < columns; j++) {
					System.out.print(matrix[i][j].getValue() + " ");
				}
			}
		}
			
		public static void main(String args[]) {
			Assingment_1_openCBLS assingment_1_openCBLS = new Assingment_1_openCBLS();
			try {
				assingment_1_openCBLS.create();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			assingment_1_openCBLS.test();
			assingment_1_openCBLS.get_max();
			assingment_1_openCBLS.creatConstraint();
			assingment_1_openCBLS.Solve();
			
			
		}

}
