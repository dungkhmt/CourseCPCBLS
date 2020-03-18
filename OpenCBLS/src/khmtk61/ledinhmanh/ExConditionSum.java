package khmtk61.ledinhmanh;

import core.VarInt;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ExConditionSum {

    public static void main(String[] args) {
        LocalSearchManager mgr = new LocalSearchManager();
        int N = 3;
        int M = 7;
        int[] w = {3,4,2,5,7,1,2};
        VarIntLS[] X = new VarIntLS[M];
        for (int i = 0; i< M; i++){
            X[i] = new VarIntLS(mgr, 0, N-1);
        }
        IFunction f = new ConditionalSum(X, w, 1);
        mgr.close();

        X[0].setValuePropagate(0);
        X[1].setValuePropagate(2);
        X[2].setValuePropagate(2);
        X[3].setValuePropagate(1);
        X[4].setValuePropagate(0);
        X[5].setValuePropagate(2);
        X[6].setValuePropagate(1);
        System.out.println(f.getValue());
        X[4].setValuePropagate(1);
        System.out.println(f.getValue());
    }

}
