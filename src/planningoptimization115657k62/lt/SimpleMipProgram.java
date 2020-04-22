package planning_optimization_lam.lt;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

/** Minimal Mixed Integer Programming example to showcase calling the solver. */
public class SimpleMipProgram {
  static {
    System.loadLibrary("jniortools");
  }

  public static void main(String[] args) throws Exception {
    // Create the linear solver with the CBC backend.
    MPSolver solver = new MPSolver(
        "SimpleMipProgram", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);

    double infinity = java.lang.Double.POSITIVE_INFINITY;
    // x and y are integer non-negative variables.
    MPVariable x = solver.makeIntVar(0.0, infinity, "x");
    MPVariable y = solver.makeIntVar(0.0, infinity, "y");

    System.out.println("Number of variables = " + solver.numVariables());

    // x + 7 * y <= 17.5.
    MPConstraint c0 = solver.makeConstraint(-infinity, 17.5, "c0");
    c0.setCoefficient(x, 1);
    c0.setCoefficient(y, 7);

    // x <= 3.5.
    MPConstraint c1 = solver.makeConstraint(-infinity, 3.5, "c1");
    c1.setCoefficient(x, 1);
    c1.setCoefficient(y, 0);

    System.out.println("Number of constraints = " + solver.numConstraints());

    // Maximize x + 10 * y.
    MPObjective objective = solver.objective();
    objective.setCoefficient(x, 1);
    objective.setCoefficient(y, 10);
    objective.setMaximization();

    final MPSolver.ResultStatus resultStatus = solver.solve();

    if (resultStatus == MPSolver.ResultStatus.OPTIMAL) {
      System.out.println("Solution:");
      System.out.println("Objective value = " + objective.value());
      System.out.println("x = " + x.solutionValue());
      System.out.println("y = " + y.solutionValue());

      System.out.println("\nAdvanced usage:");
      System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
      System.out.println("Problem solved in " + solver.iterations() + " iterations");
      System.out.println("Problem solved in " + solver.nodes() + " branch-and-bound nodes");
    } else {
      System.err.println("The problem does not have an optimal solution!");
    }
  }
}