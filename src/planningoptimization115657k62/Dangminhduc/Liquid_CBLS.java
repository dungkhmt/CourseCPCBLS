package planningoptimization115657k62.Dangminhduc;

import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;

public class Liquid_CBLS {
	int N = 20;
	int K = 5;
	int[] c = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int[] d = {60, 70, 90, 80, 100};
	
	LocalSearchManager mgr;
	ConstraintSystem C;
	
	public void createModel() {
		mgr = new LocalSearchManager();
		C = new ConstraintSystem(mgr);
	}
}
