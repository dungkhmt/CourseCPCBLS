package planningoptimization115657k62.nguyenvanduc;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.util.Scanner;

public class TwoDBinPacking {
    int MAX = 100;
    int n ;
    int[] w = new int[MAX];
    int[] h = new int[MAX];
    int W, H;


    private void load_data_from_file(String file_name) {
        try {
            File f = new File(file_name);
            Scanner scanner = new Scanner(f);

            W = scanner.nextInt();
            H = scanner.nextInt();
            int count = 0;
            while (true) {
                int tmp;
                tmp = scanner.nextInt();
                if (tmp == -1) break;
                w[count] = tmp;
                h[count] = scanner.nextInt();
                count++;
            }
            n = count;

        } catch (Exception e) {
            System.out.println(e.toString());
        }

    }

    private void print_input() {
        System.out.println("n = " + n);
        System.out.println("H = " + H + "W = " + W);
        for (int i = 0; i < n; i++) {
            System.out.println(w[i] + " " + h[i]);
        }
    }

    public void solve() {
        load_data_from_file("data/BinPacking2D/bin-packing-2D-W10-H8-I6.txt");
       //print_input();
        Model model = new Model();
        IntVar[] o = model.intVarArray(n, 0, 1);
        IntVar[] x = model.intVarArray(n, 0, W-1);
        IntVar[] y = model.intVarArray(n, 0, H-1);

        for (int i = 0; i < n; i++) {
            model.scalar(new IntVar[] {x[i], o[i]}, new int[] {1, h[i] - w[i]}, "<=", W - w[i]).post();
            model.scalar(new IntVar[] {y[i], o[i]}, new int[] {1, -h[i] + w[i]}, "<=", H - h[i]).post();

        }

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                Constraint c1 = model.scalar(new IntVar[] {x[i], o[i], x[j]}, new int[] {1, h[i] - w[i], -1}, "<=", -w[i]);
                Constraint c2 = model.scalar(new IntVar[] {x[j], o[j], x[i]}, new int[] {1, h[j] - w[j], -1}, "<=", -w[j]);

                Constraint c3 = model.scalar(new IntVar[] {y[i], o[i], y[j]}, new int[] {1, -h[i] + w[i], -1}, "<=",- h[i]);
                Constraint c4 = model.scalar(new IntVar[] {y[j], o[j], y[i]}, new int[] {1, -h[j] + w[j], -1}, "<=",- h[j]);
                model.or(model.or(c1, c2), model.or(c3, c4)).post();
            }
        }

        while(model.getSolver().solve()) {
            System.out.println("solution");
            for (int i = 0; i < n; i++) {
                System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
            }
        }

    }

    public static void main(String[] args) {
        TwoDBinPacking app = new TwoDBinPacking();
        app.solve();
    }
}

