package com.thuy.bachkhoa;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class Queen {
    private int N;
    VarIntLS X[];

    LocalSearchManager mgr;
    ConstraintSystem S;


    public Queen(int N) {
        this.N = N;
    }

    public void stateModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        X = new VarIntLS[N];

        for (int i = 0; i < N; i++) {
            X[i] = new VarIntLS(mgr, 1, N);
        }

        S.post(new AllDifferent(X));

        FuncPlus []plus = new FuncPlus[N];
        for(int i = 0; i < N; i++)
            plus[i] = new FuncPlus(X[i], i);

        S.post(new AllDifferent(plus));

        FuncMinus[]minus = new FuncMinus[N];
        for(int i = 0; i < N; i++)
            minus[i] = new FuncMinus(X[i], i);

        S.post(new AllDifferent(minus));

        mgr.close();
    }

    public void localSearch(){
        print();
        System.out.println("Init Loss : " + S.violations());

        int iter = 1_000_000, i = 0;

        // biến quản lý các biến tham gia nhiều rẳng buộc nhất
        MinMaxSelector mms = new MinMaxSelector(S);

        while (i++ < iter && S.violations() > 0) {

            // chọn biến tham gia nhiều ràng buộc nhất
            VarIntLS sel_x = mms.selectMostViolatingVariable();
            int v = mms.selectMostPromissingValue(sel_x);

            sel_x.setValuePropagate(v);
            print();
            System.out.println("Iterator " + i + " Loss :" + S.violations());
        }
    }


    private void print() {
        for (int i = 0; i < N; i++) {
            System.out.print(X[i].getValue() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Queen queen = new Queen(10);
        queen.stateModel();
        queen.localSearch();
    }
}
