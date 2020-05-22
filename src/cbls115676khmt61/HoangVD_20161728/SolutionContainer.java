package cbls115676khmt61.HoangVD_20161728;


import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class SolutionContainer {
	/*
	 * PROBLEM
	 * X[i]: liquid i container
	 * num_liquid: number of liquid types
	 * num_container: number of container
	 * each liquid has a volume
	 * each container has a volume
	 * constraint:
	 * 		X[i] != X[j] if (i, j) in ban_list
	 * 		X[i] != X[j] and X[i] != X[k] if (i, j, k) in ban_list
	 * 		sum(X[i] = j) * V_liquid(i) <= V_container(j) for j in range(0, num_container)
	 * */
	
	int num_liquid = 20;
	int num_container = 5;
	int[] V_liquid = {20, 15, 10, 20, 20, 25, 30, 
					  15, 10, 10, 20, 25, 20, 10,
					  30, 40, 25, 35, 10, 10};
	int[] V_container = {60, 70, 90, 80, 100};
	
	int[][] ban_list = {
			{0,1},
			{7,8},
			{12, 17},
			{8, 9},
			{1, 2, 9},
			{0, 9, 12}
	};
	
	LocalSearchManager mgr;
	VarIntLS[] X;
	ConstraintSystem S;
	IFunction[] V_liquid_in_container;	// volume of total liquid volumes in container
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		// init varaiable
		X = new VarIntLS[num_liquid];
		for(int i = 0; i < num_liquid; i++)
			X[i] = new VarIntLS(mgr, 0, num_container - 1);
		
		// add constraint
		S = new ConstraintSystem(mgr);
		IConstraint c;
		// ban constraint
		for(int i = 0; i < ban_list.length; i++) {
			if(ban_list[i].length == 2) {
				c = new NotEqual(X[ban_list[i][0]], X[ban_list[i][1]]);
				S.post(c);
			}
			if(ban_list[i].length == 3) {
				c = new AND(new NotEqual(X[ban_list[i][0]], X[ban_list[i][1]]), new NotEqual(X[ban_list[i][1]], X[ban_list[i][2]]));
				S.post(c);
			}
		}
		
		// container constraint
		V_liquid_in_container = new IFunction[num_container];
		for(int j = 0; j < num_container; j++) {
			V_liquid_in_container[j] = new ConditionalSum(X,  V_liquid, j);
			S.post(new LessOrEqual(V_liquid_in_container[j], V_container[j]));
		}
		
		mgr.close();
	}
	
	public void search() {
		HillClimbingSearch searcherClimbingSearch = new HillClimbingSearch();
		searcherClimbingSearch.search(S, 10000);
	}
	
	public void printSol() {
		for(int j = 0; j < num_container; j++) {
			System.out.println("Container: "+ j + ": ");
			for(int i = 0; i < num_liquid; i++) 
				if (X[i].getValue() == j) {
					System.out.print(i + ", ");
				}
			System.out.println("current_V = " + V_liquid_in_container[j].getValue());
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SolutionContainer appContainer = new SolutionContainer();
		appContainer.stateModel();
		appContainer.search();
		appContainer.printSol();
	}
}
