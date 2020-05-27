package CBLS;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class HillClimbingSearchConstraintObjectiveFunction {
	class Move{
		int i;
		int v;
		public Move(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}
	
	Random R = new Random();
	private IConstraint c;
	private VarIntLS[] X;
	private IFunction f;
	private IFunction F;
	
	public HillClimbingSearchConstraintObjectiveFunction(IConstraint c, IFunction f, VarIntLS[] X) {//, IFunction F) {
		this.c = c; this.f = f; this.X = X;
		//this.F = F;
	}
	
	public void printSolution() {
		
	}
	
	public void exploreNeighborHood(ArrayList<Move> cand) {
		int minDelta = Integer.MAX_VALUE;
		cand.clear();
		//Chon 1 bien gan gia tri moi
		for(int i = 0; i < X.length; i++) {
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++) {
				//cai tien
				if(v == X[i].getValue()) continue; // ignore current solution
				int delta = F.getAssignDelta(X[i], v);
				if(delta < 0) {
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
	}
	
	public void exploreNeighborhoodMaintainConstraintMinimizeFunction(ArrayList<Move> cand) {
		cand.clear();
		int minDeltaF = Integer.MAX_VALUE;
		for(int i = 0; i < X.length; i++) {
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++) {
				if(v == X[i].getValue()) continue; // ignore current solution
				int deltaC = c.getAssignDelta(X[i], v);
				int deltaF = f.getAssignDelta(X[i], v);
				if(deltaC <= 0 && deltaF < 0) {
					if(deltaF < minDeltaF) {
						cand.clear();
						cand.add(new Move(i, v));
						minDeltaF = deltaF;
					}
				}else if(deltaF == minDeltaF) {
					cand.add(new Move(i, v));
				}
			}
		}
	}
	
	public void exploreNeighborhoodConstraintFunction(ArrayList<Move> cand) {
		// consider (c,f) in a lexical order
		cand.clear();
		int minDeltaC = Integer.MAX_VALUE;
		int minDeltaF = Integer.MAX_VALUE;
		for(int i = 0; i < X.length; i++) {
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++) {
				//cai tien
				if(v == X[i].getValue()) continue; // ignore current solution
				int deltaC = c.getAssignDelta(X[i], v);
				int deltaF = f.getAssignDelta(X[i], v);
				if(deltaC < 0 || deltaC == 0 && deltaF < 0) { //accept only better neighbors
					if(deltaC < minDeltaC || deltaC == minDeltaC && deltaF < minDeltaF) {
						cand.clear();
						cand.add(new Move(i, v));
						minDeltaF = deltaF;
						minDeltaC = deltaC;
					}
				}else if(deltaC == minDeltaC && deltaF == minDeltaF) {
					cand.add(new Move(i, v));
				}
			}
		}
	}
	
	
	public void search(int maxIter) {
		int it = 0;
		ArrayList<Move> cand = new ArrayList<>();
		while(it < maxIter) {
			//exploreNeighborHood(cand);
			exploreNeighborhoodMaintainConstraintMinimizeFunction(cand);
			exploreNeighborhoodConstraintFunction(cand);
			if(cand.size() == 0){
				System.out.println("Reach Local optimum");
				break;
			}
			Move m = cand.get(R.nextInt(cand.size())); // select randomly a move in candidate
			X[m.i].setValuePropagate(m.v); //local move(assign value, update violations, data structures;
			it++;
			System.out.println("Step "+it+" violations= " + c.violations() + ", f= "+f.getValue()); //+ ", F= "+F.getValue());
		}
	}
	
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[6];
		for(int i = 0; i < 6; i++) {
				X[i] = new VarIntLS(mgr, 1, 6);
		}
		
		ConstraintSystem S = new ConstraintSystem(mgr);
		S.post(new AllDifferent(X));
		
		//IFunction cv = new ConstraintViolations(S);
		IFunction f = new FuncPlus(new FuncMult(X[0], 3), new FuncMult(X[5], 4));
		//IFunction F = new FuncPlus(new FuncMult(cv, 1000), new FuncMult(f, 1));
		mgr.close();
		
		//HillClimbingSearchConstraintObjectiveFunction App= new HillClimbingSearchConstraintObjectiveFunction(S, f, X, F);
		HillClimbingSearchConstraintObjectiveFunction App= new HillClimbingSearchConstraintObjectiveFunction(S, f, X);
		App.search(10);
	}
}
