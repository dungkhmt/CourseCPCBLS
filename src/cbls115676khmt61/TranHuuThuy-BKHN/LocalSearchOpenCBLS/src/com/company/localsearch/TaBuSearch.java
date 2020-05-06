package com.company.localsearch;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class TaBuSearch {
    class Move{
        int i;
        int v;

        public Move(int i, int v){
            this.i = i;
            this.v = v;
        }
    }

    int tabu[][];
    int tbl;
    VarIntLS[] X;
    int N; //number of variables
    int D; // max domain
    int bestViolations;
    ConstraintSystem C;
    Random r = new Random();
    int nc = 0, maxNC = 5;


    private void restartInitVariables(){
        for(int i = 0; i < N; i++){
            X[i].setValuePropagate(X[i].getMinValue() + r.nextInt(X[i].getMaxValue() + 1 - X[i].getMinValue()));
        }
        if(C.violations() < bestViolations){
            bestViolations = C.violations();
        }
    }

    public void searching(ConstraintSystem C, IFunction f, int maxF, int maxIteration, int tbl){
        C.post(new LessOrEqual(f, maxF));


    }


    public void searching(ConstraintSystem C, int maxIteration, int tbl) {
        this.C = C;
        this.tbl = tbl;
        X = C.getVariables();
        N = X.length;

        for (int i = 0; i < X.length; i++) {
            D = 0 < X[i].getMaxValue() ? X[i].getMaxValue() : 0;
        }

        tabu = new int[N][D + 1];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < D; j++) {
                tabu[i][j] = -1;
            }
        }

        this.tbl = tbl;
        bestViolations = C.violations();
        int it = 0;
        ArrayList<Move> cand = new ArrayList<>();
        while (it++ < maxIteration && bestViolations > 0){
            cand.clear();
            int minDeta = Integer.MAX_VALUE;
            for(int i = 0; i < N; i++){
                for(int v = X[i].getMinValue(); v<=X[i].getMaxValue(); v++){
                    int delta = C.getAssignDelta(X[i], v);
                    if(tabu[i][v] <= it || delta + C.violations() < bestViolations){
                        if(delta < minDeta){
                            cand.clear();
                            cand.add(new Move(i, v));
                            minDeta = delta;
                        }else if(delta == minDeta){
                            cand.add(new Move(i, v));
                        }
                    }
                }
            }

            if(cand.size() == 0){
                System.out.println("Local optimazation : " + bestViolations);
                break;
            }
            Move m = cand.get(r.nextInt(cand.size()));
            X[m.i].setValuePropagate(m.v);
            tabu[m.i][m.v] = it+this.tbl;
             if(C.violations() < bestViolations){
                 bestViolations = C.violations();
                 nc = 0;
             }else{
                 nc++;
                 if (nc > maxNC){
                     restartInitVariables();
                 }
             }
            System.out.println("Step " + it + " Violation:" + C.violations() + ", bestViolation : " + bestViolations);
        }

    }


}
