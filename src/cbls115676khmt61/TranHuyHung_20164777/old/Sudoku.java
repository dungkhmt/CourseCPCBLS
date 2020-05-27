package cbls115676khmt61.TranHuyHung_20164777.old;

import java.util.Random;
import java.util.Collections;
import java.util.ArrayList;
import localsearch.model.IConstraint;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.VarIntLS;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;

public class Sudoku
{
	class Move
	{
	    int i;
	    int j1;
	    int j2;
	    
	    public Move(final int r, final int x1, final int x2) {
	        this.i = r;
	        this.j1 = x1;
	        this.j2 = x2;
	    }
	}
	
    LocalSearchManager mgr;
    ConstraintSystem S;
    VarIntLS[][] X;
    
    public void stateModel() {
        this.mgr = new LocalSearchManager();
        this.X = new VarIntLS[9][9];
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                (this.X[i][j] = new VarIntLS(this.mgr, 1, 9)).setValue(j + 1);
            }
        }
        this.S = new ConstraintSystem(this.mgr);
        for (int k = 0; k < 9; ++k) {
            final VarIntLS[] y = new VarIntLS[9];
            for (int l = 0; l < 9; ++l) {
                y[l] = this.X[l][k];
            }
            this.S.post((IConstraint)new AllDifferent(y));
        }
        for (int I = 0; I < 3; ++I) {
            for (int J = 0; J < 3; ++J) {
                final VarIntLS[] y2 = new VarIntLS[9];
                int idx = -1;
                for (int m = 0; m < 3; ++m) {
                    for (int j2 = 0; j2 < 3; ++j2) {
                        ++idx;
                        y2[idx] = this.X[3 * I + m][3 * J + j2];
                    }
                }
                this.S.post((IConstraint)new AllDifferent(y2));
            }
        }
        this.mgr.close();
    }
    
    private void exploreNeighborhood(final ArrayList<Move> candidate) {
        int minDelta = Integer.MAX_VALUE;
        candidate.clear();
        for (int i = 0; i < 9; ++i) {
            for (int j1 = 0; j1 < 8; ++j1) {
                for (int j2 = j1 + 1; j2 < 9; ++j2) {
                    final int delta = this.S.getSwapDelta(this.X[i][j1], this.X[i][j2]);
                    if (delta < minDelta) {
                        candidate.clear();
                        candidate.add(new Move(i, j1, j2));
                        minDelta = delta;
                    }
                    else if (delta == minDelta) {
                        candidate.add(new Move(i, j1, j2));
                    }
                }
            }
        }
    }
    
    private void generateInitialSolution() {
        final ArrayList<Integer> permutation = new ArrayList<Integer>();
        for (int i = 0; i < 9; ++i) {
            permutation.add(i);
        }
        for (int i = 0; i < 9; ++i) {
            Collections.shuffle(permutation);
            for (int j = 0; j < 9; ++j) {
                this.X[i][j].setValuePropagate((int)permutation.get(j));
            }
        }
    }
    
    public void search(final int maxIter) {
        final Random R = new Random();
        int it = 0;
        final ArrayList<Move> candidate = new ArrayList<Move>();
        while (it < maxIter && this.S.violations() > 0) {
            this.exploreNeighborhood(candidate);
            if (candidate.size() == 0) {
                System.out.println("Reach local optimum");
                break;
            }
            final Move m = candidate.get(R.nextInt(candidate.size()));
            this.X[m.i][m.j1].swapValuePropagate(this.X[m.i][m.j2]);
            ++it;
            System.out.println("Step " + it + ", violations = " + this.S.violations());
        }
        this.printSolution();
    }
    
    private void printSolution() {
        for (int i = 0; i < 9; ++i) {
            for (int j = 0; j < 9; ++j) {
                System.out.print(String.valueOf(this.X[i][j].getValue()) + " ");
            }
            System.out.println();
        }
    }
    
    public static void main(final String[] args) {
        final Sudoku app = new Sudoku();
        app.stateModel();
        app.search(100000);
    }
}
