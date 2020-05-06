package planningoptimization115657k62.phammanhtuan.QHRB;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class Binpacking {
	private static int N;
	private static int W;
	private static int H;
	private static int[] w = new int[30];
	private static int[] h = new int[30];
	
	public static void load (String s) throws Exception {
		InputStream in = new FileInputStream(s) ;
		BufferedReader br = new BufferedReader(
					new InputStreamReader(in)
				);
		String line;
		String delims = "\\s+";
		line = br.readLine();
		String[] token = line.split(delims);
		W = Integer.parseInt(token[0]);
		H = Integer.parseInt(token[1]);		
		int i = 0;
		do {
			line = br.readLine();
			token = line.split(delims);
			w[i]= Integer.parseInt(token[0]);
			if (w[i]!=-1) {
				h[i]= Integer.parseInt(token[1]);
			}		
			i++;
		} while (line.equalsIgnoreCase("-1")==false);
		N=i-1;
	}
public static void main(String[] args) throws Exception {
	load("E:\\Java\\eclipse\\bin-packing-2D.txt");
	Model model = new Model("Binpacking");
	IntVar[][] X = new IntVar[N][N];
	for (int i=0;i<N;i++) {
		X[i][0] = model.intVar("X["+i+"]["+0+"]",0,W-1);
		X[i][1] = model.intVar("X["+i+"]["+1+"]",0,H-1);
		X[i][2] = model.intVar("X["+i+"]["+0+"]",0,1);
	}
	for (int i=0;i<N;i++) {
		Constraint c1 = model.and(model.arithm(model.intOffsetView(X[i][0], w[i]), "<=", W),model.arithm(model.intOffsetView(X[i][1], h[i]), "<=", H));
		Constraint c2 = model.and(model.arithm(model.intOffsetView(X[i][0], h[i]), "<=", W),model.arithm(model.intOffsetView(X[i][1], w[i]), "<=", H));
		model.ifThen(model.arithm(X[i][2], "=", 0), c1);
		model.ifThen(model.arithm(X[i][2], "=", 1), c2);
	}
	for (int i=0;i<N;i++) {
		Constraint o0 = model.arithm(X[i][2], "=", 0);
		Constraint o1 = model.arithm(X[i][2], "=", 1);
		IntVar x0 = model.intOffsetView(X[i][0], w[i]); // xi + wi
		IntVar x1 = model.intOffsetView(X[i][0], h[i]); // xi + hi
		IntVar x2 = model.intOffsetView(X[i][1], w[i]); // yi + wi
		IntVar x3 = model.intOffsetView(X[i][1], h[i]); // yi + hi
		for (int j=i+1;j<N;j++) {
			Constraint o2 = model.arithm(X[j][2], "=", 0);
			Constraint o3 = model.arithm(X[j][2], "=", 1);
			IntVar x4 = model.intOffsetView(X[j][0], w[j]); // xj + wj
			IntVar x5 = model.intOffsetView(X[j][0], h[j]); // xj + hj
			IntVar x6 = model.intOffsetView(X[j][1], w[j]); // yj + wj
			IntVar x7 = model.intOffsetView(X[j][1], h[j]); // yj + hj
			Constraint c1 = model.or(model.arithm(x0,"<=",X[j][0]),model.arithm(x4,"<=", X[i][0]),model.arithm(x3, "<=", X[j][1]),model.arithm(x7, "<=", X[i][1]));
			Constraint c2 = model.or(model.arithm(x0,"<=",X[j][0]),model.arithm(x5,"<=", X[i][0]),model.arithm(x3, "<=", X[j][1]),model.arithm(x6, "<=", X[i][1]));
			Constraint c3 = model.or(model.arithm(x1,"<=",X[j][0]),model.arithm(x4,"<=", X[i][0]),model.arithm(x2, "<=", X[j][1]),model.arithm(x7, "<=", X[i][1]));
			Constraint c4 = model.or(model.arithm(x1,"<=",X[j][0]),model.arithm(x5,"<=", X[i][0]),model.arithm(x2, "<=", X[j][1]),model.arithm(x6, "<=", X[i][1]));
			model.ifThen(model.and(o0,o2), c1);
			model.ifThen(model.and(o0,o3), c2);
			model.ifThen(model.and(o1,o2), c3);
			model.ifThen(model.and(o1,o3), c4);
		}
	}
	Solver s = model.getSolver();
	while (s.solve()) {
		for (int i=0;i<N;i++) {
			System.out.println(X[i][0].getValue() + " " + X[i][1].getValue() + " " + X[i][2].getValue());
		}
		System.out.println("---------------------");
	}
	
}
}
