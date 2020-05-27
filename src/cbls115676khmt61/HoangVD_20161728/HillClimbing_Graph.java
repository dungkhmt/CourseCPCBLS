package cbls115676khmt61.HoangVD_20161728;

import java.util.ArrayList;
import java.util.Random;

import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class HillClimbing_Graph {
	IConstraint c;
	GraphPartitioning_CBLS g;
	IFunction f ; //optimiation objective
	IFunction F;
	public HillClimbing_Graph (GraphPartitioning_CBLS g) {
		this.c = g.S;
		this.g = g;
		this.f = g.f;
		IFunction cv = new ConstraintViolations(c);
		F = new FuncPlus(new FuncMult(cv,1000), new FuncPlus(f, 1)); // F = 1000 * c + f
	}
	private class AssignMove{
		int i, j; //index and value of move
		public AssignMove(int i, int j) {
			this.i = i;
			this.j = j;
		}
	}
	private Random R = new Random();
	
	public void exploreNeighborhood2(IConstraint c, VarIntLS[] x, ArrayList<AssignMove> candidate) {
		//cobination of constraint and function
		candidate.clear();
		int minDelta = Integer.MAX_VALUE;
		for (int i = 0; i < x.length; i++) {
			for (int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
				int d = F.getAssignDelta(x[i], v); //su thay doi muc do vi pham khi y[i] duoc gan gia tri v
				if (d < 0) {
					if (d < minDelta) {
						candidate.clear();
						candidate.add(new AssignMove(i, v));
						minDelta = d;
					}
					else if (d == minDelta){
						candidate.add(new AssignMove(i, v));
					}
				}
			}
		}
	}
	
	public void exploreNeighborhood1(IConstraint c, VarIntLS[] x, ArrayList<AssignMove> candidate) {
		//constraint first then value
		candidate.clear();
		int minDeltaC = Integer.MAX_VALUE;
		int minDeltaF = Integer.MAX_VALUE;
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x.length; j++) {
				int dc = c.getSwapDelta(x[i], x[j]); 
				int df = f.getSwapDelta(x[i], x[j]); 
				if (dc < 0 || dc == 0 && df < 0) {
					if (dc < minDeltaC || (dc == minDeltaC && df < minDeltaF)) {
						candidate.clear();
						candidate.add(new AssignMove(i, j));
						minDeltaC = dc;
						minDeltaF = df;
					}
					else if (dc == minDeltaC && df == minDeltaF){
						candidate.add(new AssignMove(i, j));
					}
				}
			}
		}
	}
	
	public void exploreNeighborhood0(IConstraint c, VarIntLS[] x, ArrayList<AssignMove> candidate) {
		//maintain constraint
		candidate.clear();
		int minDeltaF = Integer.MAX_VALUE;
		for (int i = 0; i < x.length; i++) {
			for (int j = 0; j < x.length; j++) {
				int dc = c.getSwapDelta(x[i], x[j]); 
				int df = f.getSwapDelta(x[i], x[j]); 
				if (dc <= 0 && df < 0) {
					if (df < minDeltaF) {
						candidate.clear();
						candidate.add(new AssignMove(i, j));
						minDeltaF = df;
					}
					else if (df == minDeltaF){
						candidate.add(new AssignMove(i, j));
					}
				}
			}
		}
	}
	 
	
	private void generateInitialSolution(VarIntLS[] x){
		for(int i = 0; i < x.length; i++){
			if (i < x.length / 2) {
				x[i].setValuePropagate(0);
			}
			else {
				x[i].setValuePropagate(1);
			}
		}
	}
	 
	public void printResult(VarIntLS x[]) {
		for (int i = 0; i < x.length; i++) {
			if (i < x.length - 1) 
				System.out.print("x[" + i + "] = " + x[i].getValue() + ", ");
			else 
				System.out.println("x[" + i + "] = " + x[i].getValue());
		}
	}
	
	public void search(int maxIter, int method){
		VarIntLS[] x = g.x;
		generateInitialSolution(x);
		int it = 0;// iteration
		ArrayList<AssignMove> candidate = new ArrayList<AssignMove>();
		while(it++ < maxIter && c.violations() > 0){
			if (method == 0) {
				exploreNeighborhood0(c, x, candidate);
			}
			else if (method == 1) {
				
				exploreNeighborhood1(c, x, candidate);
			}
			else if (method == 2) {
				exploreNeighborhood2(c, x, candidate);
			}
			if(candidate.size() == 0){
				System.out.println("Reach local optimum");
				printResult(x);
				break;
			}
			AssignMove m = candidate.get(R.nextInt(candidate.size()));// select randomly a move in candidate
			x[m.i].setValuePropagate(m.j);// local move (assign value, update violations, data structures
			it++;
			System.out.println("Step " + it + ", violations = " + c.violations() + ", function value = " + g.f.getValue());
		}
	}
}
