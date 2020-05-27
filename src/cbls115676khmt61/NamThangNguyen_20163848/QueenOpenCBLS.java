package cbls115676khmt61.NamThangNguyen_20163848;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;
import localsearch.selectors.MinMaxSelector;

public class QueenOpenCBLS {
    int N; // number of queens
    LocalSearchManager mgr; // manager object
    VarIntLS[] X; // decision variables
    ConstraintSystem S;

    public QueenOpenCBLS(int n){
        this.N = n;
    }

    public void stateModel(){
        mgr = new LocalSearchManager();
        X = new VarIntLS[N];
        for(int i = 0; i < N; i++)
            X[i] = new VarIntLS(mgr,0,N-1);

        S = new ConstraintSystem(mgr);
        S.post(new AllDifferent(X));

        IFunction[] f1 = new IFunction[N];
        for(int i = 0; i < N; i++)
            f1[i] = new FuncPlus(X[i], i);
        S.post(new AllDifferent(f1));

        IFunction[] f2 = new IFunction[N];
        for(int i = 0; i < N; i++)
            f2[i] = new FuncPlus(X[i], -i);
        S.post(new AllDifferent(f2));

        mgr.close();
    }

    public void search(){
        int it = 0;
        while(it < 100000 && S.violations() > 0){
            MinMaxSelector mms = new MinMaxSelector(S);
            VarIntLS sel_x = mms.selectMostViolatingVariable();
            int sel_v = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_v); // local move
            System.out.println("Step " + it + ",  S = " + S.violations() + " ");
            it++;
        }
    }

    public static void main(String[] args) {
        QueenOpenCBLS app = new QueenOpenCBLS(10);
        app.stateModel();
        app.search();
    }
}
