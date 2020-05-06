package planningoptimization115657k62.tranthidinh;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;

public class Sudoku {
    LocalSearchManager mgr;
    VarIntLS[][] x;
    ConstraintSystem S;
    public void buildModel()
    {
        mgr = new LocalSearchManager();
        x = new VarIntLS[9][9];
        for (int i = 0; i < 9; i++)
        {
            for (int j = 0; j <0; j++)
                x[i][j] = new VarIntLS(mgr, 1, 9);
        }
        S = new ConstraintSystem(mgr);
        //rang buoc hang
        for (int i = 0; i<9; i++)
        {
            VarIntLS[] y = new VarIntLS[9];
            for (int j = 0; j<9; j++)
                y[i] = x[i][j];
            S.post(new AllDifferent(y));
        }

        //rang buoc cot
        for (int j = 0; j<9; j++)
        {
            VarIntLS[] y = new VarIntLS[9];
            for (int i = 0; i<9; i++)
                y[i] = x[i][j];
            S.post(new AllDifferent(y));
        }

        //rang buoc o 3x3
        for (int I= 0; I < 3; I++)
        {
            for (int J = 0; J < 3; J++)
            {
                VarIntLS[] y = new VarIntLS[9];
                int idx = -1;
                for (int i = 0; i < 3; i++)
                    for (int j = 0; j<3; j++)
                    {
                        idx ++;
                        y[idx] = x[3*I+i][3*J+ j];
                    }
                S.post(new AllDifferent(y));
            }
        }
       mgr.close();


    }
    public void generateInitialSolution(){
        for (int i = 0; i<9; i++)
            for (int j = 0; j < 9; j++)
                x[i][j].setValuePropagate(j+1);
    }

    class Move{
        int i;
        int j1;
        int j2;
        public Move(int i , int j1, int j2)
        {
            this.i =i;
            this.j1 = j1;
            this.j2 = j2;
        }
    }

    public void exploreNeighbor(ArrayList<Move> candidate)
    {
        candidate.clear();
        int mindelta = Integer.MAX_VALUE;
        for (int i = 0; i < 9; i++)
            for (int j1 = 0; j1 < 8; j1++)
                for (int j2=j1+1; j2 < 9; j2++) {
                    int delta = S.getSwapDelta(x[i][j1], x[i][j2]);
                    if (delta <= 0)
                    {
                        if (delta < mindelta)
                        {
                            candidate.clear();
                            candidate.add(new Move(i,j1,j2));
                            mindelta = delta;
                        }
                        else if (delta == mindelta)
                        {
                            candidate.add(new Move(i,j1,j2));
                        }
                    }
                }
    }



    public void search()
    {
        generateInitialSolution();
        Random R = new Random();
        ArrayList<Move> candidate = new ArrayList<Move>();
        int it = 0;
        while (it<10000 & S.violations() > 0)
        {
            exploreNeighbor(candidate);
            if (candidate.size() == 0)
            {
                System.out.println("Local opt");
                break;
            }
            Move m = candidate.get(R.nextInt(candidate.size()));
            x[m.i][m.j1].swapValuePropagate(x[m.i][m.j2]);
            System.out.println("step " + it + ":  S= " + S.violations());
            it++;

        }
    }

//    public void search()
//    {
//        HillClimbingSearch search = new HillClimbingSearch();
//        search.search(S, 10000);
//    }
    public static void main(String[] args){
        Sudoku app = new Sudoku();
        app.buildModel();
        app.search();

    }


}
