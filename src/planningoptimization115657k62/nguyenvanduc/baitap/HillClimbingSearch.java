package planningoptimization115657k62.nguyenvanduc.baitap;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Random;

public class HillClimbingSearch {
    VarIntLS[] x;
    class Move {
        int i;
        int v;
        public Move(int i, int v) {
            this.i = i;
            this.v = v;
        }
    }


    public void search(IConstraint c, int maxIter) {
        this.x = c.getVariables();
        int it = 0;
        ArrayList<Move> cand = new ArrayList<>();
        while (it++ < maxIter && c.violations() > 0) {
            exploreNeighborhood(c, cand);
            if (cand.size() == 0) {
                System.out.println("reach local optimum");
                break;
            }

            Move m = cand.get((new Random()).nextInt(cand.size()));
            x[m.i].setValuePropagate(m.v);
            //local move
            System.out.println("step" + it + ", violations = " + c.violations());
        }
    }

    private void exploreNeighborhood(IConstraint c, ArrayList<Move> cand) {
        //khao sat lang gieng
        int minDelta = Integer.MAX_VALUE;
        cand.clear();
        for (int i = 0; i < x.length; i++) {
            for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
                if (x[i].getValue() != v) {
                    int delta = c.getAssignDelta(x[i], v);
                    //tra ve su thay doi violation khi x[i] duoc gan gia tri v

                    if (delta <= 0) {
                        if (delta < minDelta) {
                            cand.clear();
                            cand.add(new Move(i, v));
                            minDelta = delta;
                        } else if (delta == minDelta) {
                            cand.add(new Move(i, v));
                        }
                    }
                }
            }
        }

    }
}
