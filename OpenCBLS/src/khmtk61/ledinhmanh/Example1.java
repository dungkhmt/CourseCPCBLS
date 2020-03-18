package khmtk61.ledinhmanh;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;
import localsearch.selectors.MinMaxSelector;

public class Example1 {

    int N = 5;

    LocalSearchManager mgr;
    VarIntLS[] X;
    ConstraintSystem S;

    private  void stateModel(){
        mgr = new LocalSearchManager();
        X = new VarIntLS[N];
        for (int i = 0; i< N; i++){
            X[i] = new VarIntLS(mgr, 1, N); //default X[i] = 1
        }

        S = new ConstraintSystem(mgr);
        S.post(new NotEqual(new FuncPlus(X[2],3), X[1]));
        S.post(new LessOrEqual(X[3], X[4]));
        S.post(new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0], 0)));
        S.post(new LessOrEqual(X[4], 3));
        S.post(new IsEqual(new FuncPlus(X[1], X[4]), 7));
        S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));
        mgr.close(); //Ket thuc qua trinh xay dung do thi rang buoc
    }

    public void printSol(){
        for (int i = 0; i < N ; i++) {
            System.out.print(X[i].getValue() + " ");
            System.out.println();
        }
    }

    private  void localSearch(){
        int it = 1;
        while (it < 100000 && S.violations() > 0){
            MinMaxSelector mms = new MinMaxSelector(S);
            VarIntLS sel_x = mms.selectMostViolatingVariable();
            int sel_value = mms.selectMostPromissingValue(sel_x);
            sel_x.setValuePropagate(sel_value);
            System.out.print(" Step " + it + ", S = "+ S.violations());
            System.out.println();
            it ++;
        }
        System.out.println();
        System.out.println("Best solution: ");
        printSol();
    }

    public static void main(String[] args) {
        Example1 e = new Example1();
        e.stateModel();
        e.localSearch();
    }

}
