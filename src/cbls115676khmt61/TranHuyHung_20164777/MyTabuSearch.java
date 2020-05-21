package cbls115676khmt61.TranHuyHung_20164777;

import java.util.ArrayList;
import java.util.Random;
import localsearch.model.VarIntLS;
import localsearch.model.IConstraint;

public class MyTabuSearch
{
	class Move
	{
	    int i;
	    int v;
	    
	    public Move(final int i, final int v) {
	        this.i = i;
	        this.v = v;
	    }
	}
	
    int[][] tabu;
    int tbl;
    IConstraint c;
    VarIntLS[] x;
    int N;
    int D;
    int bestViolations;
    Random R;
    int nic;
    
    public MyTabuSearch(final IConstraint c) {
        this.R = new Random();
        this.c = c;
        this.x = c.getVariables();
        this.N = this.x.length;
        this.D = 0;
        for (int i = 0; i < this.x.length; ++i) {
            this.D = ((this.D < this.x[i].getMaxValue()) ? this.x[i].getMaxValue() : this.D);
        }
        this.tabu = new int[this.N][this.D + 1];
        for (int i = 0; i < this.N; ++i) {
            for (int v = 0; v <= this.D; ++v) {
                this.tabu[i][v] = -1;
            }
        }
    }
    
    private void restart() {
        for (int i = 0; i < this.N; ++i) {
            final int v = this.R.nextInt(this.x[i].getMaxValue() - this.x[i].getMinValue() + 1) + this.x[i].getMinValue();
            this.x[i].setValuePropagate(v);
        }
        if (this.c.violations() < this.bestViolations) {
            this.bestViolations = this.c.violations();
        }
    }
    
    public void search(final int maxIter, final int tblen, final int maxStable) {
        this.nic = 0;
        this.tbl = tblen;
        this.bestViolations = this.c.violations();
        final ArrayList<MyTabuSearch.Move> cand = new ArrayList<MyTabuSearch.Move>();
        for (int it = 0; it <= maxIter && this.bestViolations > 0; ++it) {
            int minDelta = Integer.MAX_VALUE;
            for (int i = 0; i < this.N; ++i) {
                for (int v = this.x[i].getMinValue(); v <= this.x[i].getMaxValue(); ++v) {
                    final int delta = this.c.getAssignDelta(this.x[i], v);
                    if (this.tabu[i][v] <= it || delta + this.c.violations() < this.bestViolations) {
                        if (delta < minDelta) {
                            cand.clear();
                            cand.add(new Move(i, v));
                            minDelta = delta;
                        }
                        else if (delta == minDelta) {
                            cand.add(new Move(i, v));
                        }
                    }
                }
            }
            final MyTabuSearch.Move m = cand.get(this.R.nextInt(cand.size()));
            this.x[m.i].setValuePropagate(m.v);
            this.tabu[m.i][m.v] = it + this.tbl;
            if (this.c.violations() < this.bestViolations) {
                this.bestViolations = this.c.violations();
                this.nic = 0;
            }
            else {
                ++this.nic;
                if (this.nic >= maxStable) {
                    this.restart();
                }
            }
            System.out.println("Step " + it + " violations = " + this.c.violations() + ", best violations = " + this.bestViolations);
        }
    }
}
