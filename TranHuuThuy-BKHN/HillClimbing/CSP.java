package com.company;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CSP {
    public static void main(String[] args) {
        LocalSearchManager mgr = new LocalSearchManager();
        ConstraintSystem S = new ConstraintSystem(mgr);
        VarIntLS[]X = new VarIntLS[5];
        for(int i = 0; i < 5; i++){
            X[i] = new VarIntLS(mgr, 1, 5);
        }

        S.post(new NotEqual(new FuncPlus(X[2] ,3), X[1]));
        S.post(new LessOrEqual(X[3], X[4]));
        S.post(new IsEqual(new FuncPlus(X[2], X[3]), new FuncPlus(X[0],1)));
        S.post(new LessOrEqual(X[4], 3));
        S.post(new IsEqual(new FuncPlus(X[1], X[4]), 7));
        S.post(new Implicate(new IsEqual(X[2], 1), new NotEqual(X[4], 2)));
        S.post(new LessOrEqual(new Sum(X), 12));

        mgr.close();

        HillCimbing hill = new HillCimbing();
        hill.search(S, 1_000_000);
    }
}
