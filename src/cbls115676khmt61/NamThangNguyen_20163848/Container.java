package cbls115676khmt61.NamThangNguyen_20163848;

import cbls115676khmt61.NamThangNguyen_20163848.search_strategy.HillClimbingSearch;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class Container {

    int W = 4;
    int H = 6;
    int N = 6;
    int w[] = {4, 1, 2, 1, 4, 3};
    int h[] = {1, 3, 2, 3, 1, 2};

    LocalSearchManager mgr;
    ConstraintSystem S;
    VarIntLS[] X, Y, O;
    Random r = new Random();

    private void stateModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);
        X = new VarIntLS[N];
        Y = new VarIntLS[N];
        O = new VarIntLS[N];

        for (int i = 0; i < N; i++) {
            X[i] = new VarIntLS(mgr, 0, W);
            Y[i] = new VarIntLS(mgr, 0, H);
            O[i] = new VarIntLS(mgr, 0, 1);
        }


        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0)),
                        new OR(new OR(new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]),
                                new LessOrEqual(new FuncPlus(X[j], w[j]), X[i])),
                                new OR(new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]),
                                        new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]))
                        )));

                S.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1)),
                        new OR(new OR(new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]),
                                new LessOrEqual(new FuncPlus(X[j], h[j]), X[i])),
                                new OR(new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]),
                                        new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]))
                        )));

                S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0)),
                        new OR(new OR(new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]),
                                new LessOrEqual(new FuncPlus(X[j], w[j]), X[i])),
                                new OR(new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]),
                                        new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]))
                        )));

                S.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1)),
                        new OR(new OR(new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]),
                                new LessOrEqual(new FuncPlus(X[j], h[j]), X[i])),
                                new OR(new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]),
                                        new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]))
                        )));
            }

            S.post(new Implicate(new IsEqual(O[i], 0), new AND(new LessOrEqual(new FuncPlus(X[i], w[i]), W),
                    new LessOrEqual(new FuncPlus(Y[i], h[i]), H))));

            S.post(new Implicate(new IsEqual(O[i], 1), new AND(new LessOrEqual(new FuncPlus(X[i], h[i]), W),
                    new LessOrEqual(new FuncPlus(Y[i], w[i]), H))));


        }

        for (int i = 0; i < N; i++) {
            for (int j = i + 1; j < N; j++) {
                S.post(new OR(new LessOrEqual(new FuncMinus(Y[i], Y[j]), 0),
                        new OR(new AND(new LessOrEqual(h[i], new FuncMinus(X[j], X[i])), new IsEqual(O[i], 0)),
                                new AND(new LessOrEqual(w[i], new FuncMinus(X[j], X[i])), new IsEqual(O[i], 1)))));
            }
        }

        mgr.close();

    }


    class Move {
        int i;
        int x, y, o;

        public Move(int i, int x, int y, int o) {
            this.i = i;
            this.x = x;
            this.y = y;
            this.o = o;
        }
    }

    private void print() {
        char[][] p = new char[W+1][H+1];
        for (int i=0; i< W; i++)
            for (int j=0; j< H; j++)
                p[i][j] = '.';
        for (int i=0; i< N; i++) {

            if (O[i].getValue() == 0) {
                for (int j=X[i].getValue(); j<X[i].getValue()+w[i]; j++)
                    for (int k=Y[i].getValue(); k<Y[i].getValue()+h[i]; k++)
                        p[j][k] = (char) (i + '0');
            }
            else {
                for (int j=X[i].getValue(); j<X[i].getValue()+h[i]; j++)
                    for (int k=Y[i].getValue(); k<Y[i].getValue()+w[i]; k++)
                        p[j][k] = (char) (i + '0');
            }
        }

        for (int i=0; i<W; i++)
            System.out.println(p[i]);
    }

    public static void main(String[] args) {
        Container app = new Container();
        app.stateModel();
        HillClimbingSearch hill = new HillClimbingSearch();
        hill.search(app.S, 10000);
        app.print();
    }
}
