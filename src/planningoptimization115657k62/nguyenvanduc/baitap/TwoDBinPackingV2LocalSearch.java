package planningoptimization115657k62.nguyenvanduc.baitap;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.*;
import localsearch.search.TabuSearch;
import localsearch.selectors.MinMaxSelector;

public class TwoDBinPackingV2LocalSearch {
    int MAX = 100;
    int n = 6;
    int[] w = {1, 3, 2, 3, 1, 2};
    int[] h = {4, 1, 2, 1, 4, 3};
    int W = 4;
    int H = 6;
    int[] order = {0, 1, 2, 3, 4, 5}; //thu tu giao hang

    LocalSearchManager mgr = null;
    VarIntLS[] o = null;
    VarIntLS[] x = null;
    VarIntLS[] y = null;
    ConstraintSystem S = null;


    public static void main(String[] args) {
        TwoDBinPackingV2LocalSearch app = new TwoDBinPackingV2LocalSearch();
        app.solve();
    }
    public void solve() {
        mgr = new LocalSearchManager();
        x = new VarIntLS[n];
        y = new VarIntLS[n];
        o = new VarIntLS[n];
        S = new ConstraintSystem(mgr);
        for (int i = 0; i < n; i++) {
            x[i] = new VarIntLS(mgr, 0, W-1);
            y[i] = new VarIntLS(mgr, 0, H-1);
            o[i] = new VarIntLS(mgr, 0, 1);
        }

        //1. khong vuot khoi 2 bien
        for (int i = 0; i < n; i++) {
            S.post(new LessOrEqual(
                    new FuncPlus(
                            new FuncMult(x[i], 1),
                            new FuncMult(o[i], h[i]-w[i])
                    ),
                    W - w[i]
            ));

            S.post(new LessOrEqual(
                    new FuncPlus(
                            new FuncMult(y[i], 1),
                            new FuncMult(o[i], -h[i]+w[i])
                    ),
                    H - h[i]
            ));
        }

        //2. khong overlap
        for (int i = 0; i < n -1; i++) {
            for (int j = i+1; j < n; j++) {
                IFunction[] f1 = new IFunction[3];
                f1[0] = new FuncMult(x[i], 1);
                f1[1] = new FuncMult(o[i], h[i]-w[i]);
                f1[2] = new FuncMult(x[j], -1);
                IConstraint c1 = new LessOrEqual(new Sum(f1), -w[i]);

                IFunction[] f2 = new IFunction[3];
                f2[0] = new FuncMult(x[j], 1);
                f2[1] = new FuncMult(o[j], h[j]-w[j]);
                f2[2] = new FuncMult(x[i], -1);
                IConstraint c2 = new LessOrEqual(new Sum(f2), -w[j]);

                IFunction[] f3 = new IFunction[3];
                f3[0] = new FuncMult(y[i], 1);
                f3[1] = new FuncMult(o[i], -h[i]+w[i]);
                f3[2] = new FuncMult(y[j], -1);
                IConstraint c3 = new LessOrEqual(new Sum(f3), -h[i]);

                IFunction[] f4 = new IFunction[3];
                f4[0] = new FuncMult(y[j], 1);
                f4[1] = new FuncMult(o[j], -h[j]+w[j]);
                f4[2] = new FuncMult(y[i], -1);
                IConstraint c4 = new LessOrEqual(new Sum(f4), -h[j]);

                IConstraint cs[] = new IConstraint[4];
                cs[0] = c1; cs[1] = c2; cs[2] = c3; cs[3] = c4;
                S.post(new OR(cs));
            }
        }

        //3.lay theo thu tu
        //i1 can lay ra truoc i2
        for (int i1 = 0; i1 < n -1; i1++) {
            for (int i2 = i1+1; i2 < n; i2++) {
                //hoac y2 <= y1
                // da co rang buoc khong over lap o tren nen chi can y2 <= y1
                IConstraint c1 = new LessOrEqual(y[i2], y[i1]);

                //hoac 1 nam ben phai 2
                IFunction[] f2 = new IFunction[3];
                f2[0] = new FuncMult(x[i1], -1);
                f2[1] = new FuncMult(o[i2], h[i2]-w[i2]);
                f2[2] = new FuncMult(x[i2], 1);
                IConstraint c2 = new LessOrEqual(new Sum(f2), -w[i2]);

                //hoac 1 nam ben trai 2
                IFunction[] f3 = new IFunction[3];
                f3[0] = new FuncMult(x[i1], 1);
                f3[1] = new FuncMult(o[i1], h[i2]-w[i2]);
                f3[2] = new FuncMult(x[i2], -1);
                IConstraint c3 = new LessOrEqual(new Sum(f3), -w[i1]);

                S.post(new OR(new OR(c1, c2), c3));
            }
        }

        mgr.close();
//        HillClimbingSearch s = new HillClimbingSearch();
//        s.search(S, 1000);
        TabuSearch searcher = new TabuSearch();
        searcher.search(S,30, 10, 100000, 100);

//        MinMaxSelector mms = new MinMaxSelector(S);
//        int it = 0;
//        while (it++ < 1000 && S.violations() > 0) {
//            VarIntLS sel_x = mms.selectMostViolatingVariable();
//            int sel_v = mms.selectMostPromissingValue(sel_x);
//
//            sel_x.setValuePropagate(sel_v); // local move
//            System.out.println("step " + it  + ", violations =  " + S.violations());
//        }

        System.out.println("solution");
        for (int i = 0; i < n; i++) {
            System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
        }

    }
}
