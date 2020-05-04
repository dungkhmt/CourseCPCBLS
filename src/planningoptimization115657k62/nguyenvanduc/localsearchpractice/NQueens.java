package planningoptimization115657k62.nguyenvanduc.localsearchpractice;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class NQueens {
    public void solve() {
        int N = 500;
        LocalSearchManager mgr = new
                LocalSearchManager();

        VarIntLS[] x = new VarIntLS[N];
        for (int i = 0; i < N; i++) {
            x[i] = new VarIntLS(mgr, 0, N-1);
        }

        ConstraintSystem S = new ConstraintSystem(mgr);

        S.post(new AllDifferent(x));

        IFunction[] f1 = new IFunction[N];
        for (int i = 0; i < N; i++) {
            f1[i] = new FuncPlus(x[i], i);
        }
        S.post(new AllDifferent(f1));

        IFunction[] f2 = new IFunction[N];
        for (int i = 0; i < N; i++) {
            f2[i] = new FuncPlus(x[i], -i);
        }
        S.post(new AllDifferent(f2));

        mgr.close();

//        HillClimbingSearch searcher = new HillClimbingSearch();
//        searcher.search(S, 1000);
        MinMaxSelector mms = new MinMaxSelector(S);
        int it = 0;
        while (it++ < 1000 && S.violations() > 0) {
            VarIntLS sel_x = mms.selectMostViolatingVariable();
            int sel_v = mms.selectMostPromissingValue(sel_x);

            sel_x.setValuePropagate(sel_v); // local move
            System.out.println("step " + it  + ", violations =  " + S.violations());
        }
    }
    
    public static void main(String[] args) {
        NQueens app = new NQueens();
        app.solve();
    }
}