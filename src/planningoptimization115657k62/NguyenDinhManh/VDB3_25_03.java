import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class VDB3_25_03 {
	public void solve()
	{
		Model model = new Model("Example");
		IntVar[] x  = new IntVar[5];
		
		// Khai báo 5 biến từ 1 đén 5
		for(int i = 0; i < 5; i++) {
			x[i] = model.intVar("x" + i, 1, 5);
		}
		
		// ràng buộc x2 + 3 != x1
		model.arithm(model.intOffsetView(x[2], 3), "!=", x[1]).post();
		
		// x3 <= x4
		model.arithm(x[3], "<=", x[4]).post();
		
		//x0 + 1 = x2 + x3
		model.arithm(model.intOffsetView(x[0], 1), "=", x[2], "+", x[3] ).post();
		// x4 <= 3
		model.arithm(x[4], "<=", 3).post();
		//x1 + x4 = x7
		model.arithm(x[1], "+", x[4], "=", 7).post();
		// nếu x2 = 1 thì x4 != 2
		model.ifThen(model.arithm(x[2], "=", 1),model.arithm(x[4], "!=", 2) );


	    model.getSolver().solve();
	        for(int i=0; i<5; i++) {
	        	System.out.println("x[" + i + "]" + x[i].getValue());
	        }
	}

	public static void main(String[] args) {
		VDB3_25_03 app = new VDB3_25_03();
		app.solve();

	}
}
