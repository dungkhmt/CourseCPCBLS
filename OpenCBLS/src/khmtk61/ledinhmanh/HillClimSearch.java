package khmtk61.ledinhmanh;

import com.sun.source.tree.AssignmentTree;
import core.VarInt;
import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class HillClimSearch {

    class AssignMove{
        int i;
        int v;
        public AssignMove(int i, int v){
            this.i = i;
            this.v = v;
        }
    }

//    class Move{
//        int i;
//        int j1;
//        int j2;
//        public Move(int i, int j1, int j2)
//    }

    private  void exploreNeighbor(IConstraint c, VarIntLS[] x, ArrayList<AssignMove> cand){
        int minDelta = Integer.MAX_VALUE;
        cand.clear();
        for (int i =0; i< x.length; i++){
            for (int v =x[i].getMinValue(); v <= x[i].getMaxValue(); v++){
                if (v == x[i].getValue()) continue;
                int delta = c.getAssignDelta(x[i], v);
                if( delta < minDelta){
                    cand.clear();
                    cand.add(new AssignMove(i, v));
                    minDelta = delta;
                } else if (delta == minDelta){
                    cand.add(new AssignMove(i,v));
                }
            }
        }
    }

    private void geneRandom(VarIntLS [] X){
        Random R = new Random();
        for (int i =0 ; i < X.length; i++){
            int v = R.nextInt(X[i].getMaxValue() - X[i].getMinValue() + 1) + X[i].getMinValue();
            X[i].setValuePropagate(v);
        }
    }

    public void search(IConstraint c, int maxIter){
        VarIntLS [] x = c.getVariables();
        int it = 0;
        Random R = new Random();
        ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
        while(it< maxIter && c.violations()> 0){
            exploreNeighbor(c, x, cand);
            if (cand.size() == 0){
                System.out.println("Reach local ");
            }
            AssignMove m = cand.get(R.nextInt((cand.size())));
            x[m.i].setValuePropagate(m.v);
            it++;
            System.out.println("Step "+ it + ", violations = " + c.violations());
        }
    }
}
