package cbls115676khmt61.ngodoannghia_20162905;

import java.util.ArrayList;
import java.util.Random;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class MyTabuSearch {
	
	private class Move{	
		int i;
		int v;
		public Move(int i, int v){
			this.i = i;
			this.v = v;
		}
	}
	
	int tabu[][];
	int tbl;
	IConstraint c;
	VarIntLS[] x;
	int N;
	int D;
	int bestViolations;
	Random R = new Random();
	
	public MyTabuSearch(IConstraint c, int maxIter, int tblen){
		this.c = c;
		this.tbl = tblen;
		x = c.getVariables();
		N = x.length;
		D = 0;
		for(int i = 0; i < x.length; i++){
			D = D < x[i].getMaxValue() ? x[i].getMaxValue() : D;
		}
		tabu = new int[N][D+1];
		for(int i = 0; i < N; i++){
			for(int v = 0; v <= D; v++){
				tabu[i][v] = -1;
			}
		}
		
	}
	private void restart(){
		for(int i = 0; i < N; i++){
			int v = R.nextInt(x[i].getMaxValue() - x[i].getMinValue() + x[i].getMaxValue());
			x[i].setValuePropagate(v);
		}
		if(c.violations() < bestViolations){
			bestViolations = c.violations();
		}
	}
	
	public void search(IConstraint c, IFunction f, int tblen, int maxStable){
		
	}
	
	public void search(int maxIter, int tblen, int maxStable){
		this.tbl = tblen;
		bestViolations = c.violations();
		ArrayList<Move> cand = new ArrayList<>();
		int nic = 0;
		int it = 0;
		while(it <= maxIter && bestViolations > 0){
			int minDelta = Integer.MAX_VALUE;
			cand.clear();
			for(int i = 0; i < N; i++){
				for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++){
					if(x[i].getValue() != v){
						int delta = c.getAssignDelta(x[i], v);
						if(tabu[i][v] <= it || delta + c.violations() < bestViolations){
							if(delta < minDelta){
								cand.clear();
								cand.add(new Move(i, v));
								minDelta = delta;
							}
							else if(delta == minDelta){
								cand.add(new Move(i, v));
							}
						}
					}
				}
			}
			Move m = cand.get(R.nextInt(cand.size()));
			x[m.i].setValuePropagate(m.v);
			tabu[m.i][m.v] = it +tbl;
			
			if(c.violations() < bestViolations){
				bestViolations = c.violations();
				nic = 0;
			}
			else{
				nic++;
				if(nic >= maxStable){
					restart();
				}
			}
			System.out.println("Step "+ it + " violation = "+ c.violations() + " bestviolation = "+ bestViolations);
			it++;
		}
	}	

}
