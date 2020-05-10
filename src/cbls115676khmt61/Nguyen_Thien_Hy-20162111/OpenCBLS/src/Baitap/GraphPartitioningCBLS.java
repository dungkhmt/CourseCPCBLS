package Baitap;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import localsearch.constraints.basic.IsEqual;
import localsearch.functions.sum.Sum;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartitioningCBLS {

    int N;
    int[][] c;

    LocalSearchManager mgr;
    VarIntLS[] x;
    IConstraint S;
    IFunction f;
    Random R = new Random();

    public void genData(String fn, int N, int M) {
        try {
            PrintWriter out = new PrintWriter(fn);
            out.println(N);
            c = new int[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    c[i][j] = 0;
                }
            }
            for (int k = 1; k <= M; k++) {
                int i = R.nextInt(N);
                int j = R.nextInt(N);
                if (i != j) {
                    int w = R.nextInt(100) + 1;
                    c[i][j] = w;
                    c[j][i] = w;
                }
            }
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    out.print(c[i][j] + " ");
                }
                out.println();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void readData(String fn) {
        try {
            Scanner in = new Scanner(new File(fn));
            N = in.nextInt();
            System.out.println(N);
            c = new int[N][N];
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    c[i][j] = in.nextInt();
                }
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void buildModel() {
        mgr = new LocalSearchManager();
        x = new VarIntLS[N];
        for (int i = 0; i < N; i++) {
            x[i] = new VarIntLS(mgr, 0, 2);
        }
        S = new IsEqual(new Sum(x), N);
        f = new GraphPartitioningCost(c, x);
        mgr.close();
    }

    public void generateInitSolutionBalance() {
        ArrayList<Integer> L = new ArrayList<Integer>();
        ArrayList<Integer> M = new ArrayList<Integer>();
        
        for(int i = 0 ; i < N ; i ++){
            L.add(i);
            M.add(i);
        }
        
        for (int k = 0; k < N / 3; k++) {
            int idx = R.nextInt(L.size());
            int idx_2 = R.nextInt(M.size());
            while(idx == idx_2){
                idx = R.nextInt(L.size());
                idx_2 = R.nextInt(M.size());
            }
            int v = L.get(idx);
            int u = M.get(idx_2);
            x[v].setValuePropagate(1);
            x[u].setValuePropagate(2);
            L.remove(idx);
            M.remove(idx_2);
        }
        for(int i = 0 ; i < N ; i ++){
            System.out.println(x[i].getValue() + " ");
        }
    }

    public String obj() {
        return "S = " + S.violations() + ", f = " + f.getValue();
    }

    class SwapMove {

        int i;
        int j;

        public SwapMove(int i, int j) {
            this.i = i;
            this.j = j;
        }
    }

    public void printGraph() {
        for (int i = 0; i < N; i++) {
            System.out.print("A[" + i + "] = ");
            for (int j = 0; j < N; j++) {
                if (c[i][j] > 0) {
                    System.out.print(j + "(" + c[i][j] + ") ");
                }
            }
            System.out.println();
        }
    }

    public void printCrossEdges() {
        for (int i = 0; i < N; i++) {
            if (x[i].getValue() == 0) {
                System.out.print(i + " ");
            }
        }
        System.out.println();
        for (int i = 0; i < N; i++) {
            if (x[i].getValue() == 1) {
                System.out.print(i + " ");
            }
        }
        
        System.out.println();
        for (int i = 0; i < N; i++) {
            if (x[i].getValue() == 2) {
                System.out.print(i + " ");
            }
        }
        System.out.println();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i != j && x[i].getValue() != x[j].getValue() && c[i][j] > 0) {
                    System.out.println("c[" + i + "," + j + "] = " + c[i][j]);
                }
            }
        }
    }

    public void search1(int maxIter) {
        generateInitSolutionBalance();
        printGraph();
        System.out.println("init obj = " + obj());
        printCrossEdges();
        ArrayList<SwapMove> cand = new ArrayList<>();
        int it = 0;
        int cur = f.getValue();
        while (it < maxIter) {
            cand.clear();
            int minDeltaF = Integer.MAX_VALUE;
            for (int i = 0; i < N; i++) {
                for (int j = i + 1; j < N; j++) {
                    if (x[i].getValue() != x[j].getValue()) {
                        System.out.println(x[i].getValue() + " " + x[j].getValue());
                        int d = f.getSwapDelta(x[i], x[j]);
                        if (d < 0) {
                            if (d < minDeltaF) {
                                minDeltaF = d;
                                cand.clear();
                                cand.add(new SwapMove(i, j));
                            } else if (d == minDeltaF) {
                                cand.add(new SwapMove(i, j));
                            }
                        }
                    }
                }
            }
            
            if (cand.isEmpty()) {
                System.out.println("Reach local optimum");
                break;
            }
            
            SwapMove m = cand.get(R.nextInt(cand.size()));
            x[m.i].swapValuePropagate(x[m.j]);
            
            if (minDeltaF + cur != f.getValue()) {
                System.out.println("BUG???, cur = " + cur + ", delta = " + minDeltaF + ", f = "
                        + f.getValue() + ", x[" + m.i + "] <-> x[" + m.j + "]");
                break;
            }
            cur = f.getValue();
            System.out.println("Step " + it + ", obj = " + obj());
            it++;
        }
    }

    class AssignMove {

        int i;
        int v;

        public AssignMove(int i, int v) {
            this.i = i;
            this.v = v;
        }
    }

    public void search2(int maxIter) {
        ArrayList<AssignMove> cand = new ArrayList<>();
        int it = 0;
        while (it < maxIter) {
            cand.clear();
            int minDeltaS = Integer.MAX_VALUE;
            int minDeltaF = Integer.MAX_VALUE;
            for (int i = 0; i < N; i++) {
                int v = 1 - x[i].getValue();
                int deltaS = S.getAssignDelta(x[i], v);
                int deltaF = f.getAssignDelta(x[i], v);
                if (deltaS < 0 || deltaS == 0 && deltaF < 0) {// hill climbing
                    if (deltaS < minDeltaS || deltaS == minDeltaS && deltaF < minDeltaF) {
                        cand.clear();
                        minDeltaS = deltaS;
                        minDeltaF = deltaF;
                        cand.add(new AssignMove(i, v));
                    } else if (deltaS == minDeltaS && deltaF == minDeltaF) {
                        cand.add(new AssignMove(i, v));
                    }
                }
            }

            if (cand.isEmpty()) {
                System.out.println("Reach  local optimum");
                break;
            }

            AssignMove m = cand.get(R.nextInt(cand.size()));

            x[m.i].setValuePropagate(m.v);

            System.out.println("Step " + it + ", obj = " + obj());
            it++;
        }
    }

    public void runExpr() {
        int nbRuns = 20;
        int[] rs = new int[nbRuns];

        for (int r = 0; r < nbRuns; r++) {
            buildModel();
            search2(10000);
            rs[r] = f.getValue();
        }
        for (int i = 0; i < nbRuns; i++) {
            System.out.println("Run " + i + ", rs = " + rs[i]);
        }
        int minf = Integer.MAX_VALUE;
        int maxf = 0;
        double avr = 0;
        double std_dev = 0;
        for (int i = 0; i < nbRuns; i++) {
            if (minf > rs[i]) {
                minf = rs[i];
            }
            if (maxf < rs[i]) {
                maxf = rs[i];
            }
            avr += rs[i];
        }
        avr = avr * 1.0 / nbRuns;
        System.out.println("minf = " + minf + " maxf = " + maxf + ", avr = " + avr);
    }
    
    void printSolution(){
        for(int i = 0 ; i < N ; i ++){
            System.out.println(x[i].getValue() + " ");
        }
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        GraphPartitioningCBLS app = new GraphPartitioningCBLS();
        String fn = "gp-1000.txt";
        app.genData(fn, 6 , 8);
        app.readData(fn);
        //app.runExpr();
        app.buildModel();
        app.search1(1000);
        app.printSolution();
    }

}
