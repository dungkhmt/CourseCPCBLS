package cbls115676khmt61.DoNgocSon_20163506;

import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.functions.sum.SumVar;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Liquid {
	
	//modeling
	private LocalSearchManager mgr;	// manager object
	private VarIntLS[][] X;	// decision variables
	private ConstraintSystem S;
	
	private int[] V = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	
	public Liquid() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		X = new VarIntLS[20][5];
		for(int i = 0 ; i < 20 ; i ++) {
			for(int j = 0 ; j < 5 ; j ++) {
				X[i][j] = new VarIntLS(mgr, 0, 1);
			}
		}
		
		for(int i = 0 ; i < 5 ; i ++) {
			IFunction f1 = new SumVar(new VarIntLS[] {X[0][i], X[1][i]});
			IConstraint c1 = new LessOrEqual(f1, 1);
			IFunction f2 = new SumVar(new VarIntLS[] {X[7][i], X[8][i]});
			IConstraint c2 = new LessOrEqual(f2, 1);
			IFunction f3 = new SumVar(new VarIntLS[] {X[12][i], X[17][i]});
			IConstraint c3 = new LessOrEqual(f3, 1);
			IFunction f4 = new SumVar(new VarIntLS[] {X[8][i], X[9][i]});
			IConstraint c4 = new LessOrEqual(f4, 1);
			IFunction f5 = new SumVar(new VarIntLS[] {X[1][i], X[2][i], X[9][i]});
			IConstraint c5 = new LessOrEqual(f5, 2);
			IFunction f6 = new SumVar(new VarIntLS[] {X[0][i], X[9][i], X[12][i]});
			IConstraint c6 = new LessOrEqual(f6, 2);
			
			S.post(c1);
			S.post(c2);
			S.post(c3);
			S.post(c4);
			S.post(c5);
			S.post(c6);
		}
		
		VarIntLS[] contains1 = new VarIntLS[20];
		for(int i = 0 ; i < 20 ; i ++) {
			contains1[i] = X[i][0];
		}
		S.post(new LessOrEqual(new ConditionalSum(contains1, V, 1), 60));
		
		VarIntLS[] contains2 = new VarIntLS[20];
		for(int i = 0 ; i < 20 ; i ++) {
			contains1[i] = X[i][1];
		}
		S.post(new LessOrEqual(new ConditionalSum(contains1, V, 1), 70));
		
		VarIntLS[] contains3 = new VarIntLS[20];
		for(int i = 0 ; i < 20 ; i ++) {
			contains1[i] = X[i][2];
		}
		S.post(new LessOrEqual(new ConditionalSum(contains1, V, 1), 80));
		
		VarIntLS[] contains4 = new VarIntLS[20];
		for(int i = 0 ; i < 20 ; i ++) {
			contains1[i] = X[i][3];
		}
		S.post(new LessOrEqual(new ConditionalSum(contains1, V, 1), 90));
		
		VarIntLS[] contains5 = new VarIntLS[20];
		for(int i = 0 ; i < 20 ; i ++) {
			contains1[i] = X[i][4];
		}
		S.post(new LessOrEqual(new ConditionalSum(contains1, V, 1), 100));
		
		for(int i = 0 ; i < 20 ; i ++) {
			S.post(new IsEqual(new SumVar(new VarIntLS[] {X[i][0], X[i][1], X[i][2], X[i][3], X[i][4]}), 1));
		}
		
		mgr.close();
	}
	
	public void solve() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 10000);
		
		for(int i = 0 ; i < 20 ; i ++){
			for(int j = 0 ; j < 5 ; j ++){
				if(X[i][j].getValue() > 0)
					System.out.println("CL " + i + " dung trong thung " + j);
			}
		}
		
	}
	
	public static void main(String[] args) {
		Liquid l = new Liquid();
		l.solve();
	}
	
	

}
