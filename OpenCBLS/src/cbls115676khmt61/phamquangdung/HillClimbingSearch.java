package cbls115676khmt61.phamquangdung;



import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

import java.util.ArrayList;
import java.util.Random;
public class HillClimbingSearch {
	class AssignMove{
		int i;
		int v;
		public AssignMove(int i, int v){
			this.i = i; this.v = v;
		}
	}
	Random R = new Random();
	
	private void explorNeighborhood(IConstraint c, VarIntLS[] x, ArrayList<AssignMove> candidate){
		int minDelta = Integer.MAX_VALUE;
		candidate.clear();
		for(int i = 0; i < x.length; i++){
			for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++){
				if(v == x[i].getValue()) continue;// ignore current solution
				int delta = c.getAssignDelta(x[i], v);
				if(delta < minDelta){
					candidate.clear();
					candidate.add(new AssignMove(i,v));
					minDelta = delta;
				}else if(delta == minDelta){
					candidate.add(new AssignMove(i,v));
				}
			}
		}
	}
	private void explorNeighborhoodMaintainConstraint(IConstraint c, IFunction f, VarIntLS[] x, ArrayList<AssignMove> candidate){
		int minDelta = Integer.MAX_VALUE;
		candidate.clear();
		for(int i = 0; i < x.length; i++){
			for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++){
				if(v == x[i].getValue()) continue;// ignore current solution
				int deltaC = c.getAssignDelta(x[i], v);
				if(deltaC >= 0) continue;
				int delta = f.getAssignDelta(x[i], v);
				if(delta < minDelta){
					candidate.clear();
					candidate.add(new AssignMove(i,v));
					minDelta = delta;
				}else if(delta == minDelta){
					candidate.add(new AssignMove(i,v));
				}
			}
		}
	}
	private void explorNeighborhoodConstraintFunction(IConstraint c, IFunction f, VarIntLS[] x, ArrayList<AssignMove> candidate){
		int minDeltaC = Integer.MAX_VALUE;
		int minDeltaF = Integer.MAX_VALUE;
		candidate.clear();
		for(int i = 0; i < x.length; i++){
			for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++){
				if(v == x[i].getValue()) continue;// ignore current solution
				int deltaC = c.getAssignDelta(x[i], v);
				int deltaF = f.getAssignDelta(x[i], v);			
				//System.out.println("delta(" + i + "," + v + ") = (" + deltaC + "," + deltaF + ")");
				if(deltaC < 0 || deltaC == 0 && deltaF < 0){ 
				if(deltaC < minDeltaC || (deltaC == minDeltaC && deltaF < minDeltaF)){
					candidate.clear();
					candidate.add(new AssignMove(i,v));
					minDeltaC = deltaC;
					minDeltaF = deltaF;
					//System.out.println("update minDelta(" + i + "," + v + ") = (" + minDeltaC + "," + minDeltaF + ")");
				}else if(deltaC == minDeltaC && deltaF == minDeltaF){
					candidate.add(new AssignMove(i,v));
					//System.out.println("add more update minDelta(" + i + "," + v + ") = (" + minDeltaC + "," + minDeltaF + ")");
				}
					break;
				}
			}
		}
	}
	private void generateInitialSolution(VarIntLS[] x){
		for(int i = 0; i < x.length; i++){
			int v = R.nextInt(x[i].getMaxValue() - x[i].getMinValue() + 1) + x[i].getMinValue();
			x[i].setValuePropagate(v);
		}
	}
	public void search(IConstraint c, int maxIter){
		VarIntLS[] x = c.getVariables();
		
		generateInitialSolution(x);
		
		int it = 0;// iteration
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		while(it < maxIter && c.violations() > 0){
			explorNeighborhood(c, x, candidate);
			if(candidate.size() == 0){
				System.out.println("search -> Reach local optimum");
				break;
			}
			AssignMove m = candidate.get(R.nextInt(candidate.size()));// select randomly a move in candidate
			x[m.i].setValuePropagate(m.v);// local move (assign value, update violations, data structures
			it++;
			System.out.println("Step " + it + ", violations = " + c.violations());
		}
	}

	public void searchConstraintFunction(IConstraint c, IFunction f, int maxIter){
		VarIntLS[] x = c.getVariables();
		
		//generateInitialSolution(x);
		
		int it = 0;// iteration
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		System.out.println("init, violations = " + c.violations() + ", f = " + f.getValue());
		while(it < maxIter){
			explorNeighborhoodConstraintFunction(c, f, x, candidate);
			if(candidate.size() == 0){
				System.out.println("searchConstraintFunction -> Reach local optimum");
				break;
			}
			AssignMove m = candidate.get(R.nextInt(candidate.size()));// select randomly a move in candidate
			x[m.i].setValuePropagate(m.v);// local move (assign value, update violations, data structures
			it++;
			System.out.println("Step " + it + ", violations = " + c.violations() + ", f = " + f.getValue());
		}
	}
	public void searchMaintainConstraintMinimize(IConstraint c, IFunction f, int maxIter){
		VarIntLS[] x = c.getVariables();
		
		//generateInitialSolution(x);
		
		int it = 0;// iteration
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		while(it < maxIter){
			explorNeighborhoodMaintainConstraint(c, f, x, candidate);
			if(candidate.size() == 0){
				System.out.println("searchMaintainConstraintMinimize -> Reach local optimum");
				break;
			}
			AssignMove m = candidate.get(R.nextInt(candidate.size()));// select randomly a move in candidate
			x[m.i].setValuePropagate(m.v);// local move (assign value, update violations, data structures
			it++;
			System.out.println("Step " + it + ", violations = " + c.violations() + ", f = " + f.getValue());
		}
	}

}
