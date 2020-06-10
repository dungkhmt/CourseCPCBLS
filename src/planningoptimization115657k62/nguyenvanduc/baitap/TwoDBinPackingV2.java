package planningoptimization115657k62.nguyenvanduc.baitap;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.util.Scanner;

public class TwoDBinPackingV2 {
    int MAX = 100;
    int n = 6;
    int[] w = {1, 3, 2, 3, 1, 2};
    int[] h = {4, 1, 2, 1, 4, 3};
    int W = 4;
    int H = 6;
    int[] order = {0, 1, 2, 3, 4, 5}; //thu tu giao hang




    public void solve() {
        Model model = new Model();
        IntVar[] o = model.intVarArray(n, 0, 1);
        IntVar[] x = model.intVarArray(n, 0, W-1);
        IntVar[] y = model.intVarArray(n, 0, H-1);

        for (int i = 0; i < n; i++) {
            model.scalar(new IntVar[] {x[i], o[i]}, new int[] {1, h[i] - w[i]}, "<=", W - w[i]).post();
            model.scalar(new IntVar[] {y[i], o[i]}, new int[] {1, -h[i] + w[i]}, "<=", H - h[i]).post();

        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Constraint c1 = model.scalar(new IntVar[] {x[i], o[i], x[j]}, new int[] {1, h[i] - w[i], -1}, "<=", -w[i]);
                Constraint c2 = model.scalar(new IntVar[] {x[j], o[j], x[i]}, new int[] {1, h[j] - w[j], -1}, "<=", -w[j]);

                Constraint c3 = model.scalar(new IntVar[] {y[i], o[i], y[j]}, new int[] {1, -h[i] + w[i], -1}, "<=",- h[i]);
                Constraint c4 = model.scalar(new IntVar[] {y[j], o[j], y[i]}, new int[] {1, -h[j] + w[j], -1}, "<=",- h[j]);
                model.or(model.or(c1, c2), model.or(c3, c4)).post();
            }
        }

        //lay theo thu tu
        //1 can ra truoc 2
        for (int i1 = 0; i1 < n - 1; i1++) {
            for (int i2 = i1 + 1; i2 < n; i2++) {
                // hoac y2 < y1
                Constraint c1 = model.arithm(y[i1], ">=", y[i2]);

                //hoac 1 nam ben phai 2
                Constraint c2 = model.scalar(
                        new IntVar[] {x[i1], o[i2], x[i2]},
                        new int[] {1, w[i2]-h[i2], -1},
                        ">=", w[i2]
                );

                //hoac 1 nam ben trai 2
                Constraint c3 = model.scalar(
                        new IntVar[] {x[i1], o[i1], x[i2]},
                        new int[] {-1, w[i1]-h[i1], 1},
                        ">=", w[i1]
                );


                model.or(c1, c2, c3).post();
            }
        }

        model.getSolver().solve();
        System.out.println("solution");
        for (int i = 0; i < n; i++) {
            System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
        }

//        while(model.getSolver().solve()) {
//            System.out.println("solution");
//            for (int i = 0; i < n; i++) {
//                System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
//            }
//        }

    }

    public static void main(String[] args) {
        TwoDBinPackingV2 app = new TwoDBinPackingV2();
        app.solve();
    }
}

