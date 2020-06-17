package planningoptimization115657k62.vuduyanh;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class Project17_Choco {

    int NoTest;
    int N;
    int K;
    int[] d;
    int[] c;
    int[] c1;
    int[] c2;
    IntVar obj;
    IntVar[][] X;
    int total;
    Scanner sc = new Scanner(System.in);
    Random r = new Random();
//    int m = Integer.MAX_VALUE - 1;


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
                this.solve();
                long end = System.currentTimeMillis();
                long t = end - start;
                System.out.println("time: " + t + "ms.");
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

    public void solve() {
        Model model = new Model("Project");

        //khai bao bien va mien
        X = new IntVar[N][K];
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < K; j ++) {
                X[i][j] = model.intVar("X[" + i + "," + j + "]",0,1);
            }
        }
        obj = model.intVar("objective", 0, IntVar.MAX_INT_BOUND);

        //thiet lap rang buoc 1 hang chi dc giao boi 1 xe
        for (int i = 0; i < N; i ++) {
            IntVar[] x = new IntVar[K];
            int[] x_ = new int[K];
            for (int j = 0; j < K; j ++) {
                x[j] = X[i][j];
                x_[j] = 1;
            }
            //model.scalar(x, x_, ">=", 0).post();
            model.scalar(x, x_, "<=", 1).post();
        }

        //thiet lap rang buoc thoa man trong luong xe
        for (int j = 0; j < K; j ++) {
            IntVar[] y = new IntVar[N];
            for (int i = 0; i < N; i ++) {
                y[i] = X[i][j];
            }
            model.scalar(y, d, ">=", c1[j]).post();
            model.scalar(y, d, "<=", c2[j]).post();

        }

        //xay dung ham muc tieu
        IntVar[] t = new IntVar[N * K];
        int[] ck = new int[N * K];
        int  k = 0;
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < K; j ++) {
                t[k] = X[i][j];
                k ++;
            }
        }
        int u = 0;
        for (int i = 0; i < N; i ++) {
            for (int j = 0; j < K; j ++) {
                ck[u] = c[i];
                u ++;
            }
        }

        model.scalar(t,ck,"=",obj).post();
        model.setObjective(model.MAXIMIZE, obj);

        model.getSolver().solve();

        System.out.print("Ket qua: " + obj.getValue() + "\n");
        for (int j = 0; j < K; j++) {
            int temp = 0;
            System.out.print("Xe " + j + ": ");
            System.out.print("min: " + c1[j] + ", max: " + c2[j] + "\n");
            for(int i = 0; i < N; i++) {
                if(X[i][j].getValue() == 1) {
                    temp += d[i];
                    System.out.print(i + " ");
                }
            }
            System.out.print("\nTrong luong tren xe: " + temp +"\n");
            System.out.println();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Project17_Choco app = new Project17_Choco();
        app.readFile();
    }

}
