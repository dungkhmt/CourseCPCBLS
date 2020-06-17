package planningoptimization115657k62.vuduyanh;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Project17_Ortool {

    static {
        System.loadLibrary("jniortools");
    }

    int NoTest;
    int N;
    int K;
    int[] d;
    int[] v;
    int[] c1;
    int[] c2;
    Random r = new Random();
    Scanner sc = new Scanner(System.in);

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
                v = new int[N];
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
                    v[k] = Integer.parseInt(s2[k]);
                }
                for(int k = 0; k < s3.length; k++) {
                    c1[k] = Integer.parseInt(s3[k]);
                }
                for(int k = 0; k < s4.length; k++) {
                    c2[k] = Integer.parseInt(s4[k]);
                }
                //this.print();
                long start = System.currentTimeMillis();
                this.solve();
                long end = System.currentTimeMillis();
                long t = end - start;
                System.out.print("\ntime: " + t + "ms.");
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
    public void print() {
        System.out.print("d: ");
        for (int i = 0; i < d.length; i ++) {
            System.out.print(d[i] +" ");
        }
        System.out.print("\n");
        System.out.print("c: ");
        for (int i = 0; i < v.length; i ++) {
            System.out.print(v[i] + " ");
        }
        System.out.print("\n");
        System.out.print("c1: ");
        for (int i = 0; i < c1.length; i ++) {

            System.out.print(c1[i] + " ");
        }
        System.out.print("\n");
        System.out.print("c2: ");
        for (int i = 0; i < c2.length; i ++) {
            System.out.print(c2[i] + " ");
        }
    }
    public void solve() {
        MPSolver solver = new MPSolver("BP", MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
        //khởi tạo biến và miền
        MPVariable[][] x = new MPVariable[N][K];
        for(int i = 0; i < N; i++) {
            for(int j = 0; j < K; j++) {
                x[i][j] = solver.makeIntVar(0, 1, "x[" + i + "," + j + "]");
            }
        }
        MPVariable[] load = new MPVariable[K];
        for(int i = 0; i < K; i++) {
            load[i] = solver.makeIntVar(c1[i], c2[i], "load[" + i + "]");
        }
        int totalValue = 0;
        for(int i = 0; i < N; i++) {
            totalValue += v[i];
        }
        MPVariable y = solver.makeIntVar(0, totalValue, "y");
        //Thiết lập ràng buộc
        //hàng của mỗi người sẽ được giao hàng bởi đúng 1 xe hoặc không.
        for(int i = 0; i < N; i++) {
            MPConstraint c = solver.makeConstraint(0,1);
            for(int j = 0; j < K; j++) {
                c.setCoefficient(x[i][j], 1);
            }
        }
        //trọng lượng của mỗi xe không được quá c1 và c2 tương ứng
        for(int j = 0; j < K; j++) {
            MPConstraint c = solver.makeConstraint(c1[j],c2[j]);
            for(int i = 0; i < N; i++) {
                c.setCoefficient(x[i][j], d[i]);
            }
        }
        // load[j] = tổng khối lượng hàng trên xe j
        for(int j = 0; j < K; j++) {
            MPConstraint c = solver.makeConstraint(0,0);
            for(int i = 0; i < N; i++) {
                c.setCoefficient(x[i][j], v[i]);
            }
            c.setCoefficient(load[j], -1);
        }
        // y = tổng load[j]
        MPConstraint cf = solver.makeConstraint(0, 0);
        for(int j = 0; j < K; j++) {
            cf.setCoefficient(load[j], -1);
        }
        cf.setCoefficient(y, 1);
        //Khởi tạo đối tượng tối ưu.
        MPObjective obj = solver.objective();
        obj.setCoefficient(y, 1);
        obj.setMaximization();
        ResultStatus rs = solver.solve();
        if(rs != ResultStatus.OPTIMAL) {
            System.out.println("cannot find optimal solution");
        }else {
            System.out.println("Max Value = " + obj.value());
            for (int j = 0; j < K; j++) {
                int temp = 0;
                System.out.print("Xe " + j + ": ");
                System.out.print("min: " + c1[j] + ", max: " + c2[j] + "\n");
                for(int i = 0; i < N; i++) {
                    if(x[i][j].solutionValue() == 1) {
                        temp += d[i];
                        System.out.print(i + " ");
                    }
                }
                System.out.print("\nTrong luong tren xe: " + temp +"\n");
                System.out.println();
            }
        }
    }
    public static void main(String[] args) throws InterruptedException {

        Project17_Ortool app = new Project17_Ortool();
        app.readFile();
    }
}
