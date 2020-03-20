package Baitap;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class NQueen {

    int n; // number of queens
    LocalSearchManager mgr;
    VarIntLS[] x;// decision variables
    ConstraintSystem S;

    public NQueen(int n) {
        this.n = n;
    }

    public void stateModel() {
        mgr = new LocalSearchManager();
        x = new VarIntLS[n];
        for (int i = 0; i < n; i++) {
            x[i] = new VarIntLS(mgr, 0, n - 1);
        }
        S = new ConstraintSystem(mgr);
        S.post(new AllDifferent(x));
        IFunction[] f1 = new IFunction[n];
        for (int i = 0; i < n; i++) {
            f1[i] = new FuncPlus(x[i], i);
        }
        S.post(new AllDifferent(f1));
        IFunction[] f2 = new IFunction[n];
        for (int i = 0; i < n; i++) {
            f2[i] = new FuncPlus(x[i], -i);
        }
        S.post(new AllDifferent(f2));
        mgr.close();
    }

    public void search() {
        int it = 0;
        while (it < 100000 && S.violations() > 0) {
            MinMaxSelector mms = new MinMaxSelector(S);
            VarIntLS sel_x = mms.selectMostViolatingVariable();
            int sel_v = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_v);// local move
            System.out.println("Step " + it + ", S = " + S.violations());
            it++;
        }
        printSol();
    }

    public void printSol() {
        for (int i = 0; i < n; i++) {
            System.out.print(x[i].getValue() + " ");
        }
    }

    public static void main(String[] args) {
        NQueen ob = new NQueen(700);
        ob.stateModel();
        ob.search();
    }
}
