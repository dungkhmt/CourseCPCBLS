package planningoptimization115657k62.LeTrungHoangLong;

import java.util.ArrayList;
import java.util.Random;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class HimbClimbingSearch {
    public class Move {
    	int i;
    	int v;
    	public Move(int i,int v) {
    		this.i=i;
    		this.v=v;
    	}
    }
    public void exploreNeighborhood (IConstraint c, VarIntLS[] y,ArrayList<Move> cand) {
    	cand.clear();
    	int minDenlta = Integer.MAX_VALUE;
    	for (int i=0;i<y.length;i++) {
    		for (int v=y[i].getMinValue();v<=y[i].getMaxValue();v++) {
    			int d = c.getAssignDelta(y[i], v);
    			if (d<minDenlta) {
    				cand.clear();
    				cand.add(new Move(i,v));
    				minDenlta=d;
    			} else if (d==minDenlta){
    				cand.add(new Move(i,v));
    			}
    		}
    	}
    }
    public void search(ConstraintSystem c,int maxIter) {
		VarIntLS[] y = c.getVariables();
		ArrayList<Move> cand = new ArrayList<HimbClimbingSearch.Move>();
		Random r = new Random();
		int it=0;
		while(it<maxIter && c.violations()>0) {
			exploreNeighborhood(c, y, cand);
			int idx = r.nextInt(cand.size());
			Move m = cand.get(idx);
			y[m.i].setValuePropagate(m.v);
			System.out.println("Step : "+it+" violations: "+c.violations());
			it++;
		}
	}
    public static void main(String[] args) {
    	int N=1000;
		LocalSearchManager mng = new LocalSearchManager();
		VarIntLS[] x = new VarIntLS[N];
		for (int i=0;i<N;i++) {
			x[i] = new VarIntLS(mng, 0,N-1);
		}
		ConstraintSystem S = new ConstraintSystem(mng);
		
		S.post(new AllDifferent(x));
		
		IFunction[] f1 = new IFunction[N];	
		for(int i = 0; i < N; i++)			
		  f1[i] = new FuncPlus(x[i], i);		
		 S.post(new AllDifferent(f1));
		IFunction[] f2 = new IFunction[N];
		for(int i=0;i<N;i++)
			f2[i]= new FuncPlus(x[i], -i);
		S.post(new AllDifferent(f2));
		mng.close();
		HimbClimbingSearch search = new HimbClimbingSearch();
		search.search(S, 100000);
	}
}
