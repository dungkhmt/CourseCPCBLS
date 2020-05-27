package planningoptimization115657k62.damtrongtuyen;
import core.VarInt;
import localsearch.constraints.basic.*;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;
import org.mockito.internal.matchers.Not;

public class LiquidSearch {

    public static void main(String[] args) {

        int capacity[] = {60,70,90,80,100};

        int V[] = {20,15,10,20,20,25,30,15,10,10,
                20,25,20,10,30,40,25,35,10,10};

        int c1[][] = {{0, 1}, {7, 8}, {12, 17}, {8, 9}};

        int c2[][] = {{1, 2, 9}, {1, 9, 2}, {2, 9, 1},
                {0, 9, 12}, {0, 12, 9}, {9, 12, 0}};

        int conflict1[][] = new int[20][20];
        for (int i = 0; i < c1.length; i++) {
            int a0 = c1[i][0];
            int a1 = c1[i][1];
            conflict1[a0][a1] = 1;
            conflict1[a1][a0] = 1;
        }

        LocalSearchManager mgr = new LocalSearchManager();;
        VarIntLS[] X = new VarIntLS[20];
        for(int i = 0; i < 20; i++)
            X[i] = new VarIntLS(mgr,0,4);
        ConstraintSystem S = new ConstraintSystem(mgr);

        for (int i = 0; i < 20; i++) {// conflict 2
            for (int j = 0; j < 20; j++) {
                if(conflict1[i][j] == 1)
                for (int k = 0; k < 5; k++) {

                    {
                        S.post(new Implicate(new IsEqual(X[i], k), new NotEqual(X[j], k)));
                    }
                }

            }
        }
        for (int i = 0; i < c2.length; i++) {// truong hop 3 chat long
            int a0, a1, a2;
            a0 = c2[i][0];
            a1 = c2[i][1];
            a2 = c2[i][2];
            for (int j = 0; j < 5; j++) {
                S.post(new Implicate(new AND(new IsEqual(X[a0], j), new IsEqual(X[a1], j)), new NotEqual(X[a2], j)));
            }
        }

        ConditionalSum[] total = new ConditionalSum[5];
        for (int i = 0; i < 5; i++) {// dam bao khong bi trong dung tich cho phep
            total[i] = new ConditionalSum(X, V,i);
            S.post(new LessOrEqual(total[i], capacity[i]));
        }

        mgr.close();
        S.close();

        TabuSearch searcher = new TabuSearch();
        searcher.search(S,30, 10, 100000, 100);
        for (int i = 0; i < 20; i++) {
            System.out.println(X[i].getValue());
        }
    }
}
