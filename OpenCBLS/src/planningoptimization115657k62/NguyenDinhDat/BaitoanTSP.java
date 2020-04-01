import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver; 
import com.google.ortools.linearsolver.MPVariable;
public class BaitoanTSP {
	// ham muc tiêu tổng X(i,j)c(i,j) -> min
	// bài toán tìm đường đi 
	static { System.loadLibrary("jniortools"); } int N = 6; 
	int[][] c = { {0,4,2,5,6,8}, 
			      {2,0,5,2,7,9}, 
			      {1,2,0,6,3,1}, 
			      {7,5,8,0,3,5},
			      {1,2,4,3,0,6},
			      {3,8,5,4,7,5}
			      };
	double inf = java.lang.Double.POSITIVE_INFINITY;
	MPSolver solver;
	MPVariable[][] X;	
	public void solve(){ 
		if(N > 10){ System.out.println("N = 10 is too high to apply this solve method, use solveDynamicAddSubTourConstraint"); 
		return; 
		}
		solver = new MPSolver("TSP solver",MPSolver.OptimizationProblemType. valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
		X = new MPVariable[N][N]; 
		for(int i = 0; i < N; i++) 
			for(int j = 0; j < N; j++)  
				if(i != j) 
					X[i][j] = solver.makeIntVar(0, 1, "X[" + i + "," + j + "]");
			
	MPObjective obj = solver.objective();
	for(int i = 0; i < N; i++)
		for(int j = 0; j < N; j++)
			if(i != j){ 
				obj.setCoefficient(X[i][j], c[i][j]);
			}
	// flow constraint
	for(int i = 0; i < N; i++){ 
		// \sum X[i,j] = 1, \forall j \in {0,...,N-1}\{i} 
		MPConstraint fc1 = solver.makeConstraint(1,1); 
		for(int j = 0; j < N; j++) if(j != i){ 
			fc1.setCoefficient(X[i][j], 1);
		}
		// \sum X[j][i] = 1, \forall j\in {0,1,...,N-1}\{i} 
		MPConstraint fc2 = solver.makeConstraint(1,1);
		for(int j = 0; j < N; j++) if(j != i){ 
			fc2.setCoefficient(X[j][i], 1); 
		}

	}
	// sub-tour elimination constraints
	SubSetGenerator generator = new SubSetGenerator(N);
    HashSet<Integer> S = generator.first();
    while(S != null){
    	if(S.size() > 1 && S.size() < N){
    		MPConstraint sc = solver.makeConstraint(0,S.size() -1); 
    		for(int i: S){
    			for(int j: S)if(i != j){ 
    				sc.setCoefficient(X[i][j], 1); 
    			} 
    		} 
    	} 
    	S = generator.next();
    }
    final MPSolver.ResultStatus resultStatus = solver.solve();
    if (resultStatus != MPSolver.ResultStatus.OPTIMAL){ 
    	System.err.println("The problem does not have an optimal solution!"); 
    	return; 
    } 
    System.out.println("Problem solved in " + solver.wallTime() + " milliseconds"); 
    // The objective value of the solution.
    System.out.println("Optimal objective value = " +  solver.objective().value());
	} 
	public static void main(String[] args) { 
		BaitoanTSP app = new BaitoanTSP();
		app.solve(); 
	}

	
	
 

}
