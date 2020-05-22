package cbls115676khmt61.TranHuyHung_20164777;

import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.basic.FuncMult;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import java.util.ArrayList;
import java.util.Random;
import localsearch.model.VarIntLS;
import localsearch.model.IFunction;
import localsearch.model.IConstraint;

public class HillClimbingSearchCO
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
	
    private IConstraint c;
    private IFunction f;
    private VarIntLS[] X;
    private IFunction F;
    private Random R;
    
    public HillClimbingSearchCO(final IConstraint c, final IFunction f, final VarIntLS[] X, final IFunction F) {
        this.R = new Random();
        this.c = c;
        this.f = f;
        this.X = X;
        this.f = F;
    }
    
    public void exploreNeighborhoodCF(final ArrayList<HillClimbingSearchCO.Move> cand) {
        int minDeltaC = Integer.MAX_VALUE;
        int minDeltaF = Integer.MAX_VALUE;
        cand.clear();
        for (int i = 0; i < this.X.length; ++i) {
            for (int v = this.X[i].getMinValue(); v <= this.X[i].getMaxValue(); ++v) {
                if (v != this.X[i].getValue()) {
                    final int deltaC = this.c.getAssignDelta(this.X[i], v);
                    final int deltaF = this.f.getAssignDelta(this.X[i], v);
                    if (deltaC < 0 || (deltaC == 0 && deltaF < 0)) {
                        if (deltaC < minDeltaC || (deltaC == minDeltaC && deltaF < minDeltaF)) {
                            cand.clear();
                            cand.add(new HillClimbingSearchCO.Move(i, v));
                            minDeltaC = deltaC;
                            minDeltaF = deltaF;
                        }
                        else if (deltaC == minDeltaC && deltaF == minDeltaF) {
                            cand.add(new HillClimbingSearchCO.Move(i, v));
                        }
                    }
                }
            }
        }
    }
    
    public void search(final int maxIter) {
        int it = 0;
        final ArrayList<HillClimbingSearchCO.Move> cand = new ArrayList<HillClimbingSearchCO.Move>();
        while (it < maxIter) {
            this.exploreNeighborhoodCF(cand);
            if (cand.size() == 0) {
                System.out.println("Reach local optimum");
                break;
            }
            final HillClimbingSearchCO.Move m = cand.get(this.R.nextInt(cand.size()));
            this.X[m.i].setValuePropagate(m.v);
            ++it;
            System.out.println("Step " + it + ", violations = " + this.c.violations() + ", objective = " + this.f.getValue());
        }
    }
    
    public static void main(final String[] args) {
        final LocalSearchManager mgr = new LocalSearchManager();
        final VarIntLS[] X = new VarIntLS[6];
        for (int i = 0; i < 6; ++i) {
            X[i] = new VarIntLS(mgr, 1, 10);
        }
        final ConstraintSystem S = new ConstraintSystem(mgr);
        S.post((IConstraint)new AllDifferent(X));
        final IFunction f = (IFunction)new FuncPlus((IFunction)new FuncMult(X[0], 3), (IFunction)new FuncMult(X[4], 5));
        final IFunction cv = (IFunction)new ConstraintViolations((IConstraint)S);
        final IFunction F = (IFunction)new FuncPlus((IFunction)new FuncMult(cv, 1000), (IFunction)new FuncMult(f, 1));
        mgr.close();
        final HillClimbingSearchCO searcher = new HillClimbingSearchCO((IConstraint)S, f, X, F);
        searcher.search(1000);
    }
}
