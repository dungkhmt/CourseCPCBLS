package cbls115676khmt61.phamquangdung;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cbls115676khmt61.phamquangdung.HillClimbingConstraintThenFunctionNeighborhoodExplorer.Move;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class HillClimbingFunctionMaintainConstraintNeighborhoodExplorer
		implements NeighborhoodExplorer {
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
	private List<Move> cand;
	private Random R =new Random();
	
	public HillClimbingFunctionMaintainConstraintNeighborhoodExplorer(VarIntLS[] X, IConstraint c, IFunction f){
		this.X = X; this.c = c; this.f = f;
		cand = new ArrayList<Move>();
		R = new Random();
	}
	@Override
	public void exploreNeighborhood() {
		// TODO Auto-generated method stub
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

	@Override
	public boolean hasMove() {
		return cand.size() > 0;
	}

	@Override
	public void move() {
		Move m = cand.get(R.nextInt(cand.size()));
		X[m.i].setValuePropagate(m.v);
	}
	@Override
	public IConstraint getConstraint() {
		// TODO Auto-generated method stub
		return c;
	}
	@Override
	public IFunction getFunction() {
		// TODO Auto-generated method stub
		return f;
	}

}
