package planningoptimization115657k62.vuduyanh;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.SumFun;
import localsearch.functions.sum.SumVar;
import localsearch.model.*;
import org.chocosolver.solver.constraints.nary.nvalue.amnv.rules.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Random;

public class Project17_LS {

    class Move{
        int i;
        int j;
        int v;
        public Move(int i, int j, int v){
            this.i = i;
            this.j = j;
            this.v = v;
        }
    }

    private int NoTest;
    private int N;
    private int K;
    private int[] d;
    private int[] c;
    private int[] c1;
    private int[] c2;
    private VarIntLS[][] X;
    private VarIntLS[] x;
    private Random r = new Random();
    private IFunction obj;
    private LocalSearchManager lsm;
    private ConstraintSystem s;
    private List<Move> cand = new ArrayList<Move>();

    public void readFile() throws InterruptedException {
        BufferedReader br = null;
        try {
            br =  new BufferedReader(new FileReader("D:\\input.txt"));
            String r;
            r = br.readLine();
            NoTest = Integer.parseInt(r);
            for(int i = 0; i < NoTest; i++){
                r = br.readLine();
                N = Integer.parseInt(r);
                r = br.readLine();
                K = Integer.parseInt(r);
                d = new int[N];
                c = new int[N];
                c1 = new int[K];
                c2 = new int[K];
                String[] s1 = null;
                String[] s2 = null;
                String[] s3 = null;
                String[] s4 = null;
                r = br.readLine();
                s1 = r.split(" ");
                r = br.readLine();
                s2 = r.split(" ");
                r = br.readLine();
                s3 = r.split(" ");
                r = br.readLine();
                s4 = r.split(" ");
                for(int k = 0; k < s1.length; k++) {
                    d[k] = Integer.parseInt(s1[k]);
                }
                for(int k = 0; k < s2.length; k++) {
                    c[k] = Integer.parseInt(s2[k]);
                }
                for(int k = 0; k < s3.length; k++) {
                    c1[k] = Integer.parseInt(s3[k]);
                }
                for(int k = 0; k < s4.length; k++) {
                    c2[k] = Integer.parseInt(s4[k]);
                }
                long start = System.currentTimeMillis();
                this.buildModel();
                this.search2();
                long end = System.currentTimeMillis();
                long t = end - start;
                System.out.println("Time: " + t + " ms.");
                this.out();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //        return move;
    }

    public void buildModel() {
        lsm = new LocalSearchManager();
        X = new VarIntLS[N][K];
        x = new VarIntLS[N * K];
        s = new ConstraintSystem(lsm);

        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < K; j ++) {
                X[i][j] = new VarIntLS(lsm, 0, 1);
                X[i][j].setValue(0);
            }
        }

        for (int i = 0; i < N; i ++) {
            VarIntLS[] x = new VarIntLS[K];
            for (int j = 0; j < K; j ++) {
                x[j] = X[i][j];
            }
            s.post(new IsEqual(new SumVar(x), 1));
        }


        VarIntLS[][] T = new VarIntLS[K][N];
        for (int j = 0; j < K; j ++) {
            for (int i = 0; i < N; i ++) {
                T[j][i] = X[i][j];
            }

            s.post(new LessOrEqual(new ConditionalSum(T[j], d, 1), c2[j]));
            s.post(new LessOrEqual(c1[j], new ConditionalSum(T[j], d, 1)));
        }


        int k = 0;
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < K; j ++) {
                x[k] = X[i][j];
                k++;
            }
        }
        int[] y = new int[N * K];
        int u = 0;
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < K; j ++) {
                y[u] = c[i];
                u ++;
            }
        }

        IFunction[] f = new IFunction[N * K];
        for (int i = 0; i < N * K; i ++) {
            f[i] = new FuncMult(x[i], y[i]);
        }
        obj = new SumFun(f);

        lsm.close();

    }

    public void exploreNeighborhoodConstraintThenFunction(VarIntLS[][] X, IConstraint c, IFunction f, List<Move> cand) {
        cand.clear();
        int minDeltaC = Integer.MAX_VALUE;
        int minDeltaF = Integer.MAX_VALUE;
        for(int i = 0; i < X.length; i++){
            for (int j = 0; j < X[i].length; j ++) {
                for(int v = X[i][j].getMinValue(); v <= X[i][j].getMaxValue(); v++){
                    int deltaC = c.getAssignDelta(X[i][j], v);
                    int deltaF = f.getAssignDelta(X[i][j], v);
                    if(deltaC < 0 || deltaC == 0 && deltaF < 0){
                        if(deltaC < minDeltaC || (deltaC == minDeltaC && deltaF < minDeltaF)){
                            cand.clear();
                            cand.add(new Move(i, j, v));
                            minDeltaC = deltaC;
                            minDeltaF = deltaF;
                        }else if(deltaC == minDeltaC && deltaF == minDeltaF){
                            cand.add(new Move(i, j, v));
                        }
                    }
                }
            }
        }

    }

    public void move(){
        Move m = cand.get(r.nextInt(cand.size()));
        X[m.i][m.j].setValuePropagate(m.v);
    }

    public void search2(){
        int it = 0;
        while( it < 100000){
            exploreNeighborhoodConstraintThenFunction(X, s, obj, cand);
            if(cand.size() == 0){
                System.out.println("local optimum");
                break;
            }
            move();
            System.out.println("Step " + it + ": violations = " + s.violations() +
                    ", obj = " + obj.getValue());
            it++;
        }
    }

    public void out() {
        for (int j = 0; j < K; j++) {
            System.out.println("Xe " + (j + 1) + " chua:");
            int v = 0;
            for (int i = 0; i < N; i++) {
                if (X[i][j].getValue() > 0) {
                    System.out.print(i + " ");
                }
            }
            System.out.print("\n");
        }

        System.out.print("\n");
        int v = 0;
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < K; j ++) {
                if (X[i][j].getValue() == 1) {
                    v += c[i];
                }
            }
        }
        System.out.print("tong tien: " + v);
    }

    public static void main(String[] args) throws InterruptedException {
        Project17_LS app = new Project17_LS();
        app.readFile();
    }
}

