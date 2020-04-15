package planningoptimization115657k62;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class MipExample{
  static {
    System.loadLibrary("jniortools");
  }

  public static void main(String[] args) throws Exception {
    
    // Create the linear solver with the CBC backend.
    MPSolver solver = new MPSolver(
        "SimpleMipProgram", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
    

    // [START variables]
    double infinity = java.lang.Double.POSITIVE_INFINITY;
    // x and y are integer non-negative variables.
    MPVariable x = solver.makeIntVar(0.0, infinity, "x");
    MPVariable y = solver.makeIntVar(0.0, infinity, "y");

    System.out.println("Number of variables = " + solver.numVariables());
    // [END variables]

    // [START constraints]
    // x + 4 * y <= 18.5.
    MPConstraint c0 = solver.makeConstraint(-infinity, 18.5, "c0");
    c0.setCoefficient(x, 1);
    c0.setCoefficient(y, 4);

    // x <= 3.5.
    MPConstraint c1 = solver.makeConstraint(-infinity, 3.5, "c1");
    c1.setCoefficient(x, 1);
    c1.setCoefficient(y, 0);

    // y <= 4.0.
    MPConstraint c2 = solver.makeConstraint(-infinity, 4.0, "c2");
    c1.setCoefficient(x, 0);
    c1.setCoefficient(y, 1);

    System.out.println("Number of constraints = " + solver.numConstraints());
    // [END constraints]

    // [START objective]
    // Maximize x + 3 * y.
    MPObjective objective = solver.objective();
    objective.setCoefficient(x, 1);
    objective.setCoefficient(y, 3);
    objective.setMaximization();
    // [END objective]

    // [START solve]
    final MPSolver.ResultStatus resultStatus = solver.solve();
    // [END solve]

    // [START print_solution]
    if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
      System.out.println("Solution:");
      System.out.println("Objective value = " + objective.value());
      System.out.println("x = " + x.solutionValue());
      System.out.println("y = " + y.solutionValue());
      // [END print_solution]

    } else {
      System.err.println("The problem does not have an optimal solution!");
    }
  }
}
// [END program]
