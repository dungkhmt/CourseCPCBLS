package planningoptimization115657k62.LeTrungHoangLong;


import localsearch.model.*;

import java.util.ArrayList;
import java.util.Random;

import org.omg.CORBA.PUBLIC_MEMBER;

import localsearch.constraints.alldifferent.*;
import localsearch.functions.basic.*;
import localsearch.selectors.*;
import planningoptimization115657k62.phammanhtuan.Sudoku;
import planningoptimization115657k62.phammanhtuan.Sudoku.Move;



public class Sudoku2 {
	
	class Move {
		int i;
		int j1;
		int j2;
		public Move (int i,int j1,int j2) {
			this.i=i;
			this.j1=j1;
			this.j2=j2;
		}	
	}
	

	
	private void exploreNeighborhood (IConstraint S,VarIntLS[][] X,ArrayList<Move> cand) {
		cand.clear();
		int minDelta= Integer.MAX_VALUE;
		for(int i=0;i<9;i++)
			for(int j=0;j<9;j++)
				for(int j1=0;j1<8;j1++)
					for(int j2=j1+1;j2<9;j2++) {
						int delta= S.getSwapDelta(X[i][j1], X[i][j2]);
						if(delta<=0) {
							if(delta<minDelta) {
								cand.clear();
								cand.add(new Move(i, j1, j2));
								minDelta=delta;
							}else if(delta==minDelta) {
								cand.add(new Move(i, j1, j2));
							}
						}
					}
	}
	private void search (ConstraintSystem S,int maxIter,VarIntLS[][] X) {
		ArrayList<Move> cand = new ArrayList<Sudoku.Move>();
		Random r = new Random();
		int it=0;
		while(it<maxIter && S.violations()>0) {
			exploreNeighborhood(S, X, cand);
			int idx = r.nextInt(cand.size());
			Move m = cand.get(idx);
			int i=m.i;int j1=m.j1;int j2=m.j2;
			X[i][j1].swapValuePropagate(X[i][j2]);
			System.out.println("Step : "+it+" violations: "+S.violations());
			it++;
		}
	
		/*
		 * Tìm kiếm tham lam 2 bước
		 * System.out.println("init S = " + cs.violations());
		MinMaxSelector mms = new MinMaxSelector(cs);
		int it = 0;	
		while(it < 100000 && cs.violations() > 0){
		VarIntLS sel_x = mms.selectMostViolatingVariable();	
	    int sel_v = mms.selectMostPromissingValue(sel_x);
		  
		sel_x.setValuePropagate(sel_v);// local move		
		  
		System.out.println("Step " + it + ", violations = " + cs.violations());
		it++;		
		 }*/
		
		
	
	
	
}

public static void main(String[] args) {
	LocalSearchManager ls= new LocalSearchManager();
	VarIntLS[][] X= new VarIntLS[9][9];
	
	for(int i=0;i<9;i++)
		for(int j=0;j<9;j++) {
			X[i][j]= new VarIntLS(ls, 1,9);
			X[i][j].setValue(j+1);}
	ConstraintSystem cs = new ConstraintSystem(ls);
	for(int i = 0; i < 9; i++){
		VarIntLS[] y = new VarIntLS[9];
		for(int j = 0; j < 9; j++)
			y[j] = X[i][j];
		cs.post(new AllDifferent(y));
		}
	// define rang buoc AllDifferent theo cot
	for (int i = 0; i < 9; i++) {
		VarIntLS[] y = new VarIntLS[9];
		for (int j = 0; j < 9; j++) {
			y[j] = X[j][i];
		}
		cs.post(new AllDifferent(y));
	}
	for(int i = 0; i <= 2; i++){
		for(int j = 0; j <= 2; j++){
			VarIntLS[] y = new VarIntLS[9];
			int idx= -1;
			for(int im = 0; im <= 2; im++)
				for(int jm = 0; jm <= 2; jm++){
					idx++;
					y[idx] = X[3*i+im][3*j+jm];
				}
			cs.post(new AllDifferent(y));
		}
	}
	ls.close();
