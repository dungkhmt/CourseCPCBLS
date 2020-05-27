package planningoptimization115657k62.nguyenvanduc.localsearchpractice;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class Sudoku {
    LocalSearchManager mgr;
    VarIntLS[][] x = new VarIntLS[9][9];
    ConstraintSystem S;
    public static void main(String[] args) {
        Sudoku app = new Sudoku();
        app.solve();
    }
    public void solve() {
        build_model();
        search(10000);
        print_solution();
    }

    private void build_model() {
        mgr = new LocalSearchManager();
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++) {
                x[i][j] = new VarIntLS(mgr, 1, 9);
            }
        }
        S = new ConstraintSystem(mgr);

        //constraints on rows
//        for (int i = 0; i < 9; i++) {
//            S.post(new AllDifferent(x[i]));
//        }

        //constraints on columns
        for (int i = 0; i < 9; i++) {
            VarIntLS[] y = new VarIntLS[9];
            for (int j = 0; j < 9; j++) {
                y[j] = x[j][i];
            }
            S.post(new AllDifferent(y));
        }

        //constraints on sub-square 3x3


        for (int i = 0; i < 9; i+=3) {
            for (int j = 0; j < 9; j+=3) {
                VarIntLS[] tmp = new VarIntLS[9];
                tmp[0] = x[i][j];
                tmp[1] = x[i][j+1];
                tmp[2] = x[i][j+2];
                tmp[3] = x[i+1][j];
                tmp[4] = x[i+1][j+1];
                tmp[5] = x[i+1][j+2];
                tmp[6] = x[i+2][j];
                tmp[7] = x[i+2][j+1];
                tmp[8] = x[i+2][j+2];
                S.post(new AllDifferent(tmp));
            }
        }

        mgr.close();
    }

    private void search(int max_iter) {
        //HillClimbingSearch s = new HillClimbingSearch();
        //s.search(S, 10000);
        gen_init_solution();
        int it = 0;
        ArrayList<Move> cand = new ArrayList<>();
        while (it++ < max_iter && S.violations() > 0) {
            explore_neighborhood(cand);
            if (cand.size() == 0) {
                System.out.println("reach local optimum!");
                break;
            }
            Move m = cand.get( (new Random()).nextInt(cand.size()) );
            x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]);
            //local move
            System.out.println("step " + it + ", violations = " + S.violations());
        }
    }

    class Move{
        int i;
        int j1;
        int j2;
        public Move(int i, int j1, int j2) {
            this.i = i;
            this.j1 = j1;
            this.j2 = j2;
        }
    }
    void explore_neighborhood(ArrayList<Move> cand) {
        int min_delta = Integer.MAX_VALUE;
        cand.clear();
        for (int i = 0; i < 9; i++) {
            for (int j1 = 0; j1 < 8; j1++) {
                for (int j2 = j1+1; j2 < 9; j2++) {
                    int delta = S.getSwapDelta(x[i][j1], x[i][j2]);
                    if (delta <= 0) {
                        if (delta < min_delta) {
                            cand.clear();
                            cand.add(new Move(i, j1, j2));
                            min_delta = delta;
                        } else if (delta == min_delta) {
                            cand.add(new Move(i, j1, j2));
                        }
                    }
                }

            }
        }
    }


    private void gen_init_solution() {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                x[i][j].setValuePropagate(j+1);
            }
        }
    }

    private void  print_solution() {
        for (int i = 0; i < 9; i++) {
            System.out.println();
            for (int j = 0; j < 9; j++) {
                System.out.print(x[i][j].getValue() + " ");
            }
        }
    }
}
