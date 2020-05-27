package cbls115676khmt61.NguyenVanSon_20163560.CP;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;

public class OrderLBS {

	static IntVar[] X;
	public static void main(String[] args) {
		// 1. Create a Model
		Model model = new Model("my first problem");
		X  = new IntVar[5];
		for (int i = 0; i < 5; i++)
            X[i] = model.intVar("X[" + i + "]", 1, 5);

 

        model.arithm(model.intOffsetView(X[2], 3), "!=", X[1]).post();
        model.arithm(X[3], "<=", X[4]).post();
        model.arithm(model.intOffsetView(X[0], 1), "=", X[2], "+", X[3]).post();
        model.arithm(X[4], "<=", 3).post();
        model.arithm(X[1], "+", X[4], "=", 7).post();
        model.ifThen(model.arithm(X[2], "=", 1), model.arithm(X[4], "!=", 2));
        
        model.or(model.arithm(X[1], "<=", 3), model.arithm(X[3], "<=", 2),
                model.arithm(X[2], ">=", 3), model.arithm(X[4], "!=", 2));
	}
	

}
