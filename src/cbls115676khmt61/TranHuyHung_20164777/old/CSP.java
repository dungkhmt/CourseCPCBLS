package cbls115676khmt61.TranHuyHung_20164777.old;

import localsearch.functions.sum.Sum;
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

public class CSP
{
    public static void main(final String[] args) {
        final LocalSearchManager mgr = new LocalSearchManager();
        final VarIntLS[] X = new VarIntLS[5];
        for (int i = 0; i < X.length; ++i) {
            X[i] = new VarIntLS(mgr, 1, 5);
        }
        final ConstraintSystem CS = new ConstraintSystem(mgr);
        CS.post((IConstraint)new NotEqual((IFunction)new FuncPlus(X[2], 3), X[1]));
        CS.post((IConstraint)new LessOrEqual(X[3], X[4]));
        CS.post((IConstraint)new IsEqual((IFunction)new FuncPlus(X[2], X[3]), (IFunction)new FuncPlus(X[0], 1)));
        CS.post((IConstraint)new LessOrEqual(X[4], 3));
        CS.post((IConstraint)new IsEqual((IFunction)new FuncPlus(X[1], X[4]), 7));
        CS.post((IConstraint)new Implicate((IConstraint)new IsEqual(X[2], 1), (IConstraint)new NotEqual(X[4], 2)));
        CS.post((IConstraint)new LessOrEqual(7, (IFunction)new Sum(X)));
        mgr.close();
        final HillClimbingSearch searcher = new HillClimbingSearch();
        searcher.search((IConstraint)CS, 10000);
        for (int j = 0; j < X.length; ++j) {
            System.out.println("X[" + j + "] = " + X[j].getValue());
        }
    }
}
