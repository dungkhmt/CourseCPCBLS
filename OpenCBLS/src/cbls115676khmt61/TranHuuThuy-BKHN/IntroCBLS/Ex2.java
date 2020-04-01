package com.thuy.bachkhoa;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

public class Ex2 {

    private VarIntLS X[];

    LocalSearchManager mgr;

    ConstraintSystem S;


    public void stateModel() {
        X = new VarIntLS[5];
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        for (int i = 0; i < 5; i++) {
            X[i] = new VarIntLS(mgr, 1, 5);
        }

        S.post(new NotEqual(new FuncPlus(X[2], 3), X[1]));
        S.post(new LessOrEqual(X[3], X[4]));
        S.post(new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0], 1)));
        S.post(new LessOrEqual(X[4], 3));
        S.post(new IsEqual(new FuncPlus(X[1], X[4]), 7));
        S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));

        mgr.close();
    }


    public void localSearch() {
        print();
        System.out.println("Loss :" + S.violations());

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
        for (int i = 0; i < 5; i++) {
            System.out.print(X[i].getValue() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        Ex2 e = new Ex2();
        e.stateModel();
        e.localSearch();
    }

}
