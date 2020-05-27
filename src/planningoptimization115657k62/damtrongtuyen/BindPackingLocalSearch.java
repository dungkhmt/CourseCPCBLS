package planningoptimization115657k62.damtrongtuyen;
import core.VarInt;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;
public class BindPackingLocalSearch {

    public static void main(String[] args) {
        System.out.println("hello world ");

        int N=3, W=4, H=6;
        int[] w = {3, 3, 1};
        int[] h = {2, 4, 6};

        LocalSearchManager lsm = new LocalSearchManager();
        ConstraintSystem s = new ConstraintSystem(lsm);
        VarIntLS X[] = new VarIntLS[N];
        VarIntLS Y[] = new VarIntLS[N];
        VarIntLS O[] = new VarIntLS[N];
        for (int i = 0; i < N; i++) {
            X[i] = new VarIntLS(lsm, 0, W);
            Y[i] = new VarIntLS(lsm, 0, H);
            O[i] = new VarIntLS(lsm, 0, 1);
        }

        for (int i = 0; i < N; i++) {
            s.post(new Implicate(new IsEqual(O[i], 0), new LessOrEqual(new FuncPlus(X[i], w[i]), W)));
            s.post(new Implicate(new IsEqual(O[i], 0), new LessOrEqual(new FuncPlus(Y[i], h[i]), H)));
            s.post(new Implicate(new IsEqual(O[i], 1), new LessOrEqual(new FuncPlus(X[i], h[i]), W)));
            s.post(new Implicate(new IsEqual(O[i], 1), new LessOrEqual(new FuncPlus(X[i], w[i]), W)));


        }
// not overlap
        for (int i = 0; i < N - 1; i++) {
            for (int j = i+1; j < N; j++) {
                IConstraint[] constrtaint = new IConstraint[4];
                // 0, 0
                constrtaint[0] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
                constrtaint[1] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
                constrtaint[2] = new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]);
                constrtaint[3] = new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]);
                s.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 0)), new OR(constrtaint)));

                constrtaint = new IConstraint[4];
                //0, 1
                constrtaint[0] = new LessOrEqual(new FuncPlus(X[j], h[j]), X[i]);
                constrtaint[1] = new LessOrEqual(new FuncPlus(X[i], w[i]), X[j]);
                constrtaint[2] = new LessOrEqual(new FuncPlus(Y[i], h[i]), Y[j]);
                constrtaint[3] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
                s.post(new Implicate(new AND(new IsEqual(O[i], 0), new IsEqual(O[j], 1)), new OR(constrtaint)));

                constrtaint = new IConstraint[4];
                // 1, 0
                constrtaint[0] = new LessOrEqual(new FuncPlus(X[j], w[j]), X[i]);
                constrtaint[1] = new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]);
                constrtaint[2] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
                constrtaint[3] = new LessOrEqual(new FuncPlus(Y[j], h[j]), Y[i]);
                s.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 0)), new OR(constrtaint)));

                constrtaint = new IConstraint[4];
                //1,1
                constrtaint[0] = new LessOrEqual(new FuncPlus(X[j], h[j]), X[i]);
                constrtaint[1] = new LessOrEqual(new FuncPlus(X[i], h[i]), X[j]);
                constrtaint[2] = new LessOrEqual(new FuncPlus(Y[i], w[i]), Y[j]);
                constrtaint[3] = new LessOrEqual(new FuncPlus(Y[j], w[j]), Y[i]);
                s.post(new Implicate(new AND(new IsEqual(O[i], 1), new IsEqual(O[j], 1)), new OR(constrtaint)));

            }
        }
        lsm.close();
        s.close();

        TabuSearch ts = new TabuSearch();
        ts.search(s, 100, 100, 100000, 50);

        for (int i = 0; i < N; i++) {
            System.out.println(X[i].getValue() + "  " + Y[i].getValue() + "   " + O[i].getValue());
        }

    }
}
