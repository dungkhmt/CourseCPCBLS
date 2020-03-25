package Baitap;

import java.util.ArrayList;
import java.util.Random;
import localsearch.model.*;

/**
 *
 * @author hydon
 */

public class TabuSearch {
    class Move{
        int i;
        int v;
        public Move(int i , int v){
            this.i = i;
            this.v = v;
        }
    }
    int tabu[][];
    int tbl;
    IConstraint c;
    VarIntLS[] X;
    int N; // number of variables
    int D; // max domain
    int bestviolation;
    int nic = 0;
    Random R = new Random();
    
    public TabuSearch(IConstraint c){
        X = c.getVariables();
        N = X.length;
        D = 0;
        for(int i = 0 ; i < X.length ; i ++){
            D = D < X[i].getMaxValue() ? X[i].getMaxValue() : D;
        }
        tabu = new int[N][D + 1];
        for(int i = 0 ; i < N ; i ++){
            for(int v = 0 ; v <= D ; v++){
                tabu[i][v] = -1;
            }
        }
    }
    private void restart(){
        for(int i = 0 ; i < N ; i ++){
            int v = R.nextInt(X[i].getMaxValue() - X[i].getMinValue() + 1) + X[i].getMinValue();
            X[i].setValuePropagate(v);
        }
    }
    public void search(int maxIter , int tblen , int maxStable){
        this.tbl = tblen;
        bestviolation = c.violations();
        ArrayList<Move> cand = new ArrayList<>();
        cand.clear();
        nic = 0;
        int it = 0;
        while(it <= maxIter && bestviolation > 0){
            int minDelta = Integer.MAX_VALUE;
            for(int i = 0 ; i < N ; i ++){
                for(int v = X[i].getMinValue() ; v <= X[i].getMaxValue(); v ++){
                    if(X[i].getValue() != v){
                        int delta = c.getAssignDelta(X[i], v);
                        if(tabu[i][v] <= it || delta + c.violations() < bestviolation){
                            if(delta < minDelta){
                                cand.clear();
                                cand.add(new Move(i , v));
                                minDelta = delta;
                            }
                            else if(delta == minDelta){
                                cand.add(new Move(i , v));
                            }
                        }
                    }
                }
            }
        }
        Move m = cand.get(R.nextInt(cand.size()));
        X[m.i].setValuePropagate(m.v);
        tabu[m.i][m.v] = it + tbl;
        if(c.violations() < bestviolation){
            bestviolation = c.violations();
            nic = 0;
        }
        else{
            nic ++;
            if(nic >= maxStable){
                restart();
            }
        }
        System.out.println("Step " + it + " violations = " + c.violations()
                            + ", bestViolations = " + bestviolation);
        it ++;
    }
}
