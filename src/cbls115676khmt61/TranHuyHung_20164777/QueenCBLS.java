// 
// Decompiled by Procyon v0.5.36
// 

package cbls115676khmt61.TranHuyHung_20164777;

import localsearch.selectors.MinMaxSelector;
import localsearch.model.IConstraint;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.IFunction;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;

public class QueenCBLS
{
    int N;
    LocalSearchManager mgr;
    VarIntLS[] X;
    ConstraintSystem S;
    
    private QueenCBLS(final int N) {
        this.N = N;
    }
    
    private void stateModel() {
        this.mgr = new LocalSearchManager();
        this.X = new VarIntLS[this.N];
        for (int i = 0; i < this.N; ++i) {
            this.X[i] = new VarIntLS(this.mgr, 1, this.N);
        }
        this.S = new ConstraintSystem(this.mgr);
        final IConstraint c = (IConstraint)new AllDifferent(this.X);
        this.S.post(c);
        final IFunction[] f1 = new IFunction[this.N];
        for (int j = 0; j < this.N; ++j) {
            f1[j] = (IFunction)new FuncPlus(this.X[j], j);
        }
        this.S.post((IConstraint)new AllDifferent(f1));
        final IFunction[] f2 = new IFunction[this.N];
        for (int k = 0; k < this.N; ++k) {
            f2[k] = (IFunction)new FuncPlus(this.X[k], -k);
        }
        this.S.post((IConstraint)new AllDifferent(f2));
        this.mgr.close();
    }
    
    public void printSol() {
        for (int i = 0; i < this.N; ++i) {
            System.out.print(String.valueOf(this.X[i].getValue()) + " ");
        }
        System.out.println();
    }
    
    private void localSearch() {
        System.out.println("init, S = " + this.S.violations());
        for (int it = 1; it < 10000 && this.S.violations() > 0; ++it) {
            final MinMaxSelector mms = new MinMaxSelector((IConstraint)this.S);
            final VarIntLS sel_x = mms.selectMostViolatingVariable();
            final int sel_value = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_value);
            System.out.println("Step " + it + ", S = " + this.S.violations());
        }
        System.out.print("Best solution: ");
        this.printSol();
    }
    
    public static void main(final String[] args) {
        final QueenCBLS app = new QueenCBLS(1000);
        app.stateModel();
        app.localSearch();
    }
}
