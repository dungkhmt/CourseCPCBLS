package planningoptimization115657k62.nguyenvanduc.localsearchpractice;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CSP {
    public static void main(String[] args) {
        CSP app =  new CSP();
        app.solve();
    }
    public void solve() {
        LocalSearchManager mgr = new LocalSearchManager();
        VarIntLS[] x = new VarIntLS[5];
        for (int i = 0; i < 5; i++) {
            x[i] = new VarIntLS(mgr, 1, 5);
        }
        ConstraintSystem S = new ConstraintSystem(mgr);

        S.post(new NotEqual(new FuncPlus(x[2], 3), x[1]));
        S.post(new LessOrEqual(x[3], x[4]));
        S.post(new IsEqual(new FuncPlus(x[2], x[3]), new FuncPlus(x[0], 1)));
        S.post(new LessOrEqual(x[4], 3));
        S.post(new IsEqual(new FuncPlus(x[1], x[4]), 7));
        S.post(new Implicate(new IsEqual(x[2], 1), new NotEqual(x[4], 2)));

        mgr.close();

        HillClimbingSearch s = new HillClimbingSearch();
        s.search(S, 1000);

        System.out.println();
        for (int i = 0; i < 5; i++) {
            System.out.println("X" + i + " = " + x[i].getValue());
        }

    }
}
