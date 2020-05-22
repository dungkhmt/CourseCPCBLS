package cbls115676khmt61.TranHuyHung_20164777;

import localsearch.selectors.MinMaxSelector;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;

public class HillClimbing
{
    LocalSearchManager mgr;
    VarIntLS[] X;
    ConstraintSystem S;
    
    private void stateModel() {
        this.mgr = new LocalSearchManager();
        this.X = new VarIntLS[5];
        for (int i = 0; i < 5; ++i) {
            this.X[i] = new VarIntLS(this.mgr, 1, 5);
        }
        (this.S = new ConstraintSystem(this.mgr)).post((IConstraint)new NotEqual((IFunction)new FuncPlus(this.X[2], 3), this.X[1]));
        this.S.post((IConstraint)new LessOrEqual(this.X[3], this.X[4]));
        this.S.post((IConstraint)new IsEqual((IFunction)new FuncPlus(this.X[2], this.X[3]), (IFunction)new FuncPlus(this.X[0], 1)));
        this.S.post((IConstraint)new LessOrEqual(this.X[4], 3));
        this.S.post((IConstraint)new IsEqual((IFunction)new FuncPlus(this.X[1], this.X[4]), 7));
        this.S.post((IConstraint)new Implicate((IConstraint)new IsEqual(this.X[2], 1), (IConstraint)new NotEqual(this.X[4], 2)));
        this.mgr.close();
    }
    
    public void printSol() {
        for (int i = 0; i < 5; ++i) {
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
        final HillClimbing app = new HillClimbing();
        app.stateModel();
        app.localSearch();
    }
}
