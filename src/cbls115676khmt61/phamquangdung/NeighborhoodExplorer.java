package cbls115676khmt61.phamquangdung;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;

public interface NeighborhoodExplorer {
	public void exploreNeighborhood();
	public boolean hasMove();
	public void move();
	public IConstraint getConstraint();
	public IFunction getFunction();
}
