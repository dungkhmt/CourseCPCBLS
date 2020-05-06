package planningoptimization115657k62.daohoainam;

import java.util.ArrayList;
import java.util.Random;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

public class HillClimbingSearch {

	class AssignMove {
		int i;
		int v;
		public AssignMove(int i, int v) {
		this.i = i; this.v = v;
		}
		
	}		
	
	private void exploreNeighborhood(IConstraint c,  VarIntLS[] y, ArrayList<AssignMove> cand){
		cand.clear(); // lam rong danh sach
		int minDelta = Integer.MAX_VALUE; // gan luong thay doi rat lon
		for(int i = 0; i < y.length; i++) {
			for(int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
				int d = c.getAssignDelta(y[i], v);
				if(d < minDelta) { // neu nho hon han violation thi xoa cac violation phia truoc di va add moi vao
					cand.clear();
					cand.add(new AssignMove(i, v));
					minDelta = d;
					
				}else if ( d == minDelta) { // neu violation khong doi ta van add them vao
					cand.add(new AssignMove(i, v));  
				}
			}
			
		}
	}
	
	
	
	public void search(IConstraint c, int maxIter){
		VarIntLS[] y = c.getVariables();
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
		Random R = new Random();
		int it = 0;
		while(it < maxIter && c.violations() > 0) {
			exploreNeighborhood(c, y, cand);
			int idx = R.nextInt(cand.size());
			AssignMove m = cand.get(idx);
			y[m.i].setValuePropagate(m.v);
			System.out.println("Step " + it + ", violations = " + c.violations());
			it++;
		}
	}
	
	
	public static void main(String[] args) {
	}
}
