package planningoptimization115657k62.vuduyanh;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.SumVar;
import localsearch.model.*;

public class Liquid {

    LocalSearchManager lsm;
    VarIntLS[][] X;
    ConstraintSystem S;

    int[] V = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
    int[] T = {60, 70, 90, 80, 100};

    public void buildModel() {
        lsm = new LocalSearchManager();
        S = new ConstraintSystem(lsm);
        X = new VarIntLS[20][5];

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 5; j++) {
                X[i][j] = new VarIntLS(lsm, 0, 1);
            }
        }
        VarIntLS[][] t = new VarIntLS[5][20];
        for (int j = 0; j < 5; j ++) {
            for (int i = 0; i < 20; i ++) {
                t[j][i] = X[i][j];
            }
            S.post(new LessOrEqual(new ConditionalSum(t[j], V, 1), T[j]));
        }

        for (int i = 0; i < 20; i++) {
            S.post(new IsEqual(new SumVar(new VarIntLS[]{X[i][0], X[i][1], X[i][2], X[i][3], X[i][4]}), 1));
        }

        for (int i = 0; i < 5; i++) {
            IConstraint c1 = new LessOrEqual(new SumVar(new VarIntLS[]{X[0][i], X[1][i]}), 1);
            IConstraint c2 = new LessOrEqual(new SumVar(new VarIntLS[]{X[7][i], X[8][i]}), 1);
            IConstraint c3 = new LessOrEqual(new SumVar(new VarIntLS[]{X[12][i], X[17][i]}), 1);
            IConstraint c4 = new LessOrEqual(new SumVar(new VarIntLS[]{X[8][i], X[9][i]}), 1);
            IConstraint c5 = new LessOrEqual(new SumVar(new VarIntLS[]{X[1][i], X[9][i], X[12][i]}), 2);
            IConstraint c6 = new LessOrEqual(new SumVar(new VarIntLS[]{X[0][i], X[9][i], X[12][i]}), 2);

            S.post(c1);
            S.post(c2);
            S.post(c3);
            S.post(c4);
            S.post(c5);
            S.post(c6);
        }

        lsm.close();

    }

    public void solve() {
        HillClimbingSearch search = new HillClimbingSearch();
        search.search(S, 1000);

        for (int j = 0; j < 5; j++) {
            System.out.println("Thung " + (j + 1) + " chua:");
            int v = 0;
            for (int i = 0; i < 20; i++) {
                if (X[i][j].getValue() > 0) {
                    System.out.println(i);
                    v += V[i];
                }
            }
            v = T[j] - v;
            System.out.println("The tich con lai trong thung: " + v);
        }
    }

    public static void main(String[] args) {
        Liquid app = new Liquid();
        app.buildModel();
        app.solve();
    }

}