package planningoptimization115657k62.nguyenvanduc.localsearchpractice;

import java.io.File;
import java.util.Scanner;


import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.OR;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.TabuSearch;
import localsearch.selectors.MinMaxSelector;

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
    
    public static void main(String[] Args) {
    	TwoDBinPacking app = new TwoDBinPacking();
    	app.solve();
    }
    
    public void solve() {
		load_data_from_file("data/BinPacking2D/bin-packing-2D-W19-H18-I21.txt");
		build_model();
		search();
		
	}
    
    
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
        S = new ConstraintSystem(mgr);
        for (int i = 0; i < n; i++) {
            x[i] = new VarIntLS(mgr, 0, W-1);
            y[i] = new VarIntLS(mgr, 0, H-1);
            o[i] = new VarIntLS(mgr, 0, 1);
        }

        //1. khong vuot khoi 2 bien
        for (int i = 0; i < n; i++) {
            S.post(new LessOrEqual(
                    new FuncPlus(
                            new FuncMult(x[i], 1),
                            new FuncMult(o[i], h[i]-w[i])
                    ),
                    W - w[i]
            ));

            S.post(new LessOrEqual(
                    new FuncPlus(
                            new FuncMult(y[i], 1),
                            new FuncMult(o[i], -h[i]+w[i])
                    ),
                    H - h[i]
            ));
        }

        //2. khong overlap
        for (int i = 0; i < n -1; i++) {
            for (int j = i+1; j < n; j++) {
                IFunction[] f1 = new IFunction[3];
                f1[0] = new FuncMult(x[i], 1);
                f1[1] = new FuncMult(o[i], h[i]-w[i]);
                f1[2] = new FuncMult(x[j], -1);
                IConstraint c1 = new LessOrEqual(new Sum(f1), -w[i]);

                IFunction[] f2 = new IFunction[3];
                f2[0] = new FuncMult(x[j], 1);
                f2[1] = new FuncMult(o[j], h[j]-w[j]);
                f2[2] = new FuncMult(x[i], -1);
                IConstraint c2 = new LessOrEqual(new Sum(f2), -w[j]);

                IFunction[] f3 = new IFunction[3];
                f3[0] = new FuncMult(y[i], 1);
                f3[1] = new FuncMult(o[i], -h[i]+w[i]);
                f3[2] = new FuncMult(y[j], -1);
                IConstraint c3 = new LessOrEqual(new Sum(f3), -h[i]);

                IFunction[] f4 = new IFunction[3];
                f4[0] = new FuncMult(y[j], 1);
                f4[1] = new FuncMult(o[j], -h[j]+w[j]);
                f4[2] = new FuncMult(y[i], -1);
                IConstraint c4 = new LessOrEqual(new Sum(f4), -h[j]);

                IConstraint cs[] = new IConstraint[4];
                cs[0] = c1; cs[1] = c2; cs[2] = c3; cs[3] = c4;
                S.post(new OR(cs));
            }
        }
        
        mgr.close();
		
		
	}
    
    public void search(){
      HillClimbingSearch s = new HillClimbingSearch();
      s.search(S, 10000);
//      TabuSearch searcher = new TabuSearch();
//      searcher.search(S, 30, 10, 100000, 100);

//      MinMaxSelector mms = new MinMaxSelector(S);
//      int it = 0;
//      while (it++ < 50000 && S.violations() > 0) {
//          VarIntLS sel_x = mms.selectMostViolatingVariable();
//          int sel_v = mms.selectMostPromissingValue(sel_x);
//
//          sel_x.setValuePropagate(sel_v); // local move
//          System.out.println("step " + it  + ", violations =  " + S.violations());
//      }

      System.out.println("solution");
      System.out.println("x-y-o");
      for (int i = 0; i < n; i++) {
          System.out.println(x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
      }
    }
    
   
}
