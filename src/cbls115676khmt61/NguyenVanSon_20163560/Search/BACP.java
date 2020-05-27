package cbls115676khmt61.NguyenVanSon_20163560.Search;
import com.sun.org.apache.xpath.internal.operations.Plus;

import localsearch.*;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.LessThan;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.*;
public class BACP {
	int N = 12;
	int P = 4;
	int [] credits = {2,1,2,1,3,2,1,3,2,3,1,3};
	int [][] pre = {
			{1,0},
			{5,8},
			{4,5},
			{4,7},
			{3,10},
			{5,11}
	};
	int a = 3, b =3, c=7, d = 5;
	
	
	// model
	
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	IFunction[] numberCoursesPeriod;
	IFunction[] numberCreditPeriod;
	public void statemodel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N];
		for(int i = 0; i< N; i++)
			X[i] = new VarIntLS(mgr, 0, P-1);
		S = new ConstraintSystem(mgr);
		for(int i = 0 ; i< pre.length; i++) {
			IConstraint c = new LessThan(X[pre[i][0]], X[pre[i][1]]);
			S.post(c);
		}
		numberCoursesPeriod = new IFunction[P];
		numberCreditPeriod = new IFunction[P];
		for(int j = 0 ; j< P; j++) {
			numberCoursesPeriod[j] = new ConditionalSum(X, j);
			numberCreditPeriod[j] = new ConditionalSum(X, credits, j);
			
			
			S.post(new LessOrEqual(numberCoursesPeriod[j], b));
			S.post(new LessOrEqual(a,numberCoursesPeriod[j]));
			S.post(new LessOrEqual(numberCreditPeriod[j], d));
			S.post(new LessOrEqual(c, numberCreditPeriod[j]));
		}
		mgr.close();
		
		}
	
	public void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 1000);
		
	}
	public void printResult() {
		for(int j = 0 ; j< P; j++) {
			System.out.print("HK "+j+ ": ");
			for(int i = 0; i<N; i++) if(X[i].getValue() == j) {
				System.out.print(i + ",");
				
			}
			System.out.println("number course = " + numberCoursesPeriod[j].getValue() +
					", number credits = " + numberCreditPeriod[j].getValue());
		}
	}
	
	public static void main(String[] args) {
		BACP  app = new BACP();
		app.statemodel();
		app.search();
		app.printResult();
		
	}
	}

