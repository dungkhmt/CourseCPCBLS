import java.util.ArrayList;
import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class TSPDynSEC {
	static {
	    System.loadLibrary("jniortools");
	  }

	  int N = 5;
	  int[][] c = {
	                {0,4,2,5,6},
	                {2,0,5,2,7},
	                {1,2,0,6,3},
	                {7,5,8,0,3},
	                {1,2,4,3,0}};
	  double inf = java.lang.Double.POSITIVE_INFINITY;
	  MPSolver solver;
	  MPVariable[][] X;
	  private int findNext(int s){
		    for(int i = 0; i < N; i++) if(i != s && X[s][i].solutionValue() > 0) return i;
		    return -1;
		  }
		  public ArrayList<Integer> extractCycle(int s){
		    ArrayList<Integer> L = new ArrayList<Integer>();
		    int x = s;
		    while(true){
		      L.add(x);    x = findNext(x);
		      int rep = -1;
		      for(int i = 0; i < L.size(); i++)if(L.get(i) == x){
		        rep = i; break; }
		      if(rep != -1){
		        ArrayList<Integer> rL = new ArrayList<Integer>();
		        for(int i = rep; i < L.size(); i++) rL.add(L.get(i));
		        return rL;
		      }
		    }
		    //return rL;
		  }
		  private void createVariables(){
			    solver = new MPSolver("TSP solver", 
			         MPSolver.OptimizationProblemType.valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
			    X = new MPVariable[N][N];
			    for(int i = 0; i < N; i++){
			      for(int j = 0; j < N; j++){
			        if(i != j){
			          X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "," + j + "]");
			        }
			      }
			    }
			  }
		  private void createObjective(){
			    MPObjective obj = solver.objective();
			    for(int i = 0; i < N; i++){
			      for(int j = 0; j < N; j++){
			        if(i != j){
			          obj.setCoefficient(X[i][j], c[i][j]);
			        }
			      }
			    }
			  }
		  
		  private void createFlowConstraint(){
			    // flow constraint
			    for(int i = 0; i < N; i++){
			      // \sum X[j][i] = 1, \forall j\in {0,1,...,N-1}\{i}
			      MPConstraint fc1 = solver.makeConstraint(1,1);
			      for(int j = 0; j < N; j++) if(j != i){
			        fc1.setCoefficient(X[j][i], 1);
			      }
			      // \sum X[i][j] = 1, \forall j\in {0,1,...,N-1}\{i}
			      MPConstraint fc2 = solver.makeConstraint(1,1);
			      for(int j = 0; j < N; j++) if(j != i){
			        fc2.setCoefficient(X[i][j], 1);
			      }
			    }
			  }
		private void createSEC(HashSet<ArrayList<Integer>> S){
			    for(ArrayList<Integer> C: S){
			      MPConstraint sc = solver.makeConstraint(0, C.size() -1);
			      for(int i: C){
			        for(int j : C) if(i != j){
			          sc.setCoefficient(X[i][j], 1);
			        }
			      }
			    }
			  }
		private void createSolverWithSEC(HashSet<ArrayList<Integer>> S){
			    createVariables();
			    createObjective();
			    createFlowConstraint();
			    createSEC(S);
			  }
        
		public void solveDynamicAddSubTourConstraint(){
		    HashSet<ArrayList<Integer>> S = new HashSet();
		    boolean[] mark = new boolean[N];
		    boolean found = false;
		    while(!found){
		      createSolverWithSEC(S);
		      final MPSolver.ResultStatus resultStatus = solver.solve();
		      if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
		        System.err.println("The problem does not have an optimal solution!");
		        return;
		      }
		      System.out.println("obj = " + solver.objective().value());
		      for(int i = 0; i < N; i++) mark[i] = false;
		      for(int s = 0; s < N; s++)if(!mark[s]){
		        ArrayList<Integer> C = extractCycle(s);
		        if(C.size() < N){// sub-tour detected
		          System.out.print("SubTour deteted, C = "); 
		          for(int i: C) System.out.print(i + " "); System.out.println();
		          S.add(C);
		          for(int i: C) mark[i] = true;
		        }else{
		          System.out.println("Global tour detected, solution found!!!");
		          found = true; break;   
		        }
		      }
		    }
		    ArrayList<Integer> tour = extractCycle(0);
		    for(int i = 0; i < tour.size(); i++) System.out.print(tour.get(i) + " -> ");   
		    System.out.println(tour.get(0));
		}
		
	    public static void main(String[] args) {
			    TSPDynSEC app = new TSPDynSEC();
			    app.solveDynamicAddSubTourConstraint();
	}

		
		  
		  
		  
}
