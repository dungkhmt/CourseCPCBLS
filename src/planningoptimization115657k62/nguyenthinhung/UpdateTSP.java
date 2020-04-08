package planningoptimization115657k62.nguyenthinhung;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPVariable;

public class UpdateTSP {
	static {
		System.loadLibrary("jniortools");
	}
	int n = 5;
	int [][] c = new int[][] {{0,4,2,5,6},
							  {2,0,5,2,7},
							  {1,2,0,6,3},
		  				      {7,5,8,0,3},
							  {1,2,4,3,0}};
	double inf = java.lang.Double.POSITIVE_INFINITY;
	MPSolver solver;
	MPVariable[][] x;
	
	private int findNext(int s){
	    for(int i = 0; i < n; i++) if(i != s && x[s][i].solutionValue() > 0) return i;
	    return -1;
	 }
	
	public ArrayList<Integer> extractCycle(int s){
	    ArrayList<Integer> L = new ArrayList<Integer>();
	    int xi = s;
	    while(true){
	    	L.add(xi);
	    	xi = findNext(xi);
	    	int rep = -1;
	    	for(int i = 0; i < L.size(); i++)if(L.get(i) == xi){
	    		rep = i; break; 
	    	}
	    	if(rep != -1){
	    		ArrayList<Integer> rL = new ArrayList<Integer>();
	    		for(int i = rep; i < L.size(); i++)
	    			rL.add(L.get(i));
	    		return rL;
	      }
	    }
	}
	
	  private void createVariables(){
		  solver = new MPSolver("UpdataTSP", MPSolver.OptimizationProblemType.valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
		  x = new MPVariable[n][n];
		  for(int i = 0; i < n; i++){
			  for(int j = 0; j < n; j++){
				  if(i != j){
					  x[i][j] = solver.makeIntVar(0, 1, "x[" + i + "," + j + "]");
				  }
		      }
		   }
	  }
	  
	  private void createObjective(){
		  MPObjective obj = solver.objective();
		  for(int i = 0; i < n; i++){
			  for(int j = 0; j < n; j++){
				  if(i != j){
					  obj.setCoefficient(x[i][j], c[i][j]);
				  }
		      }
		  }
	  }
	  
	  public void createConstraint() {
			solver = new MPSolver("UpdateTSP", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
			x = new MPVariable[n][n];
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i != j) {
						x[i][j] = solver.makeIntVar(0, 1, "x["+i+"]["+j+"]");
					}
				}
			}
									
			for (int i = 0; i < n; i++) {
				MPConstraint c = solver.makeConstraint(1,1);
				for (int j = 0; j < n; j++) {
					if (i != j) {
						c.setCoefficient(x[i][j], 1);
					}
				}
			}
			for (int i = 0; i < n; i++) {
				MPConstraint c = solver.makeConstraint(1,1);
				for (int j = 0; j < n; j++) { 
					if (i != j) {
						c.setCoefficient(x[j][i], 1);
					}
				}
			}
			MPObjective obj = solver.objective();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
					if (i != j) {
						obj.setCoefficient(x[i][j], c[i][j]);
					}				
				}			
			}
			obj.setMinimization();
		}
	  
	  private void createSEC(HashSet<ArrayList<Integer>> S){
		    for(ArrayList<Integer> C: S){
		      MPConstraint sc = solver.makeConstraint(0, C.size() -1);
		      for(int i: C){
		        for(int j : C) if(i != j){
		          sc.setCoefficient(x[i][j], 1);
		        }
		      }
		    }
		  }

	  
	  private void createSolverWithSEC(HashSet<ArrayList<Integer>> S){
		    createVariables();
		    createObjective();
		    createConstraint();
		    createSEC(S);
		 }
	  /*
	  public void readData() throws IOException {
			File f = new File("C:\\Users\\HP\\eclipse-workspace\\Optimmization\\tsp"); // n = 50
			Scanner file = new Scanner(f);
			n = file.nextInt();
			for (int i = 0; i < n; i++) {
				for (int j = 0; j < n; j++) {
						c[i][j] = file.nextInt();
				}
			}
			file.close();
		}
		
		*/
	  public void solveDynamicAddSubTourConstraint() {
		  	// readData();
		    HashSet<ArrayList<Integer>> S = new HashSet();
		    boolean[] mark = new boolean[n];
		    boolean found = false;
		    while(!found){
				  createSolverWithSEC(S);
				  final MPSolver.ResultStatus resultStatus = solver.solve();
				  if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
					  System.err.println("Not have optimal solution.");
					  return;
				  }
				  System.out.println("obj = " + solver.objective().value());
				  for(int i = 0; i < n; i++) mark[i] = false;
				  for(int s = 0; s < n; s++)if(!mark[s]){
					  ArrayList<Integer> C = extractCycle(s);
				    //dectect subtour
					  if(C.size() < n){
						  System.out.print("SubTour deteted. C = "); 
						  for(int i: C) System.out.print(i + " ");
						  System.out.println();
						  S.add(C);
						  for(int i: C) mark[i] = true;
					  }else{
						  System.out.println("solution found.");
						      found = true;
						      break;   
						    }
				  }
		    }	
		    ArrayList<Integer> tour = extractCycle(0);
		    for(int i = 0; i < tour.size(); i++) 
		    	System.out.print(tour.get(i) + " -> ");   
		    System.out.println(tour.get(0));
	  }
	  
	  public static void main(String[] args) {
		    UpdateTSP app = new UpdateTSP();
		    app.solveDynamicAddSubTourConstraint();
		  }
	  
}
