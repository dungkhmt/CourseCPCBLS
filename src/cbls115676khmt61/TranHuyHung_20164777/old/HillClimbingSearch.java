package cbls115676khmt61.TranHuyHung_20164777.old;

import java.util.ArrayList;
import localsearch.model.VarIntLS;
import localsearch.model.IConstraint;
import java.util.Random;

public class HillClimbingSearch
{
	class AssignMove
	{
	    int i;
	    int v;
	    
	    public AssignMove(final int i, final int v) {
	        this.i = i;
	        this.v = v;
	    }
	}

	
    Random R;
    
    public HillClimbingSearch() {
        this.R = new Random();
    }
    
    private void exploreNeighborhood(final IConstraint c, final VarIntLS[] x, final ArrayList<HillClimbingSearch.AssignMove> candidate) {
        int minDelta = Integer.MAX_VALUE;
        candidate.clear();
        for (int i = 0; i < x.length; ++i) {
            for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); ++v) {
                if (v != x[i].getValue()) {
                    final int delta = c.getAssignDelta(x[i], v);
                    if (delta < minDelta) {
                        candidate.clear();
                        candidate.add(new AssignMove(i, v));
                        minDelta = delta;
                    }
                    else if (delta == minDelta) {
                        candidate.add(new AssignMove(i, v));
                    }
                }
            }
        }
    }
    
    private void generateInitialSolution(final VarIntLS[] x) {
        for (int i = 0; i < x.length; ++i) {
            final int v = this.R.nextInt(x[i].getMaxValue() - x[i].getMinValue() + 1) + x[i].getMinValue();
            x[i].setValuePropagate(v);
        }
    }
    
    public void search(final IConstraint c, final int maxIter) {
        final VarIntLS[] x = c.getVariables();
        this.generateInitialSolution(x);
        int it = 0;
        final ArrayList<HillClimbingSearch.AssignMove> candidate = new ArrayList<HillClimbingSearch.AssignMove>();
        while (it < maxIter && c.violations() > 0) {
            this.exploreNeighborhood(c, x, candidate);
            if (candidate.size() == 0) {
                System.out.println("Reach local optimum");
                break;
            }
            final HillClimbingSearch.AssignMove m = candidate.get(this.R.nextInt(candidate.size()));
            x[m.i].setValuePropagate(m.v);
            ++it;
            System.out.println("Step " + it + ", violations = " + c.violations());
        }
    }
}
