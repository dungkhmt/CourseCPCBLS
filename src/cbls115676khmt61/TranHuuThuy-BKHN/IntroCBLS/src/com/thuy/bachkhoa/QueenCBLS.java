package com.thuy.bachkhoa;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;
import localsearch.selectors.MinMaxSelector;

public class QueenCBLS {
    int N;

    // modeling
    LocalSearchManager mgr;
    VarIntLS[] X; // decision variables

    ConstraintSystem S;


    public QueenCBLS(int N) {
        this.N = N;
    }

    private void stateModel() {
        mgr = new LocalSearchManager();

        X = new VarIntLS[N];

        for (int i = 0; i < N; i++)
            X[i] = new VarIntLS(mgr, 1, N);


        // Khơi tạo các ràng buộc
        S = new ConstraintSystem(mgr);

        IConstraint c = new AllDifferent(X); // r
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

        //-----------------------------------------------------

        mgr.close(); // close model
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

        while (it++ < 10_000 && S.violations() > 0){

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
        QueenCBLS queen = new QueenCBLS(5000);
        queen.stateModel();
        queen.localSearch();
    }

}
