package BaiTap;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.AND;
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
		public Move(int i, int v){
			this.i = i; this.v  = v;
		}		
	}
	private IFunction f;
	private IConstraint c;
	private VarIntLS[] X;
	private IFunction F;
	private Random R =new Random();
	public HillClimbingSearchConstraintObjectiveFunction(IConstraint c, IFunction f, VarIntLS[] X, IFunction F){
		this.c = c; this.f = f; this.X = X;
		this.F = F;
	}
	public void exploreNeighborhoodMaintainContraintMinimizeFunction(ArrayList<Move> cand){
		cand.clear();
		int minDeltaF = Integer.MAX_VALUE;
		for(int i = 0; i < X.length; i++){
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++){
				int deltaC = c.getAssignDelta(X[i], v);
				int deltaF = f.getAssignDelta(X[i], v);
				if(deltaC <= 0 && deltaF < 0){
					if(deltaF < minDeltaF ){
						cand.clear(); 
						cand.add(new Move(i,v));
						minDeltaF = deltaF;
					}else if(deltaF == minDeltaF){
						cand.add(new Move(i,v));
					}
				}
			}
		}
	}
	public void exploreNeighborhoodConstraintFunction(ArrayList<Move> cand){
		// consider (c,f) in a lexical order
		cand.clear();
		int minDeltaC = Integer.MAX_VALUE;
		int minDeltaF = Integer.MAX_VALUE;
		for(int i = 0; i < X.length; i++){
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++){
				int deltaC = c.getAssignDelta(X[i], v);
				int deltaF = f.getAssignDelta(X[i], v);
				if(deltaC < 0 || deltaC == 0 && deltaF < 0){// accept only better neighbors
					if(deltaC < minDeltaC || deltaC == minDeltaC && deltaF < minDeltaF){
						cand.clear();
						cand.add(new Move(i,v));
						minDeltaC = deltaC; minDeltaF = deltaF;
					}else if(deltaC == minDeltaC && deltaF == minDeltaF){
						cand.add(new Move(i,v));
					}
				}
			}
		}
	}
	public void exploreNeighborhood(ArrayList<Move> cand){
		// TODO
		cand.clear();
		int minDelta = Integer.MAX_VALUE;
		for(int i = 0; i < X.length; i++){
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++){
				int delta = F.getAssignDelta(X[i], v);
				//System.out.println("delta(" + i + "," + v + ") = " + delta);
				if(delta < 0){
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
	public void printSol(){
		for(int i = 0; i < X.length; i++) System.out.println("X[" + i + "]  " + X[i].getValue());
	}
	
	public void search1(int maxIter){
		int it = 0;
		ArrayList<Move> cand = new ArrayList<>();
		printSol();
		System.out.println("Init F = " + F.getValue());
		while(it < maxIter){
			exploreNeighborhood(cand);
			
			if(cand.size() == 0){
				System.out.println("Reach local optimum"); break;
			}
			Move m = cand.get(R.nextInt(cand.size()));
			X[m.i].setValuePropagate(m.v);
			System.out.println("Step " + it + ", c = " + 
					c.violations() + ", f = " + f.getValue());
			
			it++;
		}
	}
	public void search2(int maxIter){
		int it = 0;
		ArrayList<Move> cand = new ArrayList<>();
		printSol();
		System.out.println("Init F = " + F.getValue());
		while(it < maxIter){
			exploreNeighborhoodConstraintFunction(cand);
			if(cand.size() == 0){
				System.out.println("Reach local optimum"); break;
			}
			Move m = cand.get(R.nextInt(cand.size()));
			X[m.i].setValuePropagate(m.v);
			System.out.println("Step " + it + ", c = " + 
					c.violations() + ", f = " + f.getValue());
			
			it++;
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[6];
		for(int i = 0; i <= 5; i++)
			X[i] = new VarIntLS(mgr,1,10);
		//ConstraintSystem S = new ConstraintSystem(mgr);
		
		ArrayList<IConstraint> list = new ArrayList<IConstraint>();
		list.add(new AllDifferent(X));
		// create and add other constraints here
		
		IConstraint[] arr = new IConstraint[list.size()];
		for(int i = 0;i < list.size(); i++)
			arr[i]= list.get(i);
		
		
		IConstraint S = new AND(arr);// we can add a constructor of AND with ArrayList parameters (list)
		
		IFunction f = new FuncPlus(new FuncMult(X[0], 3), new FuncMult(X[4], 5));
		
		//IFunction cv = new ConstraintViolations(S);
		//IFunction f1 = new FuncMult(cv, 1000);
		//IFunction f2 = new FuncMult(f, 1);
		//IFunction F = new FuncPlus(f1, f2);
		
		IFunction F = new FuncPlus(new FuncMult(new ConstraintViolations(S), 1000), new FuncMult(f, 1));
		
		mgr.close();
		System.out.println("S = " + S.violations() + ", f = " + f.getValue());
		//+ ", cv = " + 
		//cv.getValue() + ", F = " + F.getValue() + ", f1 = " + f1.getValue() + ", f2 = " + f2.getValue());
		
		HillClimbingSearchConstraintObjectiveFunction searcher = 
				new HillClimbingSearchConstraintObjectiveFunction(S,f,X,F);
		
		//searcher.search1(10);
		searcher.search2(10);
		
	}

}
