import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;
public class BindPack2D {
	public static void Bind2D() {
		int N = 3;
		int H = 6;
		int W = 4;
		int[] w = {3, 3, 1};
		int[] h = {2, 4, 6};
		Model model = new Model("DbindPack");
		IntVar[] x = new IntVar[N];
		IntVar[] y = new IntVar[N];
		IntVar[] o = new IntVar[N];
		
		for(int i = 0; i < N; i++) {
			x[i] = model.intVar("x[" + i+ "]", 0, W);
			y[i] = model.intVar("y[" + i+ "]", 0, H);
			o[i] = model.intVar("o[" + i+ "]", 0, 1);
		}
		for(int i = 0; i < N; i++){
			Constraint c1 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", W);
			Constraint c2 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", H);
			model.ifThen(model.arithm(o[i],"=", 0), model.and(c1,c2));
			
			Constraint c3 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", W);
			Constraint c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", H);
			model.ifThen(model.arithm(o[i],"=", 1), model.and(c3,c4));
		}
		
		for(int i = 0; i < N; i++)
			for(int j = i+1; j < N; j++){
				Constraint o00 = model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 0));
				Constraint o01 = model.and(model.arithm(o[i], "=", 0),model.arithm(o[j], "=", 1));
				Constraint o10 = model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 0));
				Constraint o11 = model.and(model.arithm(o[i], "=", 1),model.arithm(o[j], "=", 1));
				
				Constraint c01 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				Constraint c02 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				Constraint c03 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
				Constraint c04 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
				
				model.ifThen(o00, model.or(model.or(c01, c02), model.or(c03,c04)));
				
				
				Constraint c11 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				Constraint c12 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				Constraint c13 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
				Constraint c14 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				
				model.ifThen(o01, model.or(model.or(c11, c12), model.or(c13,c14)));
				
				
				Constraint c21 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				Constraint c22 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				Constraint c23 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
				Constraint c24 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
				
				model.ifThen(o10, model.or(model.or(c21, c22), model.or(c23,c24)));
				
				
				Constraint c31 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				Constraint c32 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				Constraint c33 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
				Constraint c34 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				
				model.ifThen(o11, model.or(model.or(c31, c32), model.or(c33,c34)));
		}
		model.getSolver().solve();
		for(int i = 0; i < N; i++) {
			System.out.print("("+x[i]+"," +y[i]+")" +o[i]+"\n");
		}
	}
	public static void main(String[] args) {
		Bind2D();
	}
}
