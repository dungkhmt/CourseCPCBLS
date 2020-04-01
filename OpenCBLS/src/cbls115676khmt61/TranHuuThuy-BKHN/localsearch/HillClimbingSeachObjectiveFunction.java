package com.company.localsearch;

import localsearch.constraints.alldifferent.AllDifferentVarIntLS;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;

import java.util.ArrayList;
import java.util.Random;

public class HillClimbingSeachObjectiveFunction {

    class Move {
        int i;
        int v;

        public Move(int i, int v) {
            this.i = i;
            this.v = v;
        }
    }

    private IConstraint c;
    private VarIntLS[] X;
    private IFunction F;
    int anpla, beta;
    Random r = new Random();

    public HillClimbingSeachObjectiveFunction(IFunction F, IConstraint c) {
        this.c = c;

        this.X = c.getVariables();
        anpla = 1000;
        beta =1;

        this.F  = F;
    }

    public void exploreNeighborhood(ArrayList<Move> cand) {
        cand.clear();

        for(int i = 0; i < X.length; i++){
            for(int v = X[i].getMinValue(); v<= X[i].getMaxValue(); v++){
                if (X[i].getValue() == v) {
                    continue;
                }
                int deltaC = c.getAssignDelta(X[i], v);
                int deltaF = F.getAssignDelta(X[i], v);

                if(deltaC <= 0 || deltaF <= 0) {
                    if(deltaF == 0 || deltaC == 0){
                        cand.add(new Move(i, v));
                    }
                    else if(deltaC < 0 && deltaF < 0){
                        cand.clear();
                        cand.add(new Move(i, v));
                    }
                }

            }
        }
    }

    public void search(int maxIter) {
        int it = 0;
        ArrayList<Move> cand = new ArrayList<>();
        while (it++ < maxIter && c.violations() > 0) {
            exploreNeighborhood(cand);
            if(cand.size() == 0){
                System.out.println("Local Opimazation, violations = "+c.violations()+ ", F = " + F.getValue());
                break;
            }
            Move m = cand.get(r.nextInt(cand.size()));
            X[m.i].setValuePropagate(m.v);

            System.out.println("Step "+ it  +", violations = "+c.violations() + ", F = " + F.getValue());
        }

        for(int i = 0; i < X.length; i++){
            System.out.print(X[i].getValue() + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {
        LocalSearchManager mgr = new LocalSearchManager();
        ConstraintSystem c = new ConstraintSystem(mgr);

        VarIntLS[]X = new VarIntLS[5];
        for(int i = 0;i < X.length; i++){
            X[i] = new VarIntLS(mgr, 1, 5);
        }

        c.post(new AllDifferentVarIntLS(X));
        IFunction f = new FuncPlus(new FuncMult(X[0], 3), new FuncMult(X[4], 5));
        IFunction cv = new ConstraintViolations(c);
        IFunction F = new FuncPlus(new FuncMult(cv, 1000), new FuncPlus(f, 1));

        mgr.close();

        HillClimbingSeachObjectiveFunction seacher = new HillClimbingSeachObjectiveFunction(F,c);
        seacher.search(1_000);
    }

}
