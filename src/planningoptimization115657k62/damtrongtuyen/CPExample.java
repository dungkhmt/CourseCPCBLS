package planningoptimization115657k62.damtrongtuyen;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.variables.IntVar;

import java.util.List;

public class CPExample {
    public static void main(String[] args) {
        Model model = new Model("cp_test");
        IntVar[] var = model.intVarArray(5, 1, 5);
        model.arithm(var[1], "!=", var[2], "+", 3).post();
        model.arithm(var[3], "<=", var[4]).post();
        model.arithm(model.intOffsetView(var[0],1), "=", var[2], "+", var[3]).post();
        model.arithm(var[4], "<=",3).post();
        model.arithm(var[1], "+", var[4], "=", 7).post();
        model.ifThen(model.arithm(var[2], "=", 1), model.arithm(var[4], "!=", 2));
        List<Solution> solutions = model.getSolver().findAllSolutions();
        // constraint cho nhiều biến: model.scalar
        for(Solution s: solutions)
        {
            System.out.println(s.toString());
        }
//        for (int i = 0; i < 5; i++) {
//            System.out.println("var "+i+": "+ var[i]);
//        }

    }
}
