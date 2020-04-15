package planningoptimization115657k62.KieuMinhHieu;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class BinPacking2D {
	int N = 3; //number of packages
	int W = 4;
	int H = 6;
	int[] w = { 3, 3, 1};
	int[] h = { 2, 4, 6};

	public void solver() {
		Model model = new Model("BinTracking");
		IntVar[][] p = new IntVar[N][3];

		for (int j = 0; j < N; j++) {
			p[j][2] = model.intVar("o[" + j + "]", 0, 1);//xoay hay khong xoay
			p[j][0] = model.intVar("x[" + j + "]", 0, W);
			p[j][1] = model.intVar("y[" + j + "]", 0, H);

			model.ifThen(model.arithm(p[j][2], "=", 0), model.and(model.arithm(p[j][0], "<=", W - w[j]), model.arithm(p[j][1], "<=", H - h[j])));
			model.ifThen(model.arithm(p[j][2], "=", 1), model.and(model.arithm(p[j][0], "<=", W - h[j]), model.arithm(p[j][1], "<=", H - w[j])));
		}

		for (int i = 0; i < N - 1; i++) {
			for (int j = i + 1; j < N; j++) {
				model.ifThen(model.and(model.arithm(p[i][2], "=", 0), model.arithm(p[j][2], "=", 0)), model.or(model.arithm(p[j][0], "-", p[i][0], ">=", w[i]), model.arithm(p[i][0], "-", p[j][0], ">=", w[j]), model.arithm(p[j][1], "-", p[i][1], ">=", h[i]), model.arithm(p[i][1], "-", p[j][1], ">=", h[j])));
				model.ifThen(model.and(model.arithm(p[i][2], "=", 0), model.arithm(p[j][2], "=", 1)), model.or(model.arithm(p[j][0], "-", p[i][0], ">=", w[i]), model.arithm(p[i][0], "-", p[j][0], ">=", h[j]), model.arithm(p[j][1], "-", p[i][1], ">=", h[i]), model.arithm(p[i][1], "-", p[j][1], ">=", w[j])));
				model.ifThen(model.and(model.arithm(p[i][2], "=", 1), model.arithm(p[j][2], "=", 0)), model.or(model.arithm(p[j][0], "-", p[i][0], ">=", h[i]), model.arithm(p[i][0], "-", p[j][0], ">=", w[j]), model.arithm(p[j][1], "-", p[i][1], ">=", w[i]), model.arithm(p[i][1], "-", p[j][1], ">=", h[j])));
				model.ifThen(model.and(model.arithm(p[i][2], "=", 1), model.arithm(p[j][2], "=", 1)), model.or(model.arithm(p[j][0], "-", p[i][0], ">=", h[i]), model.arithm(p[i][0], "-", p[j][0], ">=", h[j]), model.arithm(p[j][1], "-", p[i][1], ">=", w[i]), model.arithm(p[i][1], "-", p[j][1], ">=", w[j])));
			}
		}
		int j = 0;
		while (model.getSolver().solve()) {
			System.out.println("Solution " + j++ + " found : ");
			for (int i = 0; i < N; i++) {
				System.out.println(p[i][0].getValue()+", " + p[i][1].getValue() + ", " +p[i][2].getValue());
			}
        }
		System.out.println("sdfa");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BinPacking2D app = new BinPacking2D();
		app.solver();
	}

}