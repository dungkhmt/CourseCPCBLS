package com.thuy.bachkhoa;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;
import localsearch.selectors.MinMaxSelector;

public class Example_2 {

    private int N;


    LocalSearchManager mgr;
    VarIntLS X[];

    ConstraintSystem S;

    public Example_2(int N) {
        this.N = N;
    }

    private void stateModel() {
        mgr = new LocalSearchManager();

        X = new VarIntLS[N];

        for (int i = 0; i < N; i++) {
            X[i] = new VarIntLS(mgr, 1, N);
        }

        S = new ConstraintSystem(mgr);


        // X2 + 3 != X1
        S.post(new NotEqual(new FuncPlus(X[2] ,3), X[1]));

        //X3 <= X4
        S.post(new LessOrEqual(X[3], X[4]));

        S.post(new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0], 1)));

        S.post(new LessOrEqual(new FuncMinus(X[4], 3), 0));

        S.post(new IsEqual(new FuncPlus(X[1], X[4]), 7));

        S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));

        mgr.close();
    }

    private void printSol() {
        for (int i = 0; i < N; i++) {
            System.out.print(X[i].getValue() + " ");
        }
        System.out.println();
    }

    private void localSearch() {
        // ban đầu
        printSol();
        System.out.println("init, S = " + S.violations());

        int it = 1;

        // biến quản lý các biến tham gia nhiều rẳng buộc nhất
        MinMaxSelector mms = new MinMaxSelector(S);

        while (it++ < 10_000 && S.violations() > 0) {

            // chọn biến tham gia nhiều ràng buộc nhất
            VarIntLS sel_x = mms.selectMostViolatingVariable();

            // lấy ra giá trị tốt nhất
            int sel_value = mms.selectMostPromissingValue(sel_x);

            sel_x.setValuePropagate(sel_value);

            System.out.println("Step " + it + ", S = " + S.violations());
        }

        System.out.println("Best solution");
        printSol();
    }

    public static void main(String[] args) {
        Example_2 exm = new Example_2(5);
        exm.stateModel();
        exm.localSearch();
    }


}
