package baitap;

import localsearch.model.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.*;
import localsearch.search.TabuSearch;
import localsearch.selectors.*;
import localsearch.constraints.basic.*;
import java.io.PrintWriter;
import java.util.*;
import localsearch.functions.conditionalsum.ConditionalSum;

public class Container {
	
	int N = 6;
	int L = 6;
	int W = 4;

	
	int[] w = {1, 3, 2, 3, 1, 2};
	int[] l = {4, 1, 2, 1, 4, 3};
	
	VarIntLS[] x;
    VarIntLS[] y;
    VarIntLS[] o;
    
    LocalSearchManager mgr;
    ConstraintSystem S;
    
    private void stateModel(){
        mgr = new LocalSearchManager();
        S = new ConstraintSystem(mgr);
        x = new VarIntLS[N];
        y = new VarIntLS[N];
        o = new VarIntLS[N];
        
        for(int i = 0 ; i < N;  i ++){
            x[i] = new VarIntLS(mgr , 0 , W);
            y[i] = new VarIntLS(mgr , 0 , L);
            o[i] = new VarIntLS(mgr , 0 , 1);
        }
        for (int i = 0; i < N; i++) {
            S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(x[i], w[i]), W)));
            S.post(new Implicate(new IsEqual(o[i], 0), new LessOrEqual(new FuncPlus(y[i], l[i]), L)));
            S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(x[i], l[i]), W)));
            S.post(new Implicate(new IsEqual(o[i], 1), new LessOrEqual(new FuncPlus(y[i], w[i]), L)));
        }
        
        for(int i = 0 ; i < N - 1 ; i ++){
            for(int j = i + 1 ; j < N ; j ++){
                IConstraint[] c = new IConstraint[4];
                c[0] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
                c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
                c[2] = new LessOrEqual(new FuncPlus(y[i], l[i]), y[j]);
                c[3] = new LessOrEqual(new FuncPlus(y[j], l[j]), y[i]);
                
                S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(o[j], 0)), new OR(c)));
                
                c = new IConstraint[4];
                c[0] = new LessOrEqual(new FuncPlus(x[j], l[j]), x[i]);
                c[1] = new LessOrEqual(new FuncPlus(x[i], w[i]), x[j]);
                c[2] = new LessOrEqual(new FuncPlus(y[i], l[i]), y[j]);
                c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
                
                S.post(new Implicate(new AND(new IsEqual(o[i], 0), new IsEqual(o[j], 1)), new OR(c)));
                
                c = new IConstraint[4];
                c[0] = new LessOrEqual(new FuncPlus(x[j], w[j]), x[i]);
                c[1] = new LessOrEqual(new FuncPlus(x[i], l[i]), x[j]);
                c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
                c[3] = new LessOrEqual(new FuncPlus(y[j], l[j]), y[i]);
     
                S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(o[j], 0)), new OR(c)));
                
                c = new IConstraint[4];
                c[0] = new LessOrEqual(new FuncPlus(x[j], l[j]), x[i]);
                c[1] = new LessOrEqual(new FuncPlus(x[i], l[i]), x[j]);
                c[2] = new LessOrEqual(new FuncPlus(y[i], w[i]), y[j]);
                c[3] = new LessOrEqual(new FuncPlus(y[j], w[j]), y[i]);
                
                S.post(new Implicate(new AND(new IsEqual(o[i], 1), new IsEqual(o[j], 1)), new OR(c)));
            }
        }
        
//        for(int i = 0; i < N; i++) {
//        	for(j = i )
//        }
        
        mgr.close();
    }
    
    private void search(){
        HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S,  1000);
    }
    
    public void solve(){
        stateModel();
        search();
        for(int i = 0 ; i < N ; i ++){
            System.out.println("Item " + i + ": " + x[i].getValue() + " " + y[i].getValue() + " " + o[i].getValue());
        }
    }
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Container app = new Container();
//		app.stateModel();
//		app.search();
		app.solve();
	}

}
