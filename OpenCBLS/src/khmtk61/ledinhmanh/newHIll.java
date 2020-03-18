package khmtk61.ledinhmanh;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class newHIll {

    LocalSearchManager mgr;// doi tuong quan ly
    VarIntLS[][] X;// bien quyet dinh
    ConstraintSystem S;// he thong cac rang buoc

    class Move{
        int i;
        int j1;
        int j2;
        public Move(int i, int j1, int j2){
            this.i = i; this.j1 = j1; this.j2 = j2;
        }
    }

    public void stateModel() {
        mgr = new LocalSearchManager();
        X = new VarIntLS[9][9];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                X[i][j] = new VarIntLS(mgr, 1, 9);
                X[i][j].setValue(j + 1);
            }
        }

        S = new ConstraintSystem(mgr);
        for (int i = 0; i < 9; i++) {
            VarIntLS[] y = new VarIntLS[9];
            for (int j = 0; j < 9; j++)
                y[j] = X[j][i];
            S.post(new AllDifferent(y));
        }
        for (int I = 0; I <= 2; I++) {
            for (int J = 0; J <= 2; J++) {
                VarIntLS[] y = new VarIntLS[9];
                int idx = -1;
                for (int i = 0; i <= 2; i++)
                    for (int j = 0; j <= 2; j++) {
                        idx++;
                        y[idx] = X[3 * I + i][3 * J + j];
                    }
                S.post(new AllDifferent(y));
            }
        }
        mgr.close();
    }

    private void exploreNei(ArrayList<Move> cand){
        int minDelta = Integer.MAX_VALUE;
        for (int i=0; i< 9; i++){
            for (int j1= 0 ; j1 < 8; j1++){
                for (int j2 = j1+1; j2 < 9; j2++){
                    int delta = S.getSwapDelta(X[i][j1], X[i][j2]);
                    if (delta < minDelta){
                        cand.clear();
                        cand.add(new Move(i, j1, j2));
                        minDelta = delta;
                    }
                    else if(delta == minDelta){
                        cand.add(new Move(i, j1, j2));
                    }
                }
            }
        }
    }

    public void search(){
        Random R = new Random();
        ArrayList<Move> cand = new ArrayList<Move>();
        int it = 0;

        while(it <= 100000 && S.violations() > 0) {
            exploreNei(cand);
            if(cand.size() == 0){
                break;
            }
            Move  m = cand.get(R.nextInt(cand.size()));
            X[m.i][m.j1].swapValuePropagate(X[m.i][m.j2]);
            it++;
            System.out.println("Step " + it + " S = "+ S.violations());
        }

    for(int i = 0; i < 9; i++){
        for(int j = 0; j < 9; j++)
            System.out.print(X[i][j].getValue() + " ");
        System.out.println();
        }
    }

    public static void main(String[] args) {
        newHIll app = new newHIll();
        app.stateModel();
        app.search();
    }
}