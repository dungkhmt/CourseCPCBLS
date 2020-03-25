package Baitap;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class QueenCBLS {
    // data structures input

    int N;	// number of queens

    //modeling
    LocalSearchManager mgr;	// manager object
    VarIntLS[] X;	// decision variables
    ConstraintSystem S;

    private QueenCBLS(int N) {
        this.N = N;
    }

    private void stateModel() {
        mgr = new LocalSearchManager();
        X = new VarIntLS[N];
        for (int i = 0; i < N; i++) {
            X[i] = new VarIntLS(mgr, 1, N);	// by default, init value of X[i] = 1
        }
        S = new ConstraintSystem(mgr);
        IConstraint c = new AllDifferent(X);
        S.post(c);

        IFunction[] f1 = new IFunction[N];
        for (int i = 0; i < N; i++) {
            f1[i] = new FuncPlus(X[i], i);
        }
        S.post(new AllDifferent(f1));

        IFunction[] f2 = new IFunction[N];
        for (int i = 0; i < N; i++) {
            f2[i] = new FuncPlus(X[i], -i);
        }
        S.post(new AllDifferent(f2));

        mgr.close();
    }

    public void printSol() {
        for (int i = 0; i < N; i++) {
            System.out.print(X[i].getValue() + " ");
        }
        System.out.println();
    }

    private void localSearch() {
        printSol();
        System.out.println("init, S = " + S.violations());
        int it = 1;
        while (it < 100000 && S.violations() > 0) {
            MinMaxSelector mms = new MinMaxSelector(S);
            VarIntLS sel_x = mms.selectMostViolatingVariable();
            int sel_value = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_value);	// local move: assign value,
            // propagate update violations,
            // thanks to dependency graph
            System.out.println("Step " + it + ", S = " + S.violations());
            it++;
        }
        System.out.print("Best solution: ");
        printSol();
    }

    public void test1() {
        N = 5;
        mgr = new LocalSearchManager();
        X = new VarIntLS[N];
        for (int i = 0; i < N; i++) {
            X[i] = new VarIntLS(mgr, 1, 10);
            X[i].setValue(i + 1);
        }
        IFunction T = new Sum(X);
        mgr.close();
        printSol();
        System.out.println("T = " + T.getValue());
        int delta = T.getAssignDelta(X[2], 4);
        printSol();
        System.out.println("delta = " + delta + ", T = " + T.getValue());
        X[2].setValuePropagate(4);
        System.out.println("delta = " + delta + ", T = " + T.getValue());
    }

    public static void main(String[] args) {
        QueenCBLS app = new QueenCBLS(600);
        app.stateModel();
        app.localSearch();
        app.test1();
    }
}
