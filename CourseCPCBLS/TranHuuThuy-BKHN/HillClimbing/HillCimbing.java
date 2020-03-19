package com.company;
import localsearch.model.ConstraintSystem;

import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class HillCimbing {

    Random r = new Random();

    class AssignMove {
        int i;
        int v;

        public AssignMove(int i, int v) {
            this.i = i;
            this.v = v;
        }
    }

    private void generate(VarIntLS[]x){
        for(int i = 0; i < x.length; i++){
            int v = r.nextInt(x[i].getMaxValue() - x[i].getMinValue() + 1) + x[i].getMinValue();
            x[i].setValuePropagate(v);
        }
    }

    public void search(ConstraintSystem c, int maxIter) {
        VarIntLS[] x = c.getVariables();
        generate(x);

        int it = 0;
        ArrayList<AssignMove> cand = new ArrayList<>();

        while (it++ < maxIter && c.violations() > 0) {
            explore(c, x, cand);
            if (cand.isEmpty()) {
                System.out.println("Local Optimazation");
                break;
            }
            AssignMove m = cand.get(r.nextInt(cand.size()));
            x[m.i].setValuePropagate(m.v);
            System.out.println("Step " + it + " violations : " + c.violations());
        }
    }

    private void explore(ConstraintSystem c, VarIntLS[] x, ArrayList<AssignMove> cand) {
        int minDeta = Integer.MAX_VALUE;
        cand.clear();

        for (int i = 0; i < x.length; i++) {
            for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
                if(v == x[i].getValue()) continue;
                int delta = c.getAssignDelta(x[i], v);
                if (delta < minDeta) {
                    cand.clear();
                    cand.add(new AssignMove(i, v));
                    minDeta = delta;
                } else if (delta == minDeta) {
                    cand.add(new AssignMove(i, v));
                }
            }
        }
    }
}
