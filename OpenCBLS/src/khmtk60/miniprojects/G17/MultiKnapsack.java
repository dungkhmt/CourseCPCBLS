package khmtk60.miniprojects.G17;


import java.util.ArrayList;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class MultiKnapsack {
	LocalSearchManager mgr;
	ConstraintSystem S;
	VarIntLS[][] x;
	VarIntLS[][] y;
	VarIntLS[][] z;
	
	public int n;
	public int m;
	
	// i = 1, 2, .., N
	public MinMaxTypeMultiKnapsackInputItem[] items;
	
	// b = 1, 2, .., M
	public MinMaxTypeMultiKnapsackInputBin[] bins;
	
	public MultiKnapsack(int n, int m) {
		this.n = n;
		this.m = m;
	}
	
	public void stateModel() {
		this.mgr = new LocalSearchManager();
		this.x = new VarIntLS[this.n][this.m];
		for (int i = 0; i < this.n; i ++) {
			for (int j = 0; j < this.m; j ++) {
				this.x[i][j] = new VarIntLS(mgr, 0, 1);
			}
		}
		
		this.S = new ConstraintSystem(mgr);
		IConstraint temp;
		
		// Constraint 1 a
		for (int b = 0; b < this.m; b ++) {
			temp = new LessOrEqual((int) (this.bins[b].getCapacity() + .1), new Sum(x[b]));
		}
		
		this.mgr.close();
	}
	
	public void search() {
		
	}
	
	public void solve() {
		
	}
}
