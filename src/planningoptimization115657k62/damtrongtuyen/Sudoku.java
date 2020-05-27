package planningoptimization115657k62.damtrongtuyen;

import core.VarInt;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;

public class Sudoku {
    LocalSearchManager lsm;
    VarIntLS[][] X;
    ConstraintSystem S;

    public void initState()
    {
        lsm = new LocalSearchManager();
        X = new VarIntLS[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                X[i][j] = new VarIntLS(lsm, 1, 9);
                X[i][j].setValue(j+1);
            }
        }

        S = new ConstraintSystem(lsm);

        for (int i = 0; i < 9; i++)
        {
            VarIntLS[] y = new VarIntLS[9];
            for (int j = 0; j < 9; j++) {
                y[j] = X[i][j];
            }
            S.post(new AllDifferent(y));
        }

        for (int i = 0; i < 9; i++) {
            VarIntLS[] y = new VarIntLS[9];
            for (int j = 0; j < 9; j++) {
                y[j] = X[j][i];
            }
            S.post(new AllDifferent(y));
        }

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                VarIntLS[] y = new VarIntLS[9];
                int idx = -1;
                for (int k = 0; k < 2; k++){
                    for (int l = 0; l < 2; l++) {
                        idx++;
                        y[idx] = X[3*i+k][3*j+l];
                    }
                }
                S.post(new AllDifferent(y));
            }
        }
        S.close();
        lsm.close();
    }
    public  void search()
    {
        TabuSearch searcher = new TabuSearch();
        searcher.search(S,30, 10, 100000, 100);
    }
    public static void main(String[] args) {
        Sudoku s = new Sudoku();
        s.initState();
        s.search();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(s.X[i][j] + " ");
            }
            System.out.println();
        }
    }
}
