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

public class MyTabuSearch {
	class Move{
		int i;
		int v;
		public Move(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	int tabu[][];
	int tbl;
	VarIntLS[] x;
	int N; // number of variables
	int D; //max domain
	int bestViolations;
	IConstraint c;
	int nic = 0;
	Random R = new Random();
	public MyTabuSearch(IConstraint c) {
		
		this.c = c;
		x = c.getVariables();
		N = x.length;
		D = 0;
		for(int i=0; i<x.length; i++) {
			D = D < x[i].getMaxValue() ? x[i].getMaxValue() : D;
			
		}
		tabu = new int[N][D+1];
		for(int i=0; i<N; i++) {
			for(int v=0; v<=D; v++) {
				tabu[i][v] = -1;
			}
		}
	}
	
	private void restart() {
		for(int i = 0; i < N; i++) {
			int v = R.nextInt(x[i].getMaxValue() - x[i].getMinValue()+1) + x[i].getMinValue();
			x[i].setValuePropagate(v);
		}
		if(c.violations() < bestViolations) {
			bestViolations = c.violations();
		}
	}
	
	public void search(int maxIter, int tblen, int maxStable) {
		this.tbl = tblen;
		bestViolations = c.violations();
		nic = 0;
		ArrayList<Move> cand = new ArrayList<>(); 
		for(int it = 0; it <= maxIter; it++) {
			int minDelta = Integer.MAX_VALUE;
			cand.clear();
			for(int i = 0; i < N; i++) {
				for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
					int delta = c.getAssignDelta(x[i], v);
					if(tabu[i][v] <= it || delta + c.violations() < bestViolations) {
						if(delta < minDelta) {
							cand.clear();
							cand.add(new Move(i, v));
							minDelta = delta;
						}else if(delta == minDelta) {
							cand.add(new Move(i, v));
						}
					}
				}
			}
			
			Move m = cand.get(R.nextInt(cand.size()));
			x[m.i].setValuePropagate(m.v);
			tabu[m.i][m.v] = it + tbl;// dua move(i,v) vao DS tabu
			
			if(c.violations() < bestViolations){
				bestViolations = c.violations();
				nic = 0;
			}else{
				nic++;
				if(nic >= maxStable){
					restart();
				}
			}
			System.out.println("Step " + it + " violations = " + c.violations() + ", bestViolations = " + bestViolations);
			it++;
			
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
