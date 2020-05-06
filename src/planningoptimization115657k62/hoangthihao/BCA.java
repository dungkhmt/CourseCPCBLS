
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class BCA {
	static {
		System.loadLibrary("jniortools");
	}
	// input data structures
	int M = 3; // so giao vien 0,1,...,M-1
	int N = 13; // so mon hoc 0, 1, ..., N-1
	int[][] teachClass = {{ 1, 0, 1, 0, 1, 0, 0, 0, 1 ,0, 1, 0, 0 },
						  { 1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0 },
						  { 0, 1, 1, 1, 0, 0, 0, 1 ,0, 1 ,0, 1, 1 } };
	int[] credits = {3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4};
	int[][] conflict = { {  0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0 }, 
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
	
	public void solver() {
		MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		MPVariable[][] x = new MPVariable[N][M];
		
		// Ma tran X có kích thước NxM 
		for (int i = 0; i < N; i++)
			for (int j = 0; j < M; j++)
				x[i][j] = solver.makeIntVar(0, 1, "x[" + i + "," + j + "]");
		MPVariable[] load = new MPVariable[M]; // khoi lượng tín chỉ của mỗi gv 
		int totalCredits = 0; // tổng số tín chỉ của tất cả các môn học 
		for (int i = 0; i < credits.length; i++)
			totalCredits += credits[i];
		for (int i = 0; i < M; i++)
			load[i] = solver.makeIntVar(0, totalCredits, "load[" + i + "]");
		
		MPVariable y = solver.makeIntVar(0, totalCredits, "y"); // hàm mục tiêu 
		
		for (int i = 0; i < N; i++)
			for (int j = 0; j < M; j++)
				if(teachClass[j][i] == 0) {			// Nếu gv j không dạy lớp i thì x[i][j]=0 
					MPConstraint c1 = solver.makeConstraint(0, 0);
					c1.setCoefficient(x[i][j], 1);
				}
		
		for (int i = 0; i < N; i++)
			for (int j = 0; j < N; j++)
				if (conflict[i][j] == 1) {	// Nếu 2 lớp trùng giờ thì tổng các cột k của ma trận X bằng 1 
					for (int k = 0; k < M; k++) {
						MPConstraint c2 = solver.makeConstraint(0, 1);
						c2.setCoefficient(x[i][k], 1);
						c2.setCoefficient(x[j][k], 1);
						
					}
				}
		
		for (int i = 0; i < N; i++) {
			MPConstraint c3 = solver.makeConstraint(1, 1); // Tổng mỗi  cột j của ma trận X bằng 1    
			for (int j = 0; j < M; j++)
				c3.setCoefficient(x[i][j], 1);
		}
		for (int i = 0; i < M; i++) {
			MPConstraint c4 = solver.makeConstraint(0, 0);
			for (int j = 0; j < N; j++)
				c4.setCoefficient(x[j][i], credits[j]);
			c4.setCoefficient(load[i], -1);
		}
		for (int i = 0; i < M; i++) {
			MPConstraint c5 = solver.makeConstraint(0, totalCredits);
			c5.setCoefficient(load[i], -1);
			c5.setCoefficient(y, 1);
		}
		MPObjective obj = solver.objective();
		obj.setCoefficient(y, 1);
		obj.setMinimization();
		MPSolver.ResultStatus rs = solver.solve();
		if (rs != MPSolver.ResultStatus.OPTIMAL) {
			System.out.println("Cannot find optimal solution");
		} else {
			System.out.println("obj = " + obj.value());
			for (int i = 0; i < M; i++) {
				System.out.print("teacher " + i + ": ");
				for (int j = 0; j < N; j++)
					if (x[j][i].solutionValue() == 1)
						System.out.print(j + " ");
				System.out.println(", load = " + load[i].solutionValue());
			}
		}
	} 
	
	public static void main(String[] args) {
		BCA app = new BCA();
		app.solver();
	}
	
}
