package planningoptimization115657k62.hoangmanhhung;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

/**
 * Linear programming example that shows how to use the API.
 *
 */

public class Test {
  static {
    System.loadLibrary("jniortools");
  }

  private static MPSolver createSolver(String solverType) {
    try {
      return new MPSolver(
          "LinearProgrammingExample", MPSolver.OptimizationProblemType.valueOf(solverType));
    } catch (java.lang.IllegalArgumentException e) {
      return null;
    }
  }

  private static void runExample(String solverType, boolean printModel) {
    MPSolver solver = createSolver(solverType);
    if (solver == null) {
      System.out.println("Could not create solver " + solverType);
      return;
    }
    double infinity = java.lang.Double.POSITIVE_INFINITY;
    // x1, x2 and x3 are continuous non-negative variables.
    MPVariable x1 = solver.makeNumVar(0, 14, "x1");
    MPVariable x2 = solver.makeIntVar(0, 20, "x2");

    // Maximize x1 + x2
    MPObjective objective = solver.objective();
    objective.setCoefficient(x1, 1);
    objective.setCoefficient(x2, 1);
    objective.setMaximization();

    // x1 - 10x2 <=7
    MPConstraint c0 = solver.makeConstraint(-infinity, 7.0);
    c0.setCoefficient(x1, 1);
    c0.setCoefficient(x2, -10);

    // 2x1+3x2<=20
    MPConstraint c1 = solver.makeConstraint(-infinity, 20.0);
    c1.setCoefficient(x1, 2);
    c1.setCoefficient(x2, 3);



    System.out.println("Number of variables = " + solver.numVariables());
    System.out.println("Number of constraints = " + solver.numConstraints());

    if (printModel) {
      String model = solver.exportModelAsLpFormat();
      System.out.println(model);
    }

    final MPSolver.ResultStatus resultStatus = solver.solve();

    // Check that the problem has an optimal solution.
    if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
      System.err.println("The problem does not have an optimal solution!");
      return;
    }

    // Verify that the solution satisfies all constraints (when using solvers
    // others than GLOP_LINEAR_PROGRAMMING, this is highly recommended!).
    if (!solver.verifySolution(/*tolerance=*/1e-7, /* log_errors= */ true)) {
      System.err.println("The solution returned by the solver violated the"
          + " problem constraints by at least 1e-7");
      return;
    }

    System.out.println("Problem solved in " + solver.wallTime() + " milliseconds");
    // The objective value of the solution.
    System.out.println("Optimal objective value = " + solver.objective().value());

    // The value of each variable in the solution.
    System.out.println("x1 = " + x1.solutionValue());
    System.out.println("x2 = " + x2.solutionValue());
  }

  public static void main(String[] args) throws Exception {
    System.out.println("---- Mixed integer programming example ----");
    runExample("CBC_MIXED_INTEGER_PROGRAMMING", true);
  }
}

