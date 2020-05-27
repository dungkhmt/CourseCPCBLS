package planningoptimization115657k62.nguyenvanduc.localsearchpractice;

import java.io.File;
import java.util.Scanner;

import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Template;

import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class TwoDBinPacking {
    int MAX = 100;
    int n ;
    int[] w = new int[MAX];
    int[] h = new int[MAX];
    int W, H;

    LocalSearchManager mgr;
    ConstraintSystem S;
    VarIntLS[] x;
    VarIntLS[] y;
    VarIntLS[] o;
    
    
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
    
    private void build_model() {
		mgr = new LocalSearchManager();
		x = new VarIntLS[n];
		y = new VarIntLS[n];
		o = new VarIntLS[n];
		
		for (int i = 0; i < n; i++) {
			x[i] = new VarIntLS(mgr, 0, W - 1);
			y[i] = new VarIntLS(mgr, 0, H - 1);
			o[i] = new VarIntLS(mgr, 0, 1);
		}
		
		S = new ConstraintSystem(mgr);
		
		for (int i = 0; i < n; i++) {
			FuncPlus tmp = new FuncPlus( x[i], new FuncMult(o[i], (h[i] - w[i])) )
		}
		
		
	}
    
    public void solve() {
		load_data_from_file("");
		
	}
}
