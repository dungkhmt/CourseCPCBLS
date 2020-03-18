package com.company;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class Sudoku {
    LocalSearchManager mgr;
    ConstraintSystem S;

    VarIntLS X[][];

    Random r = new Random();


    class Move {
        int i;
        int j1;
        int j2;

        public Move(int i, int j1, int j2) {
            this.i = i;
            this.j1 = j1;
            this.j2 = j2;
        }
    }

    public void stateModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);

        X = new VarIntLS[9][9];

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                X[i][j] = new VarIntLS(mgr, 1, 9);
                X[i][j].setValue(j + 1);
            }
        }

//        for (int i = 0; i < 9; i++) {
//            VarIntLS[] y = new VarIntLS[9];
//            for (int j = 0; j < 9; j++) {
//                y[j] = X[i][j];
//            }
//            S.post(new AllDifferent(y));
//        }

        for (int i = 0; i < 9; i++) {
            VarIntLS[] y = new VarIntLS[9];
            for (int j = 0; j < 9; j++) {
                y[j] = X[j][i];
            }
            S.post(new AllDifferent(y));
        }

        for (int I = 0; I < 3; I++) {
            for (int J = 0; J < 3; J++) {
                VarIntLS[] y = new VarIntLS[9];
                int idx = -1;

                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        y[++idx] = X[3 * I + i][3 * J + j];
                    }
                }
                S.post(new AllDifferent(y));
            }
        }
        mgr.close();

    }

    public void localSearch() {
        ArrayList<Move> cand = new ArrayList<>();
        int iter = 0;
        while (iter++ < 1_000_000 && S.violations() > 0) {
            int minDelta = Integer.MAX_VALUE;
            cand.clear();
            for (int i = 0; i < 9; i++) {
                for (int j1 = 0; j1 < 8; j1++) {
                    for (int j2 = j1 + 1; j2 < 9; j2++) {
                        int delta = S.getSwapDelta(X[i][j1], X[i][j2]);
                        if (delta < minDelta) {
                            cand.clear();
                            cand.add(new Move(i, j1, j2));
                            minDelta = delta;
                        } else if (delta == minDelta) {
                            cand.add(new Move(i, j1, j2));
                        }
                    }
                }
            }

            if (cand.size() == 0) {
                System.out.println("Local Opimazation");
                break;
            }

            int idx = r.nextInt(cand.size());
            Move m = cand.get(idx);
            X[m.i][m.j1].swapValuePropagate(X[m.i][m.j2]);
            System.out.println("Step " + iter + " Loss :" + S.violations());
        }

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(X[i][j].getValue() + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        Sudoku s = new Sudoku();
        s.stateModel();
        s.localSearch();
    }
}
