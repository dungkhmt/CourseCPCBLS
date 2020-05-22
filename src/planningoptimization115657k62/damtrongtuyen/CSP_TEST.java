package planningoptimization115657k62.damtrongtuyen;

import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class CSP_TEST {
    public static void main(String[] args) {
        LocalSearchManager lsm = new LocalSearchManager();
        VarIntLS var[] = new VarIntLS[5];
        for (int i = 0; i < 5; i++) {
            var[i] = new VarIntLS(lsm, 1, 5);
        }
        ConstraintSystem s = new ConstraintSystem(lsm);
        s.post(new NotEqual(new FuncPlus(var[2], 3), var[1]));
        s.post(new LessOrEqual(var[3], var[4]));
        s.post(new IsEqual(new FuncPlus(var[2], var[3]), new FuncPlus(var[0], 1)));
        s.post(new LessOrEqual(var[4], 3));
        s.post(new IsEqual(new FuncPlus(var[1], var[4]), 7));
        s.post(new Implicate(new IsEqual(var[2], 1), new NotEqual(var[4], 2)));

        s.close();
        lsm.close();

        TabuSearch searcher = new TabuSearch();
        searcher.search(s, 30, 10, 100000, 100);
        for (int i = 0; i < 5; i++) {
            System.out.println(var[i]);
        }
    }

}
