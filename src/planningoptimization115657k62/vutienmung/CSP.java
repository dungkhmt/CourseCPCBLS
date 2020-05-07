package planningoptimization115657k62.vutienmung;

import localsearch.model.*;


public class CSP {
	LocalSearchManager  mgr = new LocalSearchManager();
//	VarInt
	IConstraint[] c = new IConstraint[6];
	c[0] = new NotEqual(new FuncPlus(X[2], 3), X[1]);
	c[1] = new L
}
