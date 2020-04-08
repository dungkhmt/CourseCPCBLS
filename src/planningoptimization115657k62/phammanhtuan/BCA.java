package planningoptimization115657k62.phammanhtuan;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolverResponseStatus;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public class BCA {
   static {
	   System.loadLibrary("jniortools");
   }
   public static void main(String[] args) {
	   int N=13,M=3;
	   int[][] teachClass = {
		   {1,0,1,0,1,0,0,0,1,0,1,0,0},
		   {1,1,0,1,0,1,1,1,1,0,0,0,0},
		   {0,1,1,1,0,0,0,1,0,1,0,1,1},
	   };
	   int[] credits = {3,3,4,3,4,3,3,3,4,3,3,4,4};
	   int[][] conflict = { { 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0 },
			   { 1, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0 },
			   { 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			   { 0, 0, 0, 0, 0, 0, 0, 1, 0, 1, 0, 0, 0 },
			   { 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			   { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1 },
			   { 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1 },
			   { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			   { 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 },
			   { 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			   { 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 },
			   { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0 },
			   { 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0 } };
	   final double INF = java.lang.Double.POSITIVE_INFINITY;
	   MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
	   MPVariable[][] X = new MPVariable[M][N];
	   for (int i=0;i<M;i++)
	   {
		   for (int j=0;j<N;j++) X[i][j]=solver.makeIntVar(0, 1, "X["+i+"]["+j+"]");
	   }
	   int totalcredits=0;
	   for (int i=0;i<N;i++) totalcredits+=credits[i];
	   MPVariable[] load = new MPVariable[M];
	   for (int i=0;i<M;i++) {
		   load[i]=solver.makeIntVar(0, totalcredits, "load["+i+"]");
	   }
	   MPVariable y = solver.makeIntVar(0, totalcredits, "y");
	   for (int i=0;i<M;i++)
		   for (int j=0;j<N;j++) {
			   if (teachClass[i][j]==0) {
				   MPConstraint c = solver.makeConstraint(0,0);
				   c.setCoefficient(X[i][j], 1);
			   }
		   }
	   for (int i=0;i<N;i++)
		   for (int j=0;j<N;j++) {
			   if (conflict[i][j]==1) {
				   for (int k=0;k<M;k++) {
					   MPConstraint c = solver.makeConstraint(0,1);
					   c.setCoefficient(X[k][i], 1);
					   c.setCoefficient(X[k][j], 1);
				   }
			   }
		   }
	   for (int i=0;i<N;i++) {
		   MPConstraint c = solver.makeConstraint(1,1);
		   for (int j=0;j<M;j++) c.setCoefficient(X[j][i], 1);
	   }
	   for (int i=0;i<M;i++) {
		   MPConstraint c = solver.makeConstraint(0,0);
		   for (int j=0;j<N;j++) c.setCoefficient(X[i][j], credits[j]);
		   c.setCoefficient(load[i], -1);
	   }
	   for (int i=0;i<M;i++) {
		   MPConstraint c = solver.makeConstraint(0,totalcredits);
		   c.setCoefficient(load[i], -1);
		   c.setCoefficient(y, 1);
	   }
	   MPObjective obj = solver.objective();
	   obj.setCoefficient(y, 1);
	   obj.setMinimization();
	   
	   MPSolver.ResultStatus rs = solver.solve();
	   if (rs != MPSolver.ResultStatus.OPTIMAL) {
		   System.out.println("Cannot find optimal solution");
	   } else {
		   System.out.println("Objective value: "+obj.value());
		   for (int i = 0; i < M; i++) {
			   System.out.print ("teacher " + i + ":");
			   for (int j = 0; j < N;j++) {
			       if (X[i][j].solutionValue() ==1) {
			           System.out.print (j + " ");
			       }
	           }
			   System.out.println();
		   }
       }
   }
}
