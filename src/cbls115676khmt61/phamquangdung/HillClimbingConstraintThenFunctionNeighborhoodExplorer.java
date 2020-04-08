package cbls115676khmt61.phamquangdung;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cbls115676khmt61.phamquangdung.HillClimbingSearchConstraintObjectiveFunction.Move;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class HillClimbingConstraintThenFunctionNeighborhoodExplorer implements NeighborhoodExplorer {
	class Move{
		int i; int v;
		public Move(int i, int v){
			this.i = i; this.v = v;
		}
	}
	private VarIntLS[] X;
	private IConstraint c;
	private IFunction f;
	private List<Move> cand;
	private Random R;
	public HillClimbingConstraintThenFunctionNeighborhoodExplorer(VarIntLS[] X, IConstraint c, IFunction f){
		this.X = X; this.c = c; this.f = f;
		cand = new ArrayList<Move>();
		R = new Random();
	}
	@Override
	public void exploreNeighborhood() {
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
