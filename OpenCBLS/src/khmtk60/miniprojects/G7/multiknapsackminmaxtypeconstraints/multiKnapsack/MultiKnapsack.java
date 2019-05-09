package khmtk60.miniprojects.G7.multiknapsackminmaxtypeconstraints.multiKnapsack;


import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.*;
import localsearch.search.TabuSearch;

import java.util.ArrayList;
import java.util.Random;

class AssignMove{
    int i;
    int v;
    public AssignMove(int i, int v) {
        this.i = i;
        this.v = v;
    }
}



public class MultiKnapsack {

    private  LocalSearchManager mgr;
    private static ConstraintSystem S;
    private VarIntLS[][] x;
    private VarIntLS[][] Y;
    private VarIntLS[][] Z;
    private int n = 16;
    private int m = 3;
    private int mt = 3;
    private int mr = 3;
    private int[] w_item = new int[]{3,2,1,6,4,7,2,4,3,3,2,5,4,1,3,2};
    private int[] p_item = new int[]{1,0,0,1,1,0,1,2,0,0,1,1,1,0,0,2};
    private int[] t_item = new int[]{0,1,0,1,1,2,0,0,1,2,0,1,1,0,0,2};
    private int[] r_item = new int[]{1,0,1,0,1,2,2,0,1,2,2,1,1,0,0,2};
    private int[] maxW = new int[]{22,17,18};
    private int[] maxP = new int[]{5,3,6};
    private int[] maxT = new int[]{2,2,2};
    private int[] maxR = new int[]{1,1,1};
    private int[][] D = new int[][]{{0,1},{1,2},{0,2},{1,2},{0,1},{0,2},{1,2},{1,2},{0,2},{0,2},{1,2},
            {0,1}, {0,2}, {1,2}, {0,1}, {0,2}};

    public void HillClimbing(IConstraint c, int maxIter) {
        VarIntLS[] y = c.getVariables();
        ArrayList<AssignMove> cand = new ArrayList<AssignMove>();

        int it = 0;
        Random R = new Random();
        while(it < maxIter && c.violations() > 0) {
            cand.clear();
            float minDelta = Integer.MAX_VALUE;
            for(int i = 0 ; i < y.length ; i++) {
                for( int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
                    float d = c.getAssignDelta(y[i], v);
                    if(d < minDelta ) {
                        cand.clear();
                        cand.add(new AssignMove(i, v));
                        minDelta = d;
                    }
                    else if(d == minDelta) {
                        cand.add(new AssignMove(i, v));
                    }
                }
            }
            int idx= R.nextInt(cand.size());
            AssignMove m  = cand.get(idx);
            y[m.i].setValuePropagate(m.v);;
            System.out.println("Step " +it + " violations = " + c.violations());
            it++;
        }
    }



    public void state(){

        mgr = new LocalSearchManager();
        x = new VarIntLS[n][m];
        Y = new VarIntLS[mt][m];
        Z = new VarIntLS[mr][m];
        Random rd = new Random();
        //init x
        for (int i = 0 ; i < n; i++){
            for (int j = 0; j < m; j++) {
                x[i][j] = new VarIntLS(mgr, 0, 1);
                //x[i][j].setValue(rd.nextInt(2));

            }
        }
        //init Y
        for (int i = 0 ; i < mt; i++){
            for (int j = 0; j < m; j++) {
                Y[i][j] = new VarIntLS(mgr, 0 , 1);
                //Y[i][j].setValue(rd.nextInt(2));
            }
        }
        //init Z
        for (int i = 0 ; i < mr; i++){
            for (int j = 0; j < m; j++) {
                Z[i][j] = new VarIntLS(mgr,0,1);
                //Z[i][j].setValue(rd.nextInt(2));
            }
        }


        S = new ConstraintSystem(mgr);



        for (int i = 0; i < n; i++){
            S.post(new IsEqual(new Sum(x[i]), 1));
            S.post(new IsEqual(new FuncPlus(x[i][D[i][0]], x[i][D[i][1]]), 1));
            //S.post(new LessOrEqual(1, new FuncPlus(x[i][D[i][0]], x[i][D[i][1]])));
        }


        IFunction[] f1 = new IFunction[n];
        for (int j =0; j < m; j++){
            for(int i = 0; i < n; i++){
                f1[i] = new FuncMult(x[i][j], w_item[i]);
            }
            S.post(new LessOrEqual(new Sum(f1), maxW[j]));
        }

        IFunction[] f2 = new IFunction[n];
        for (int j =0; j < m; j++){
            for(int i = 0; i < n; i++){
                f2[i] = new FuncMult(x[i][j], p_item[i]);
            }
            S.post(new LessOrEqual(new Sum(f2), maxP[j]));
        }


//        ConditionalSum[] a = new ConditionalSum[m];
//        for (int j = 0; j < m; j++){
//            VarIntLS[] s = new VarIntLS[n];
//            for (int i = 0; i < n; i++){
//                s[i] = x[i][j];
//            }
//            a[j] = new ConditionalSum(s, w_item, 1);
//            S.post(new LessOrEqual(a[j], maxW[j]));
//        }
//
//        ConditionalSum[] b = new ConditionalSum[m];
//        for (int j = 0; j < m; j++){
//            VarIntLS[] s = new VarIntLS[n];
//            for (int i = 0; i < n; i++){
//                s[i] = x[i][j];
//            }
//            b[j] = new ConditionalSum(s, p_item, 1);
//            S.post(new LessOrEqual(a[j], maxP[j]));
//        }




        for (int j= 0; j < m; j++){
            VarIntLS[] t = new VarIntLS[mt];
            for (int i =0; i < mt; i++){
                t[i]= Y[i][j];
            }
            S.post(new LessOrEqual(new Sum(t), maxT[j]));
        }

        for (int j= 0; j < m; j++){
            VarIntLS[] r = new VarIntLS[mr];
            for (int i =0; i < mr; i++){
                r[i]= Z[i][j];
            }
            S.post(new LessOrEqual(new Sum(r), maxR[j]));
        }




        for(int i = 0; i < n; i++){
            for (int j = 0; j < m; j++){
                S.post(new LessOrEqual(x[i][j],Y[t_item[i]][j]));
            }
        }

        for(int i = 0; i < n; i++){
            for (int j = 0; j <m; j++){
                S.post(new LessOrEqual(x[i][j],Z[r_item[i]][j]));
            }
        }

        mgr.close();
      
    }

    public void search() {
    	TabuSearch ts = new TabuSearch();
        //ts.search(S, 30, 30, 100000, 100);
        HillClimbing(S,10000);

        for (int i = 0; i < n; i++){
            for (int j=0; j < m; j++){
                System.out.print(x[i][j].getValue()+ " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        MultiKnapsack m = new MultiKnapsack();
        m.state();
        m.search();
        /*VarIntLS[] x = S.getVariables();
		HashMap<VarIntLS, Integer> map = new HashMap<VarIntLS, Integer>();
		for (int i = 0; i < x.length; i++)
			map.put(x[i], i);

		int n = x.length;
		System.out.println(n);*/
    }
}
