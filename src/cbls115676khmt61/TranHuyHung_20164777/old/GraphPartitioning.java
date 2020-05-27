package cbls115676khmt61.TranHuyHung_20164777.old;

import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncMinus;
import localsearch.model.IConstraint;
import localsearch.constraints.basic.IsEqual;
import localsearch.functions.sum.Sum;
import localsearch.model.IFunction;
import localsearch.model.ConstraintSystem;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;

public class GraphPartitioning
{
	class Move
	{
	    int i;
	    int j;
	    
	    public Move(final int i, final int j) {
	        this.i = i;
	        this.j = j;
	    }
	}
	
    private int[][] E;
    private int N;
    private LocalSearchManager mgr;
    private VarIntLS[] X;
    private ConstraintSystem S;
    private IFunction f;
    
    public GraphPartitioning() {
        this.E = new int[][] { { 1, 2, 8 }, { 1, 3, 2 }, { 1, 7, 3 }, { 2, 3, 8 }, { 2, 4, 7 }, { 2, 7, 4 }, { 2, 8, 6 }, { 0, 2, 5 }, { 3, 5, 1 }, { 3, 8, 5 }, { 4, 6, 8 }, { 4, 7, 9 }, { 0, 4, 1 }, { 5, 9, 5 }, { 5, 8, 4 }, { 6, 9, 4 }, { 0, 6, 7 }, { 0, 8, 2 }, { 0, 9, 8 } };
        this.N = 10;
    }
    
    private void stateModel() {
        this.mgr = new LocalSearchManager();
        this.S = new ConstraintSystem(this.mgr);
        this.X = new VarIntLS[this.N];
        for (int i = 0; i < this.N; ++i) {
            this.X[i] = new VarIntLS(this.mgr, 0, 1);
        }
        this.S.post((IConstraint)new IsEqual((IFunction)new Sum(this.X), this.N / 2));
        final IFunction[] w = new IFunction[this.E.length];
        for (int j = 0; j < this.E.length; ++j) {
            final int[] e = this.E[j];
            final IFunction part = (IFunction)new FuncMinus(this.X[e[0]], this.X[e[1]]);
            w[j] = (IFunction)new FuncMult((IFunction)new FuncMult(part, part), e[2]);
        }
        this.f = (IFunction)new Sum(w);
        this.mgr.close();
    }
    
    private void generateInitialSolution() {
        final ArrayList<Integer> sol = new ArrayList<Integer>();
        for (int i = 0; i < this.N / 2; ++i) {
            sol.add(0);
            sol.add(1);
        }
        Collections.shuffle(sol);
        for (int i = 0; i < this.N; ++i) {
            this.X[i].setValuePropagate((int)sol.get(i));
        }
    }
    
    private void exploreNeighborhood(final ArrayList<GraphPartitioning.Move> candidate) {
        int minDelta = 0;
        candidate.clear();
        for (int i = 0; i < this.N; ++i) {
            for (int j = i + 1; j < this.N; ++j) {
                final int delta = this.f.getSwapDelta(this.X[i], this.X[j]);
                if (delta < minDelta) {
                    candidate.clear();
                    candidate.add(new Move(i, j));
                    minDelta = delta;
                }
            }
        }
    }
    
    public void search(final int maxIter) {
        final Random R = new Random();
        this.generateInitialSolution();
        int it = 0;
        final ArrayList<GraphPartitioning.Move> candidate = new ArrayList<GraphPartitioning.Move>();
        while (it < maxIter) {
            this.exploreNeighborhood(candidate);
            if (candidate.size() == 0) {
                System.out.println("Reach local optimum");
                break;
            }
            final GraphPartitioning.Move m = candidate.get(R.nextInt(candidate.size()));
            this.X[m.i].swapValuePropagate(this.X[m.j]);
            ++it;
            System.out.println("Step " + it + ", objective = " + this.f.getValue());
        }
        this.printSolution();
    }
    
    private void printSolution() {
        System.out.print("Part:");
        for (int i = 0; i < this.N; ++i) {
            System.out.print(" " + this.X[i].getValue());
        }
    }
    
    public static void main(final String[] args) {
        final GraphPartitioning app = new GraphPartitioning();
        app.stateModel();
        app.search(10);
    }
}
