package planningoptimization115657k62.hoangmanhhung;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
public class TSP {
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
  
  public void solve(){
	  if(N > 10){	
		  System.out.println("N = 10 is too high to apply this solve method, use solveDynamicAddSubTourConstraint"); 
		  return;
	  }
	  solver = new MPSolver("TSP solver",MPSolver.OptimizationProblemType.valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
	  X = new MPVariable[N][N];
	  for(int i = 0; i < N; i++)
		  for(int j = 0; j < N; j++)  if(i != j)
			  X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "," + j + "]");
    
	  MPObjective obj = solver.objective();
	  for(int i = 0; i < N; i++)
		  for(int j = 0; j < N; j++) if(i != j)
			  obj.setCoefficient(X[i][j], c[i][j]);
	  // flow constraint
	  for(int i = 0; i < N; i++){
		  MPConstraint fc1 = solver.makeConstraint(1,1);
		  for(int j = 0; j < N; j++) if(j != i){
			  fc1.setCoefficient(X[i][j], 1);
		  }
		  MPConstraint fc2 = solver.makeConstraint(1,1);
		  for(int j = 0; j < N; j++) if(j != i){
			  fc2.setCoefficient(X[j][i], 1);
		  }
	  }

	  final MPSolver.ResultStatus resultStatus = solver.solve();
	  if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
		  System.err.println("The problem does not have an optimal solution!");
		  return;
	  }
	  System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
	  // The objective value of the solution.
	  System.out.println("Optimal objective value = " +  solver.objective().value());   
  }
  
  public static void main(String[] args) {
	  TSP tsp = new TSP();
	  tsp.solve();
  }
}
