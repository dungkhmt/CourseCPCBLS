package planningoptimization115657k62.phamvietbang.project;

import java.util.*;

import org.chocosolver.solver.search.strategy.Search;

import localsearch.model.*;
import localsearch.constraints.*;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.*;
import localsearch.functions.basic.FuncPlus;
public class HillClimbingSearch {
	public class Move{
		int i,j;
		public Move(int i, int j) {
			this.i=i;
			this.j=j;
		}
	}
	public void findNeighborhood(IConstraint c, VarIntLS[] y, ArrayList<Move> cand) {
			cand.clear();
			int minDelta=Integer.MAX_VALUE;
			for(int i=0;i<y.length;i++) {
				for(int j=y[i].getMinValue();j<=y[i].getMaxValue();j++) {
					int d=c.getAssignDelta(y[i], j);
					if(d<minDelta) {
						cand.clear();
						cand.add(new Move(i,j));
						minDelta=d;
					}else if(d==minDelta) {
						cand.add(new Move(i,j));
					}
				}
			}
	}
	public void search(ConstraintSystem c, int maxIter) {
		VarIntLS[] y=c.getVariables();
		ArrayList<Move> cand=new ArrayList<HillClimbingSearch.Move>();
		Random r= new Random();
		int it=0;
		while(it<maxIter&&c.violations()>0) {
			findNeighborhood(c,y,cand);
			int index=r.nextInt(cand.size());
			Move m=cand.get(index);
			y[m.i].setValuePropagate(m.j);
			System.out.println("Step: "+it+" violation: "+c.violations());
			it++;
		}
	}
	public static void main(String[] args) {
		int N=1000;
		LocalSearchManager mgr=new LocalSearchManager();
		VarIntLS[] x=new VarIntLS[N];
		for(int i=0;i<N;i++) {
			x[i]=new VarIntLS(mgr,0,N-1);
		}
		ConstraintSystem S= new ConstraintSystem(mgr);
		S.post(new AllDifferent(x));
		IFunction[] function1=new IFunction[N];
		for(int i=0;i<N;i++)
			function1[i]=new FuncPlus(x[i],i);
		S.post(new AllDifferent(function1));
		IFunction[] function2=new IFunction[N];
		for(int i=0;i<N;i++)
			function2[i]=new FuncPlus(x[i],-i);
		S.post(new AllDifferent(function2));
		mgr.close();
		HillClimbingSearch search=new HillClimbingSearch();
		search.search(S, 100000);
	}
}
