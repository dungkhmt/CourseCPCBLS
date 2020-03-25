package com.company.appliaction;

import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class Packing {

    int W = 4;
    int H = 6;
    int N = 3;
    int w[] = {3, 3, 1};
    int h[] = {2, 4, 6};

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

        mgr.close();

    }

    private void search() {
        int it = 0;
        ArrayList<Move> cand = new ArrayList<>();

        while (it++ < 1_000_000 && S.violations() > 0) {
            cand.clear();
            int minDelta = Integer.MAX_VALUE;

            for (int i = 0; i < N; i++) {
                for (int x = X[i].getMinValue(); x <= X[i].getMaxValue(); x++) {
                    for (int y = Y[i].getMinValue(); y <= Y[i].getMaxValue(); y++) {
                        for (int o = O[i].getMinValue(); o <= O[i].getMaxValue(); o++) {

                            if (X[i].getValue() == x && Y[i].getValue() == y && O[i].getValue() == 0) continue;

                            int delta = S.getAssignDelta(X[i], x) + S.getAssignDelta(Y[i], y) + S.getAssignDelta(O[i], o);
                            if (delta < minDelta) {
                                cand.clear();
                                cand.add(new Move(i, x, y, o));
                                minDelta = delta;
                            } else if (delta == minDelta) {
                                cand.add(new Move(i, x, y, o));
                            }
                        }
                    }
                }
            }
            if (cand.isEmpty()) {
                System.out.println("Local Opimazation, violations = " + S.violations());
                break;
            }

            Move m = cand.get(r.nextInt(cand.size()));

            X[m.i].setValuePropagate(m.x);
            Y[m.i].setValuePropagate(m.y);
            O[m.i].setValuePropagate(m.o);

            System.out.println("Step " + it + " violations : " + S.violations());
        }
        System.out.print("X,Y,O = [");
        for (int i = 0; i < N; i++) {
            System.out.print("(" + X[i].getValue() + "," + Y[i].getValue() + "," + O[i].getValue() + "),");
        }
        System.out.println("]");
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

    public static void main(String[] args) {
        Packing p = new Packing();
        p.stateModel();
        p.search();
    }
}
