package bt;

//package planningoptimization115657k62.phamquangdung;

import java.util.ArrayList;
import java.util.Random;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

public class HillClimbingSearch {
	VarIntLS[] x;
	public HillClimbingSearch(){
		
	}
	class Move{
		int i;
		int v;
		public Move(int i,  int v){
			this.i = i; this.v = v;
		}
	}
	private void exploreNeighborhood(IConstraint c, ArrayList<Move> cand){
		// khao sat lang gieng 
		int minDelta = Integer.MAX_VALUE;
		cand.clear();
		for(int i = 0; i < x.length; i++){
			for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) 
					if(x[i].getValue() != v){
				int delta = c.getAssignDelta(x[i], v);// tra ve su thay doi violation khi
													//	x[i] duoc gan gia tri v
				if(delta <= 0){
					if(delta < minDelta){
						cand.clear();
						cand.add(new Move(i,v));
						minDelta = delta;
					}else if(delta == minDelta){
						cand.add(new Move(i,v));
					}
				}
			}
		}		
	}
	public void search(IConstraint c, int maxIter){
		this.x = c.getVariables();
		int it = 0;
		ArrayList<Move> cand = new ArrayList<Move>();
		Random R = new Random();
		while(it < maxIter && c.violations() > 0){
			exploreNeighborhood(c, cand);
			if(cand.size() == 0){
				System.out.println("Reach local optimum"); break;
			}
			
			int idx = R.nextInt(cand.size());// diversify the search
			Move m = cand.get(idx);
			
			x[m.i].setValuePropagate(m.v);// local move (buoc di chuyen cuc bo)
			it++;
			System.out.println("Step " + it + ", violations = " + c.violations());
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
