package cbls115676khmt61.NguyenVanSon_20163560.Search;

import java.util.*;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import com.sun.org.apache.bcel.internal.generic.IfInstruction;
import com.sun.org.apache.xerces.internal.impl.dv.ValidatedInfo;

import Backtrack.HillClimbingSearch;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class LiquidContainers {
	int [] liquids  = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10,10} ;
	int [] ContainerCapacity  = { 60, 70, 80, 90, 100};
	int M = ContainerCapacity.length;
	int N = 20;
	int[][] Conflicts  = { { 0,1 }, { 7,8 }, {12, 17}, {8,9}, {1,2,9}, {0,9,12} };
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[] x;
	
	
	public void StateModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		x  = new VarIntLS[this.M];
		
		for (int i = 0 ; i< this.N; i++ ) {
			x[i] = new VarIntLS(mgr, 0, this.M-1);
		}
		
		for (int j  = 0; j < this.M; j++) {	
			S.post(new LessOrEqual( new ConditionalSum(x, liquids, j), ContainerCapacity[j]));
		}
		for (int i =0 ; i< Conflicts.length; i++) {
			int[] conflict  = Conflicts[i];
			VarIntLS[] Comp = new VarIntLS[conflict.length];
		for(int k =0; k < conflict.length; i++) {
				Comp[i] = x[conflict[i]];
				}
			for(int j =  0; j <M; j++)
				S.post(new NotEqual(new ConditionalSum(Comp, j), conflict.length));
			
		
		}
		mgr.close();
		
		
		
		
			
			
			
		}
	public void Search() {
		HillClimbingSearch model = new HillClimbingSearch();
		model.search(S, 1000);
	}
	public void PrintSolution() {
		
		for (int i = 0; i< N; i ++)
			System.out.print("X[" + i+ "]" + x[i].getValue());
		System.out.println();
	}

public static void main(String[] args) {
	LiquidContainers x  = new LiquidContainers();
	x.StateModel();
	x.Search();
	x.PrintSolution();
}
}
