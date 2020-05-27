package cbls115676khmt61.TranHuyHung_20164777.old;

import localsearch.model.IFunction;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;

public class ExampleConditionalSum
{
    public static void main(final String[] args) {
        final LocalSearchManager mgr = new LocalSearchManager();
        final int M = 7;
        final int N = 3;
        final int[] w = { 3, 4, 2, 5, 7, 1, 2 };
        final VarIntLS[] X = new VarIntLS[M];
        for (int i = 0; i < M; ++i) {
            X[i] = new VarIntLS(mgr, 0, N - 1);
        }
        final IFunction f = (IFunction)new ConditionalSum(X, w, 1);
        mgr.close();
        X[0].setValuePropagate(0);
        X[1].setValuePropagate(2);
        X[2].setValuePropagate(2);
        X[3].setValuePropagate(1);
        X[4].setValuePropagate(0);
        X[5].setValuePropagate(2);
        X[6].setValuePropagate(1);
        System.out.println("f = " + f.getValue());
        final int d = f.getAssignDelta(X[6], 0);
        System.out.println("d = " + d + ", f = " + f.getValue());
        X[6].setValuePropagate(0);
        System.out.println("f = " + f.getValue());
    }
}
