package khmtk60.miniprojects.G10.custom.search;

import khmtk60.miniprojects.G10.localsearch.model.IConstraint;
import khmtk60.miniprojects.G10.localsearch.model.IFunction;
import khmtk60.miniprojects.G10.localsearch.model.VarIntLS;
import khmtk60.miniprojects.G10.localsearch.search.MoveType;
import khmtk60.miniprojects.G10.localsearch.search.OneVariableValueMove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CustomTabu {
    private Random rand = null;
    private double t_best;
    private double t0;
    private double t;

    public CustomTabu() {
        rand = new Random();
    }

    public String name() {
        return "CustomTabuSearch";
    }

    private void restartMaintainConstraint(VarIntLS[] x, IConstraint S,
                                           int[][] tabu) {

        for (int i = 0; i < x.length; i++) {
            ArrayList<Integer> L = new ArrayList<Integer>();
            for (int v: x[i].getDomain()) {
                if (S.getAssignDelta(x[i], v) <= 0)
                    L.add(v);
            }
            int idx = rand.nextInt(L.size());
            int v = L.get(idx);
            x[i].setValuePropagate(v);
        }
        for (int i = 0; i < tabu.length; i++) {
            for (int j = 0; j < tabu[i].length; j++)
                tabu[i][j] = -1;
        }

    }

    public void updateBest(){

    }

    public void searchMaintainConstraintsMinimize(IFunction f, IConstraint S,
                                                  int tabulen, int maxTime, int maxIter, int maxStable) {
        double t0 = System.currentTimeMillis();

        VarIntLS[] x = S.getVariables();
        HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
        for (int i = 0; i < x.length; i++)
            map.put(x[i], i);

        int n = x.length;
        int maxV = -1000000000;
        int minV = 100000000;
        for (int i = 0; i < n; i++) {
            if (maxV < x[i].getMaxValue())
                maxV = x[i].getMaxValue();
            if (minV > x[i].getMinValue())
                minV = x[i].getMinValue();
        }
        int D = maxV - minV;
        int tabu[][] = new int[n][D + 1];
        for (int i = 0; i < n; i++)
            for (int v = 0; v <= D; v++)
                tabu[i][v] = -1;

        int it = 0;
        maxTime = maxTime * 1000;// convert into milliseconds

        int best = f.getValue();
        int[] x_best = new int[x.length];
        for (int i = 0; i < x.length; i++)
            x_best[i] = x[i].getValue();

        System.out.println(name()
                + "::searchMaintainConstraintsMinimize, init S = "
                + S.violations());
        int nic = 0;
        ArrayList<OneVariableValueMove> moves = new ArrayList<OneVariableValueMove>();
        Random R = new Random();

        while (it < maxIter && System.currentTimeMillis() - t0 < maxTime) {
            int sel_i = -1;
            int sel_v = -1;
            int minDelta = 10000000;
            moves.clear();
            for (int i = 0; i < n; i++) {
                for (int v : x[i].getDomain()) {
                    int deltaS = S.getAssignDelta(x[i], v);
                    int deltaF = f.getAssignDelta(x[i], v);
                    // System.out.println("min  =   "+x[i].getMinValue()+"   max =     "+x[i].getMaxValue());
                    /*
                     * Accept moves that are not tabu or they are better than
                     * the best solution found so far (best)
                     */
                    if (deltaS <= 0)
                        if (tabu[i][v - minV] <= it
                                || f.getValue() + deltaF < best) {
                            if (deltaF < minDelta) {
                                minDelta = deltaF;
                                sel_i = i;
                                sel_v = v;
                                moves.clear();
                                moves.add(new OneVariableValueMove(
                                        MoveType.OneVariableValueAssignment,
                                        minDelta, x[i], v));
                            } else if (deltaF == minDelta) {
                                moves.add(new OneVariableValueMove(
                                        MoveType.OneVariableValueAssignment,
                                        minDelta, x[i], v));
                            }
                        }
                }
            }

            // perform the move
            if (moves.size() <= 0) {
                System.out
                        .println(name()
                                + "::searchMaintainConstraintsMinimize --> restart.....");
                restartMaintainConstraint(x, S, tabu);
                nic = 0;
            } else {
                OneVariableValueMove m = moves.get(R.nextInt(moves.size()));
                sel_i = map.get(m.getVariable());
                sel_v = m.getValue();
                x[sel_i].setValuePropagate(sel_v);
                tabu[sel_i][sel_v - minV] = it + tabulen;
                System.out.println(name()
                        + "::searchMaintainConstraintsMinimize, Step " + it
                        + ", S = " + S.violations() + ", f = " + f.getValue()
                        + ", best = " + best + ", delta = " + minDelta
                        + ", nic = " + nic);
                // update best
                if (f.getValue() < best) {
                    best = f.getValue();
                    for (int i = 0; i < x.length; i++)
                        x_best[i] = x[i].getValue();
                    updateBest();
                    t_best = System.currentTimeMillis() - t0;
                }

                //if (minDelta >= 0) {
                if(f.getValue() >= best){
                    nic++;
                    if (nic > maxStable) {
                        System.out
                                .println(name()
                                        + "::searchMaintainConstraintsMinimize  -> restart.....");
                        restartMaintainConstraint(x, S, tabu);
                        nic = 0;
                    }
                } else {
                    nic = 0;
                }
            }
            it++;
        }
        for (int i = 0; i < x.length; i++)
            x[i].setValuePropagate(x_best[i]);

    }


}

