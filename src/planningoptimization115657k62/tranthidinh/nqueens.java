import HillClimbingSearch.HillClimbingSearch;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;
import localsearch.selectors.MinMaxSelector;

public class nqueens {
    public static void main(String[] args)
    {
        int N = 10;
        LocalSearchManager mgr = new LocalSearchManager();
        VarIntLS[] X = new VarIntLS[N];
        for(int i = 0; i<N; i++)
            X[i] = new VarIntLS(mgr, 0, N-1);
        ConstraintSystem S = new ConstraintSystem(mgr);
        S.post(new AllDifferent(X));
        IFunction[] f1 = new IFunction[N];
        for (int i = 0; i<N; i++)
            f1[i] = new FuncPlus(X[i], i);
        S.post(new AllDifferent(f1));
        IFunction[] f2 = new IFunction[N];
        for (int i = 0; i<N; i++)
            f2[i] = new FuncPlus(X[i], -i);
        S.post(new AllDifferent(f2));
        mgr.close();
        HillClimbingSearch searcher = new HillClimbingSearch();
        searcher.search(S, 10000);
        System.out.println("khoi tao S=" + S.violations());
        MinMaxSelector mms = new MinMaxSelector(S);
        int it = 0;
        while (it < 10000 && S.violations() > 0)
        {
            VarIntLS sel_x = mms.selectMostViolatingVariable();
            int sel_v = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_v);
            System.out.println("Step" + it + ", violations = " + S.violations());
            it++;
        }

    }


}
