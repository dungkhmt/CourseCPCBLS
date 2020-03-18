package khmtk61.ledinhmanh;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.*;
import localsearch.selectors.MinMaxSelector;

public class QueenCBLS {
    //data structrers input

    int N;

    //modeling

    LocalSearchManager mgr;
    VarIntLS[] X;
    ConstraintSystem S;

    QueenCBLS(int N){
        this.N = N;
    }

    private  void stateModel(){
        mgr = new LocalSearchManager();
        X = new VarIntLS[N];
        for (int i = 0; i< N; i++){
            X[i] = new VarIntLS(mgr, 1, N); //default X[i] = 1
        }

        S = new ConstraintSystem(mgr);

        IConstraint c = new AllDifferent(X);
        S.post(c);
        IFunction[] f1 = new IFunction[N];
        for (int i =0; i<N; i++){
            f1[i] = new FuncPlus(X[i], i);
        }
        S.post(new AllDifferent(f1));

        IFunction[] f2 = new IFunction[N];
        for (int i = 0; i< N; i++){
            f2[i] = new FuncPlus(X[i], - i);
        }
        S.post(new AllDifferent(f2));

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
        QueenCBLS queen = new QueenCBLS(5000);
        queen.stateModel();
        queen.localSearch();
    }
}
