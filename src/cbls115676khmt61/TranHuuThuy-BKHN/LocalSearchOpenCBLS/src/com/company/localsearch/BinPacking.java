


/*
    Có 5 thùng có  dung tích 60, 70, 90, 80, 100.Có 20 chất lỏng 0, 1, .., 19 vởi thể tích được cho bởi bảng

    Chất lỏng   Thể tích
    0           20
    1           15
    ...


    Danh mục câm các chất lỏng cùng một thùng
    0,1
    7, 8
    12, 17
    8,9
    1,2,9
    0, 9, 12
    Viết chương trinh phân loại sao cho dung tích mỗi thùng không vượt quá dung tích của nó

 */


import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.*;

import java.util.ArrayList;
import java.util.Arrays;

public class BinPacking {
    int N = 20;
    int K = 5;
    VarIntLS X[][]; // NxK
    ArrayList<ArrayList<Integer>> C = new ArrayList<ArrayList<Integer>>(); // các dung dịch không được phép chứa cùng thùng

    int vk[] = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10}; //dung tich mỗi dung dịch
    int v[] = {60, 70, 90, 80, 100}; // dung tích của mỗi thùng

    ConstraintSystem S;
    LocalSearchManager mgr;


    public void buildModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        C.add(new ArrayList<>(Arrays.asList(0, 1)));
        C.add(new ArrayList<>(Arrays.asList(7, 8)));
        C.add(new ArrayList<>(Arrays.asList(12, 17)));
        C.add(new ArrayList<>(Arrays.asList(8, 9)));
        C.add(new ArrayList<>(Arrays.asList(1, 2, 9)));
        C.add(new ArrayList<>(Arrays.asList(0, 9, 12)));

        X = new VarIntLS[N][K];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < K; j++)
                X[i][j] = new VarIntLS(mgr, 0, 1);
        }


        for (int j = 0; j < N; j++) {
            VarIntLS[] Y = new VarIntLS[K];
            for (int k = 0; k < K; k++)
                Y[k] = X[j][k];

            S.post(new IsEqual(new Sum(Y), 1));
        }

        for (ArrayList<Integer> c : C) {
            for (int k = 0; k < K; k++) {
                IConstraint[] Y = new IConstraint[c.size()];

                for (int i = 0; i < Y.length; i++) {
                    Y[i] = new IsEqual(X[c.get(i)][k], 1);
                }

                S.post(new IsEqual((IFunction) new AND(Y), 1));
            }
        }

        for (int k = 0; k < K; k++) {
            VarIntLS[] Y = new VarIntLS[N];
            for (int j = 0; j < N; j++)
                Y[j] = X[j][k];

            S.post(new LessOrEqual(new ConditionalSum(Y, vk, 1), v[k]));
        }

        mgr.close();
    }

    public static void main(String[] args) {
        BinPacking app = new BinPacking();
        app.buildModel();

        HillCimbingSearch hill = new HillCimbingSearch();
        hill.search(app.S, 100);
    }
}
