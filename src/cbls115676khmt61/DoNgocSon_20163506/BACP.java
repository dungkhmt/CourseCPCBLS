package cbls115676khmt61.DoNgocSon_20163506;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class BACP {
	int N  = 12; // so mon hoc 0,1,2,...
	int P = 4; // so hoc ky 0,1,2,...
	int[] credits = {2, 1, 2, 1, 3, 2, 1, 3, 2, 3, 1, 3};
	int[][] pre = {
			{1, 0},
			{5, 8},
			{4, 5},
			{4, 7},
			{3, 10},
			{5, 11}
	};
	int a = 3, b = 3, c = 5, d = 7;
	
	// model
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	IFunction[] numberCoursesPeriod; //so mon hoc moi hoc ky
	IFunction[] numberCreditsPeriod; //so tin chi cac mon hoc moi hoc ky
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for (int i=0; i<N; i++)
			X[i] = new VarIntLS(mgr, 0, P-1);
		
		S = new ConstraintSystem(mgr);
		
		for (int k=0; k<pre.length; k++) {
			IConstraint c = new LessThan(X[pre[k][0]], X[pre[k][1]]);
			S.post(c);
		}
		
		numberCoursesPeriod = new IFunction[P];
		numberCreditsPeriod = new IFunction[P];
		
		for (int j=0; j<P; j++) {
			numberCoursesPeriod[j] = new ConditionalSum(X, j);
			numberCreditsPeriod[j] = new ConditionalSum(X, credits, j);
			
			S.post(new LessOrEqual(numberCoursesPeriod[j], b));
			S.post(new LessOrEqual(a, numberCoursesPeriod[j]));
			S.post(new LessOrEqual(numberCreditsPeriod[j], d));
			S.post(new LessOrEqual(c, numberCreditsPeriod[j]));
		}
		
		// S.post(new LessOrEqual(new MySum(X), 12));
		mgr.close(); // create dependency graph
	}
	
	public void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 10000);
	}
	
	public void printResult() {
		for (int j=0; j<P; j++) {
			System.out.print("HK " + j + ": ");
			for (int i=0; i<N; i++)
				if (X[i].getValue() == j)
					System.out.print(i + ", ");
			System.out.print("number courses = " + numberCoursesPeriod[j].getValue());
			System.out.println(", number credits = " + numberCreditsPeriod[j].getValue());
		}
	}
	
	public static void main(String[] args) {
		BACP app = new BACP();
		app.stateModel();
		app.search();
		app.printResult();
	}
}
