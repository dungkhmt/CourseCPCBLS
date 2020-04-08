package cbls115676khmt61.phamquangdung;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;
import cbls115676khmt61.phamquangdung.HillClimbingConstraintNeighborhoodExplorer.Move;

public class HillClimbingFunctionNeighborhoodExplorer implements
		NeighborhoodExplorer {
	class Move{
		int i; int v;
		public Move(int i, int v){
			this.i = i; this.v = v;
		}
	}
	private VarIntLS[] X;
	private IFunction f;
	private List<Move> cand;
	private Random R;
	
	public HillClimbingFunctionNeighborhoodExplorer(VarIntLS[] X, IFunction f) {
		// TODO Auto-generated constructor stub
		this.X = X; this.f = f;
		cand = new ArrayList<Move>();
		R = new Random();
	}
	@Override
	public void exploreNeighborhood() {
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
		return null;
	}
	@Override
	public IFunction getFunction() {
		// TODO Auto-generated method stub
		return f;
	}

}
