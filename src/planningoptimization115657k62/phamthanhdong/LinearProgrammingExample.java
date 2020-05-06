package planningoptimization115657k62.phamthanhdong;

//chuong trinh minh hoa voi cac giang buoc
/*
 x + 2*y <= 14
 x - 10*y >= 7
 2*x + y <= 20
*/
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

/** Simple linear programming example.*/
public class LinearProgrammingExample {
  static {
    System.loadLibrary("jniortools");
  }

  public static void main(String[] args) throws Exception {
    // [START solver]
    MPSolver solver = new MPSolver(
        "LinearProgrammingExample", MPSolver.OptimizationProblemType.GLOP_LINEAR_PROGRAMMING);
    // [END solver]

    // [START variables]
    double infinity = java.lang.Double.POSITIVE_INFINITY;
    // x and y are continuous non-negative variables.
    MPVariable x1 = solver.makeNumVar(0.0, 14, "x1");
    MPVariable x2 = solver.makeNumVar(0.0, 20, "x2");
    System.out.println("Number of variables = " + solver.numVariables());
   

    // [START constraints]
    // x + 2*y <= 14.
    MPConstraint c0 = solver.makeConstraint(-infinity, 14.0, "c0");
    c0.setCoefficient(x1, 1);
    c0.setCoefficient(x2, 2);

    // x - 10*y >= 7.
    //MPConstraint c1 = solver.makeConstraint(0.0, infinity, "c1");
    MPConstraint c1 = solver.makeConstraint(-infinity, 7);//giá trị vô cùng
    c1.setCoefficient(x1, 1);
    c1.setCoefficient(x2, -10);

    // 2*x + y <= 20.
    MPConstraint c2 = solver.makeConstraint(0, 20);
    c2.setCoefficient(x1, 2);
    c2.setCoefficient(x2, 1);
    System.out.println("Number of constraints = " + solver.numConstraints());
  

    // [START objective]
    // Maximize 3 * x + 4 * y.
    MPObjective objective = solver.objective();
    objective.setCoefficient(x1, 3);
    objective.setCoefficient(x2, 4);
    objective.setMaximization();
    
    //START solve
    final MPSolver.ResultStatus resultStatus = solver.solve();
    // Check that the problem has an optimal solution.
    if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
      System.err.println("The problem does not have an optimal solution! - Khong co giai phap !");
      return;
    }
    

    // [START print_solution]
    // The value of each variable in the solution.
    System.out.println("Solution");
    System.out.println("x1 = " + x1.solutionValue());
    System.out.println("x2 = " + x2.solutionValue());

    // The objective value of the solution.
    System.out.println("Optimal objective value = " + solver.objective().value());
    // [END print_solution]
  }
}
// [END program]
