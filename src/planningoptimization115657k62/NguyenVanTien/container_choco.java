package planningoptimization115657k62.NguyenVanTien;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class container_choco {

	int N = 6;
	int L = 6;
	int W = 4;
	int[] w = { 1, 3, 2, 3, 1, 2 };
	int[] l = { 4, 1, 2, 1, 4, 3 };

	public void solve() {
		Model model = new Model("container");
		IntVar[][] p = new IntVar[N][3];
		IntVar[] position = new IntVar[N];
		
		for (int j = 0; j < N; j++) {
			position[j] = model.intVar("position[" + j + "]",0,L);
			p[j][2] = model.intVar("o[" + j + "]", 0, 1);//xoay hay khong xoay
			p[j][0] = model.intVar("x[" + j + "]", 0, W);
			p[j][1] = model.intVar("y[" + j + "]", 0, L);
			
			model.ifThen(model.arithm(p[j][2], "=", 0), model.and(model.arithm(p[j][0], "<=", W - w[j]), model.arithm(p[j][1], "<=", L - l[j])));
			model.ifThen(model.arithm(p[j][2], "=", 1), model.and(model.arithm(p[j][0], "<=", W - l[j]), model.arithm(p[j][1], "<=", L - w[j])));
		}
		
		for (int i = 0; i < N - 1; i++) {
			for (int j = i + 1; j < N; j++) {
				model.ifThen(model.and(model.arithm(p[i][2], "=", 0), model.arithm(p[j][2], "=", 0)), model.or(model.arithm(p[j][0], "-", p[i][0], ">=", w[i]), model.arithm(p[i][0], "-", p[j][0], ">=", w[j]), model.arithm(p[j][1], "-", p[i][1], ">=", l[i]), model.arithm(p[i][1], "-", p[j][1], ">=", l[j])));
				model.ifThen(model.and(model.arithm(p[i][2], "=", 0), model.arithm(p[j][2], "=", 1)), model.or(model.arithm(p[j][0], "-", p[i][0], ">=", w[i]), model.arithm(p[i][0], "-", p[j][0], ">=", l[j]), model.arithm(p[j][1], "-", p[i][1], ">=", l[i]), model.arithm(p[i][1], "-", p[j][1], ">=", w[j])));
				model.ifThen(model.and(model.arithm(p[i][2], "=", 1), model.arithm(p[j][2], "=", 0)), model.or(model.arithm(p[j][0], "-", p[i][0], ">=", l[i]), model.arithm(p[i][0], "-", p[j][0], ">=", w[j]), model.arithm(p[j][1], "-", p[i][1], ">=", w[i]), model.arithm(p[i][1], "-", p[j][1], ">=", l[j])));
				model.ifThen(model.and(model.arithm(p[i][2], "=", 1), model.arithm(p[j][2], "=", 1)), model.or(model.arithm(p[j][0], "-", p[i][0], ">=", l[i]), model.arithm(p[i][0], "-", p[j][0], ">=", l[j]), model.arithm(p[j][1], "-", p[i][1], ">=", w[i]), model.arithm(p[i][1], "-", p[j][1], ">=", w[j])));
			}
		}
		
		for (int i = 0; i < N - 1; i++) {
			model.ifThen(model.arithm(p[i][2], "=", 1),model.arithm(position[i],  "-", p[i][0], "=", w[i]));
			model.ifThen(model.arithm(p[i][2], "=", 0),model.arithm(position[i],  "-", p[i][1], "=", l[i]));
		}
		
		for (int i = 0; i < N-1; i++) {
			model.arithm(position[i], ">=", position[i+1]);
		}
		
		int j = 0;
		while (model.getSolver().solve()) {
			System.out.println("Solution " + j++ + " found : ");
			for (int i = 0; i < N; i++) {
				System.out.println(p[i][0].getValue()+", " + p[i][1].getValue() + ", " +p[i][2].getValue());
			}
		}

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		container_choco app = new container_choco();
		app.solve();
	}

}
