/*
* Problem: CBP.java
* Description: 
* Created by ngocjr7 on [2020-03-28 21:16:09]
*/
package cbls115676khmt61.ngocbh_20164797;

import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import cbls115676khmt61.ngocbh_20164797.search.HillClimbingSearch;

public class CSP {
    int NUM_ITER=1000;
    int n = 0; // number of variables
    LocalSearchManager mgr;
    ConstraintSystem S;
    VarIntLS[] x;

    public CSP(int n) {
        this.n = n;
    }

    public void stateModel() {
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);
        x = new VarIntLS[n];
        for (int i = 0; i < n; i++) 
            x[i] = new VarIntLS(mgr,1,5);
        S.post(new NotEqual(new FuncPlus(x[2], 3), x[1]));
        S.post(new LessOrEqual(x[3], x[4]));
        S.post(new IsEqual(new FuncPlus(x[2], x[3]), new FuncPlus(x[0], 1)));
        S.post(new LessOrEqual(x[4], 3));
        S.post(new IsEqual(new FuncPlus(x[1], x[4]), 7));
        S.post(new Implicate(new IsEqual(x[2], 1), new NotEqual(x[4], 2)));

        mgr.close();
    }
    
    public void printResults() {
        System.out.printf("violations = %d, when X = ",this.S.violations());
        for (int i = 0; i < n; i++) 
            System.out.printf("%d ",x[i].getValue());
    }

    public static void main(String[] args) {
        CSP prob = new CSP(5);
        prob.stateModel();
        int seed = 6;
        HillClimbingSearch searcher = new HillClimbingSearch(seed);
        searcher.satisfy_constraint(prob.S);
        prob.printResults();
    }
}