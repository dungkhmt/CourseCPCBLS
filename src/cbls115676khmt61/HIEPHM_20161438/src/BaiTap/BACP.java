package cbls115676khmt61.HIEPHM_20161438.src.BaiTap;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class BACP {
	int N =12; //so mon hoc
	int P =4; //so ki hoc
	int[] credits = {2,1,2,1,3,2,1,3,2,3,1,3};
	int[][] pre = {
			{1,0},
			{5,8},
			{4,5},
			{4,7},
			{3,10},
			{5,11}
	};
	int a =3;
	int b=3;
	int c=5;
	int d=7;
	//model
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] X;
	IFunction[] numberCoursePeriod;
	IFunction[] numberCreditsPeriod;
	
	public void stateModel() {
		mgr = new  LocalSearchManager();
		X = new VarIntLS[N];
		for(int i =0;i<N;i++)
			X[i]=new VarIntLS(mgr, 0, P-1);
		S = new ConstraintSystem(mgr);
		for(int i =0;i<pre.length;i++) {
			IConstraint c = new LessThan(X[pre[i][0]], X[pre[i][1]]);
			S.post(c);
		}
		numberCoursePeriod = new IFunction[P];
		numberCreditsPeriod = new IFunction[P];
		
		for(int j =0;j<P;j++) {
			numberCoursePeriod[j]= new ConditionalSum(X	, j);
			numberCreditsPeriod[j]=new ConditionalSum(X	, credits, j);
			S.post(new LessOrEqual(numberCoursePeriod[j], b));
			S.post(new LessOrEqual(a,numberCoursePeriod[j]));
			S.post(new LessOrEqual(numberCreditsPeriod[j], d));
			S.post(new LessOrEqual(c,numberCreditsPeriod[j]));
		}
		mgr.close();
		
	}
	
	public void search() {
		BaiTap.HillClimbingSearch searcher = new BaiTap.HillClimbingSearch();
		searcher.LocalSearch(S, 10000);
		
	}
	
	public void printResult() {
		for (int j = 0; j<P;j++) {
			System.out.println("HK "+j+": ");
			for (int i = 0; i < N; i++) {
				if(X[i].getValue()==j)
					System.out.print(i+", ");
			}
			System.out.println("number Courses = "+numberCoursePeriod[j].getValue()
					+", number credits = "+numberCreditsPeriod[j].getValue());
			
		}
		
	}
	
	public static void main(String[] args) {
		BACP app = new BACP();
		app.stateModel();
		app.search();
		app.printResult();
	}
}
