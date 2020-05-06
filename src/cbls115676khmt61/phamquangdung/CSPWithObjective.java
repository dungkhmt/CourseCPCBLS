package cbls115676khmt61.phamquangdung;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.ortools.constraintsolver.ConstraintSolverParametersOrBuilder;

import cbls115676khmt61.phamquangdung.HillClimbingConstraintThenFunctionNeighborhoodExplorer.Move;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.ConstraintViolations;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CSPWithObjective {
	class Move{
		int i; int v;
		public Move(int i, int v){
			this.i = i; this.v = v;
		}
	}
	private LocalSearchManager mgr;
	private VarIntLS[] X;
	private IConstraint S;
	private IFunction obj;
	private IFunction F;
	private List<Move> cand = new ArrayList<Move>();
	private Random R = new Random();
	
	public void exploreNeighborhoodConstraint(VarIntLS[] X, IConstraint c, List<Move> cand) {
		int minDelta = Integer.MAX_VALUE;
		cand.clear();
		for(int i = 0; i < X.length; i++){
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++){
				int delta = c.getAssignDelta(X[i], v);
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
	
	public void exploreNeighborhoodFunctionMaintainConstraint(VarIntLS[] X, 
			IConstraint c,	IFunction f, List<Move> cand) {
		cand.clear();
		int minDeltaF = Integer.MAX_VALUE;
		for(int i = 0; i < X.length; i++){
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++){
				int deltaC = c.getAssignDelta(X[i], v);
				int deltaF = f.getAssignDelta(X[i], v);
				if(deltaC > 0) continue;// ignore worse constraint violations
				if(deltaF < 0){// consider only better neighbors in hill climbing
					if(deltaF < minDeltaF){
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
	public void exploreNeighborhoodFunction(VarIntLS[] X, IFunction f, List<Move> cand) {
		// TODO Auto-generated method stub
		int minDelta = Integer.MAX_VALUE;
		cand.clear();
		for(int i = 0; i < X.length; i++){
			for(int v = X[i].getMinValue(); v <= X[i].getMaxValue(); v++){
				int delta = f.getAssignDelta(X[i], v);
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

	public void exploreNeighborhoodConstraintThenFunction(VarIntLS[] X, IConstraint c, IFunction f, List<Move> cand) {
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
	public void stateModel(){
		mgr = new LocalSearchManager();
		X = new VarIntLS[5];
		for(int i = 0; i < X.length; i++){
			X[i] = new VarIntLS(mgr,1,5);
		}
		ArrayList<IConstraint> list = new ArrayList<IConstraint>();
		list.add(new AllDifferent(X));
		// create and add other constraints here
		
		IConstraint[] arr = new IConstraint[list.size()];
		for(int i = 0;i < list.size(); i++)
			arr[i]= list.get(i);
		
		
		S = new AND(arr);// we can add a constructor of AND with ArrayList parameters (list)
		
		obj = new FuncPlus(new FuncMult(X[0], 3), new FuncMult(X[4], 5));
		
		F = new FuncPlus(new FuncMult(new ConstraintViolations(S), 1000), 
				new FuncMult(obj, 1));
		mgr.close();
	}
	public void move(){
		Move m = cand.get(R.nextInt(cand.size()));
		X[m.i].setValuePropagate(m.v);
	}
	public void search1(){
		int it = 0;
		while(it < 10000 & S.violations() > 0){
			exploreNeighborhoodConstraint(X, S, cand);
			if(cand.size() == 0){
				System.out.println("local optimum"); break;
			}
			move();
			System.out.println("Step " + it + ": violations = " + S.violations() + 
					", obj = " + obj.getValue());
			it++;
		}
		System.out.println("Phase 2");
		it = 0;
		while(it < 100){
			exploreNeighborhoodFunctionMaintainConstraint(X, S, obj, cand);
			if(cand.size() == 0){
				System.out.println("local optimum");
				break;
			}
			move();
			System.out.println("Step " + it + ": violations = " + S.violations() +
					", obj = " + obj.getValue());
			it++;
		}		
	}
	public void search2(){
		int it = 0;
		while(it < 100){
			exploreNeighborhoodConstraintThenFunction(X, S, obj, cand);
			if(cand.size() ==0){
				System.out.println("local optimum");
				break;
			}
			move();
			System.out.println("Step " + it + ": violations = " + S.violations() +
					", obj = " + obj.getValue());
			it++;
		}	
	}
	public void search3(){
		int it = 0;
		while(it < 100){
			exploreNeighborhoodFunction(X, F, cand);
			if(cand.size() ==0){
				System.out.println("local optimum");
				break;
			}
			move();
			System.out.println("Step " + it + ": violations = " + S.violations() +
					", obj = " + obj.getValue());
			it++;
		}	
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		CSPWithObjective app = new CSPWithObjective();
		app.stateModel();
		//app.search1();
		//app.search2();
		app.search3();
		
		
		if(true) return;
		
		LocalSearchManager mgr = new LocalSearchManager();
		VarIntLS[] X = new VarIntLS[5];
		for(int i = 0; i < X.length; i++){
			X[i] = new VarIntLS(mgr,1,5);
		}
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
		
		HillClimbingConstraintNeighborhoodExplorer ne1 = 
				new HillClimbingConstraintNeighborhoodExplorer(X, S);
		HillClimbingConstraintThenFunctionNeighborhoodExplorer ne2 = 
				new HillClimbingConstraintThenFunctionNeighborhoodExplorer(X, S, f);
		HillClimbingFunctionNeighborhoodExplorer ne3 = 
				new HillClimbingFunctionNeighborhoodExplorer(X, F);
		HillClimbingFunctionMaintainConstraintNeighborhoodExplorer ne4 = 
				new HillClimbingFunctionMaintainConstraintNeighborhoodExplorer(X, S, f);
		
		System.out.println("Init : violations = " + S.violations() + ", f = " + f.getValue());
		
		int it = 0;
		/*
		it = 0;
		while(it < 100){
			ne3.exploreNeighborhood();
			if(ne3.hasMove())
				ne3.move();
			else{
				System.out.println("local optimum");
				break;
			}
			System.out.println("Step " + it + ": F = " + F.getValue() + ", violations = " + S.violations() + ", f = " + f.getValue());
			it++;
		}
		if(true) return;
		*/
		
		it = 0;
		while(it < 100){
			ne2.exploreNeighborhood();
			if(ne2.hasMove())
				ne2.move();
			else{
				System.out.println("local optimum");
				break;
			}
			System.out.println("Step " + it + ": violations = " + S.violations() + ", f = " + f.getValue());
			it++;
		}
		
		
		
		it = 0;
		while(it < 10000 & S.violations() > 0){
			ne1.exploreNeighborhood();
			if(ne1.hasMove())
				ne1.move();
			else{
				System.out.println("local optimum");
				break;
			}
			System.out.println("Step " + it + ": violations = " + S.violations() + ", f = " + f.getValue());
			it++;
		}
		System.out.println("Phase 2");
		it = 0;
		while(it < 100){
			ne1.exploreNeighborhood();
			if(ne4.hasMove())
				ne4.move();
			else{
				System.out.println("local optimum");
				break;
			}
			System.out.println("Step " + it + ": violations = " + S.violations() + ", f = " + f.getValue());
			it++;
		}
		
	}

}
