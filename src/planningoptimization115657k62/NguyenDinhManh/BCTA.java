import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPSolverResponseStatus;
import com.google.ortools.linearsolver.MPVariable;

public class BCTA {
	static {
		System.loadLibrary("jniortools");
	}
	//input data
	int m = 3; //so giao vien
	int n = 13; // so lop hoc
	int [][] teachClass = {{1, 0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 0},
							{1, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0},
							{0, 1, 1, 1, 0, 0, 0, 1, 0, 1, 0, 1, 1}};
	
	int[] credits = {3, 3, 4, 3, 4, 3, 3, 3, 4, 3, 3, 4, 4};
						//0  1  2 3   4  5  6  7  8  9  10 11 12
	int [][] conflict = {{0, 0, 1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0},
						{0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0},
						{0, 0, 1, 0, 1, 0, 0, 1, 0, 1, 0, 0, 0},
						{1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1},
						{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 12},
						{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						{0, 0, 1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
						{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
						{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
						{0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
						{0, 0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0}};
	
	public void solve() {
		MPSolver solver = new MPSolver("BCA", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		MPVariable [][] x = new MPVariable[n][m];
		
		// Tạo các biến xij nếu gv j dạy môn i
		for(int i = 0; i < n; i++)
			for(int j = 0; j< m; j++)
				x[i][j] = solver.makeIntVar(0,  1, "x[" + i + "," + j + "]");
		
		MPVariable[] load = new MPVariable[m];
		int totalCredits = 0;
		for(int i = 0; i < credits.length; i++)
			totalCredits += credits[i];
		for(int i = 0; i < m; i++)
			load[i] = solver.makeIntVar(0, totalCredits, "load[" + i + "]");
		
		
		//so tin chi cho moi giao viên 0 < y < tổng số tín chỉ
		MPVariable y = solver.makeIntVar(0, totalCredits, "y");
		
		
		// tạo ràng buộc
		// 1 giáo viên có thể dạy 1 môn học hay không
		for(int i = 0; i < n; i++)
			for(int j = 0; j < m ; i++)
				if(teachClass[j][i] == 0) { //giao vien j ko dạy môn i xij = 0
					MPConstraint c = solver.makeConstraint(0, 0); // ngăn câm gán môn i vào gv j
					c.setCoefficient(x[i][j], 1);;
				
				}
		
		// 2 môn trùng thơi khóa biểu 0 < xjk + xjk < 1
		for(int i = 0; i < n; i++)
			for(int j = 0; j< n; j++)
				if(conflict[i][j] == 1) {
					for(int k = 0; k < m; k++) {
						MPConstraint c = solver.makeConstraint(0, 1);
						c.setCoefficient(x[i][k], 1);
						c.setCoefficient(x[j][k], 1);
					}
				}
		
		
		// ràng buộc với mỗi môn chỉ gán cho 1 giáo viên: duyệt qua các môn, thiết lập ràng buoc xij = 1
		for(int i = 0; i < n; i++) {
			MPConstraint c = solver.makeConstraint(1, 1);
			for(int j = 0; j < m; j++)
				c.setCoefficient(x[i][j], 1);
		}
		
		// tổng số tín gán cho mỗi gv phải nhỏ hơn y  ci * xij < loadi
		for(int i = 0; i < m; i++) {
			MPConstraint c = solver.makeConstraint(0, 0);
			for(int j = 0; j < n; j++)
				c.setCoefficient(x[j][i], credits[j]);
			c.setCoefficient(load[i], -1);
		}
		
		// 0< y - loadi < total
		for(int i = 0; i < m; i++) {
			MPConstraint c = solver.makeConstraint(0, totalCredits);
			c.setCoefficient(load[i], -1);
			c.setCoefficient(y, 1);
		}
		
		MPObjective obj = solver.objective();
		obj.setCoefficient(y, 1);
		obj.setMinimization();
		ResultStatus rs = solver.solve();
		if(rs != ResultStatus.OPTIMAL) {
			System.out.println("cannot find optimal solution");
			
		}else {
			System.out.println("obj = " + obj.value());
			for(int i = 0; i< m; i++) {
				System.out.println("teach " + i + " :");
				for(int j = 0; j < n;j++)
					if(x[j][i].solutionValue() == 1)
						System.out.println(j + " ");
				System.out.println(", load = " + load[i].solutionValue());
			}
		}
	}
	
	public static void main(String[] args) {
		BCTA bt = new BCTA();
		bt.solve();

	}
	
	
}
