package com.hung_vt;

import java.util.*;

import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import localsearch.search.HillClimbing;
import localsearch.search.TabuSearch;

public class LiquidContainers {
	int M = 5;
	int[] capacities = {60, 70, 90, 80, 100};
	
	int N = 20;
	int[] volumes = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10,
			20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[][] conflicts = {
			{0, 1},
			{7, 8},
			{12, 17},
			{8, 9},
			{1, 2, 9},
			{0, 9, 12}
	};
	
	LocalSearchManager mgr;
    VarIntLS[] X;
    ConstraintSystem S;
	
	public LiquidContainers() {
		
	}
	
	private void stateModel() {
		mgr = new LocalSearchManager();
        X = new VarIntLS[N];
        for (int i = 0; i < N; ++i)
            X[i] = new VarIntLS(mgr, 0, M - 1);
        S = new ConstraintSystem(mgr);
        
        for (int j=0; j<M; j++)
        	S.post(new LessOrEqual(new ConditionalSum(X, volumes, j), capacities[j]));
        
        for (int k=0; k<conflicts.length; k++) {
        	int[] conflict = conflicts[k];
        	VarIntLS[] components = new VarIntLS[conflict.length];
        	for (int i=0; i<conflict.length; i++)
        		components[i] = X[conflict[i]];
        	for (int j=0; j<M; j++)
        		S.post(new NotEqual(new ConditionalSum(components, j), conflict.length));
        }
        
        mgr.close();
	}
	
	private void search() {
		MyTabuSearch model = new MyTabuSearch(S);
		model.search(100000, 20, 100);
	}
	
	private void print() {
		System.out.println();
		System.out.println("Violations = " + S.violations());
		for (int j=0; j<M; j++) {
			System.out.print("Thung " + capacities[j] + " lit:");
			for (int i=0; i<N; i++)
				if (X[i].getValue() == j)
					System.out.print(" " + i);
			System.out.println();
		}
	}
	
	public void solve() {
		stateModel();
		search();
		print();
	}
	
	public static void main(String[] args) {
		LiquidContainers app = new LiquidContainers();
		app.solve();
	}
}
