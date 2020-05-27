package cbls115676khmt61.ngodoannghia_20162905;


import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;


//Bai toan xep mon hoc
public class Buoi5 {
	int N = 12; // so mon hoc
	int P = 4; // ky hoc
	int[] credits = {2,1,2,1,3,2,1,3,2,3,1,3};
	int[][] pre = {
			{1,0},
			{5,8},
			{4,5},
			{4,7},
			{3,10},
			{5,11}
	};
	int a = 3, b = 3, c = 3, d = 7;
	//len mo hinh
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	IFunction[] numberCoursesPreiod;
	IFunction[] numberCreditsPeriod;
	
	public void StateModel(){
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for(int i = 0; i < N; i++){
			X[i] = new VarIntLS(mgr, 0, P-1);
		}
		S = new ConstraintSystem(mgr);
		
		for(int k = 0; k < pre.length; k++){
			IConstraint c = new LessThan(X[pre[k][0]], X[pre[k][1]]);
			S.post(c);
		}
		numberCoursesPreiod = new IFunction[P];
		numberCreditsPeriod = new IFunction[P];
		
		for(int j = 0; j < P; j++){
			numberCoursesPreiod[j] = new ConditionalSum(X,j);
			numberCreditsPeriod[j] = new ConditionalSum(X, credits, j);
			
			S.post(new LessOrEqual(numberCoursesPreiod[j], b));
			S.post(new LessOrEqual(a, numberCoursesPreiod[j]));
			S.post(new LessOrEqual(numberCreditsPeriod[j], d));
			S.post(new LessOrEqual(c, numberCreditsPeriod[j]));
		}
		mgr.close();
	}
	public void search(){
		//Buoi3 searcher = new Buoi3();
		//searcher.search(S, 10000);
		MyTabuSearch tabu = new MyTabuSearch(S, 10000,20);
		tabu.search(10000, 20, 100);
	}
	public void printResult(){
		for(int j = 0; j < P; j++){
			System.out.print("HK "+ j + ": ");
			for(int i = 0; i < N; i++)
				if(X[i].getValue() == j){
					System.out.print(i+ ": ");
				}
			System.out.println("Number courses = " + numberCoursesPreiod[j].getValue()+ 
					", number credits = "+ numberCreditsPeriod[j].getValue());
		}
	}
	
}
