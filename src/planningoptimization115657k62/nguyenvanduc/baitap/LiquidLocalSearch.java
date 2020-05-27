package planningoptimization115657k62.nguyenvanduc.baitap;

import com.sun.org.apache.xpath.internal.functions.FuncSum;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.functions.sum.SumFun;
import localsearch.functions.sum.SumVarConstraints;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;

import java.util.ArrayList;

public class LiquidLocalSearch {
    int N = 5; //so thung chua
    int M = 20; //so loai chat long
    int Q = 6; //so luong conflict
    int[] c = {60, 70, 90, 80, 100};
    int[] v = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10,
            20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
    ArrayList<Integer>[] Ls = null;

    LocalSearchManager mgr = null;
    VarIntLS[][] X = null;
    ConstraintSystem S = null;

    public static void main(String[] args) {
        LiquidLocalSearch app = new LiquidLocalSearch();
        app.solve();
    }

    public void solve() {
        //conflict
        //cho nay du lieu nho thi lam thu cong cho nhanh
        //neu du lieu lon thi lam vong lap
        Ls = new ArrayList[Q];
        Ls[0] = new ArrayList<Integer>();
        Ls[0].add(0); Ls[0].add(1);

        Ls[1] = new ArrayList<Integer>();
        Ls[1].add(7); Ls[1].add(8);

        Ls[2] = new ArrayList<Integer>();
        Ls[2].add(12); Ls[2].add(17);

        Ls[3] = new ArrayList<Integer>();
        Ls[3].add(8); Ls[3].add(9);

        Ls[4] = new ArrayList<Integer>();
        Ls[4].add(1); Ls[4].add(2); Ls[4].add(9);

        Ls[5] = new ArrayList<Integer>();
        Ls[5].add(0); Ls[5].add(9); Ls[5].add(12);

        mgr = new LocalSearchManager();
        X = new VarIntLS[M][N];
        for (int i = 0; i < M; i++) {
            for (int j = 0; j < N; j++) {
                X[i][j] = new VarIntLS(mgr, 0, 1);
            }
        }
        S = new ConstraintSystem(mgr);

        //1.
        for (int i = 0; i < M; i++) {
            S.post(new IsEqual(new Sum(X[i]), 1));
        }

        //2 conflict
        for (int q = 0; q < Q; q++) {
            if (Ls[q].size() == 2) {
                int i1 = Ls[q].get(0);
                int i2 = Ls[q].get(1);
                for (int j = 0; j < N; j++) {
                    S.post(new LessOrEqual(new FuncPlus(X[i1][j], X[i2][j]), 1));
                }
            } else {
                int i1 = Ls[q].get(0);
                int i2 = Ls[q].get(1);
                int i3 = Ls[q].get(2);
                for (int j = 0; j < N; j++) {
                    VarIntLS[] tmp = new VarIntLS[3];
                    tmp[0] = X[i1][j];
                    tmp[1] = X[i2][j];
                    tmp[2] = X[i3][j];
                    S.post(new LessOrEqual(new Sum(tmp), 2));
                }
            }
        }

        //3 <= tong suc chua
        for (int j = 0; j < N; j++) {
            IFunction[] f = new IFunction[M];
            for (int i = 0; i < M; i++) {
                f[i] = new FuncMult(X[i][j], v[i]);
            }
            S.post(new LessOrEqual(new Sum(f), c[j]));
        }

        mgr.close();
        HillClimbingSearch s = new HillClimbingSearch();
        s.search(S, 1000);

        for (int j = 0; j < N; j++) {
            System.out.println();
            System.out.print("thung " + j + ": ");
            for (int i = 0; i < M; i++) {
                if (X[i][j].getValue() == 1) {
                    System.out.print(i + ", ");
                }
            }
        }



    }
}
