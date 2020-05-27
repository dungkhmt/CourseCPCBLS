package ConstrainProgramming;

/*
Một container cần vận chuyển 6 kiện hàng 0->5 đến các điểm giao theo thứ tự đó. Thùng xe có chiều dài bằng H = 6m
và chiều rộng bằng W=4m. Chiều dài rộng các kiện hàng là:
Kiện hàng     wi    li
0             1     4
1             3     1
2             2     2
3             3     1
4             1     4
5             2     3
Hãy viết chương trình tìm kiems cục bộ dựa trên ràng buộc đưa ra phương án sắp xếp sao cho đến môi điểm giao,
 việc tháo gỡ 1 kiện hàng không cần phải làm dịch chuyển kiện hàng chưa được giao.
 Lưu ý: container chỉ có cửa ở 1 đầu, các kiện hàng khi dến điểm giao sẽ có xe nâng đưa cánh tay vào container và nhấc kiện hàng ra
 */


import javafx.geometry.HPos;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.Random;

public class Container {
    int W = 4;
    int H = 6;
    int N = 6;
    int[] h = {1,3,2,3,1,2};
    int[] w = {4,1,2,1,4,3};

    LocalSearchManager mgr;
    VarIntLS[] x;
    VarIntLS[] y;
    VarIntLS[] o;
    ConstraintSystem S;
    private void stateModel(){

        mgr = new LocalSearchManager();
        x = new VarIntLS[N];
        y = new VarIntLS[N];
        o = new VarIntLS[N];
        Random rd = new Random();
        for(int i = 0; i < N; i++){
            x[i] = new VarIntLS(mgr,0,W);
            y[i] = new VarIntLS(mgr,0,H);
            o[i] = new VarIntLS(mgr,0,1);
        }
        S = new ConstraintSystem(mgr);
        for(int i = 0; i < N-1; i++){
            for(int j = i+1; j < N; j++){
                // items i and j cannot overlap
                IConstraint[] c1 = new IConstraint[2];
                c1[0] = new IsEqual(o[i], 0);
                c1[1] = new IsEqual(o[j], 0);
                IConstraint c2 = new AND(c1);
                IConstraint[] c3 = new IConstraint[4];
                c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]),x[j]);
                c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]),x[i]);
                c3[2] = new LessOrEqual(new FuncPlus(y[i], h[i]),y[j]);
                c3[3] = new LessOrEqual(new FuncPlus(y[j], h[j]),y[i]);
                IConstraint c4 = new OR(c3);
                S.post(new Implicate(c2, c4));


                c1 = new IConstraint[2];
                c1[0] = new IsEqual(o[i], 0);
                c1[1] = new IsEqual(o[j], 1);
                c2 = new AND(c1);
                c3 = new IConstraint[4];
                c3[0] = new LessOrEqual(new FuncPlus(x[i], w[i]),x[j]);
                c3[1] = new LessOrEqual(new FuncPlus(x[j], h[j]),x[i]);
                c3[2] = new LessOrEqual(new FuncPlus(y[i], h[i]),y[j]);
                c3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]),y[i]);
                c4 = new OR(c3);
                S.post(new Implicate(c2, c4));


                c1 = new IConstraint[2];
                c1[0] = new IsEqual(o[i], 1);
                c1[1] = new IsEqual(o[j], 0);
                c2 = new AND(c1);
                c3 = new IConstraint[4];
                c3[0] = new LessOrEqual(new FuncPlus(x[i], h[i]),x[j]);
                c3[1] = new LessOrEqual(new FuncPlus(x[j], w[j]),x[i]);
                c3[2] = new LessOrEqual(new FuncPlus(y[i], w[i]),y[j]);
                c3[3] = new LessOrEqual(new FuncPlus(y[j], h[j]),y[i]);
                c4 = new OR(c3);
                S.post(new Implicate(c2, c4));

                c1 = new IConstraint[2];
                c1[0] = new IsEqual(o[i], 1);
                c1[1] = new IsEqual(o[j], 1);
                c2 = new AND(c1);
                c3 = new IConstraint[4];
                c3[0] = new LessOrEqual(new FuncPlus(x[i], h[i]),x[j]);
                c3[1] = new LessOrEqual(new FuncPlus(x[j], h[j]),x[i]);
                c3[2] = new LessOrEqual(new FuncPlus(y[i], w[i]),y[j]);
                c3[3] = new LessOrEqual(new FuncPlus(y[j], w[j]),y[i]);
                c4 = new OR(c3);
                S.post(new Implicate(c2, c4));


            }
        }

        for(int i = 0; i < N; i++){
            S.post(new Implicate(new IsEqual(o[i], 0),
                    new LessOrEqual(new FuncPlus(x[i], w[i]), W)));
            S.post(new Implicate(new IsEqual(o[i], 0),
                    new LessOrEqual(new FuncPlus(y[i], h[i]), H)));

            S.post(new Implicate(new IsEqual(o[i], 1),
                    new LessOrEqual(new FuncPlus(x[i], h[i]), W)));
            S.post(new Implicate(new IsEqual(o[i], 1),
                    new LessOrEqual(new FuncPlus(y[i], w[i]), H)));
        }

//        Rang buộc lấy đồ
        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                S.post(new OR(new LessOrEqual(new FuncMinus(y[i], y[j]), 0),
                        new OR(new AND(new LessOrEqual(h[i], new FuncMinus(x[j], x[i])), new IsEqual(o[i], 0)),
                                new AND(new LessOrEqual(w[i], new FuncMinus(x[j], x[i])), new IsEqual(o[i], 1)))));
            }
        }

        mgr.close();
    }

    private void print() {
        char[][] p = new char[W+1][H+1];
        for (int i=0; i< W; i++)
            for (int j=0; j< H; j++)
                p[i][j] = '.';
        for (int i=0; i< N; i++) {
            if (o[i].getValue() == 0) {
                for (int j=x[i].getValue(); j<x[i].getValue()+w[i]; j++)
                    for (int k=y[i].getValue(); k<y[i].getValue()+h[i]; k++)
                        p[j][k] = (char) (i + '0');
            }
            else {
                for (int j=x[i].getValue(); j<x[i].getValue()+h[i]; j++)
                    for (int k=y[i].getValue(); k<y[i].getValue()+w[i]; k++)
                        p[j][k] = (char) (i + '0');
            }
        }

        for (int i=0; i<W; i++)
            System.out.println(p[i]);
    }

    private void search(){
        HillCimbingSearch searcher = new HillCimbingSearch();
        searcher.search(S, 10000);
        print();
    }
    public void solve(){
        stateModel();
        search();
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Container app = new Container();
        app.solve();
    }
}
