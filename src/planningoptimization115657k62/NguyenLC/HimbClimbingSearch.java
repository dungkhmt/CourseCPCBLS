package planningoptimization115657k62.NguyenLC;

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
	VarIntLS[] x;
	public HimbClimbingSearch() {
		
	}

	public class Move{
		int i, v;// i index
		ArrayList<Move> cand= new ArrayList<Move>(); //candidate
		public Move(int i, int v) {
			this.i=i;this.v=v;
		}
	}
	
	private void exploreNeighborhood (IConstraint c, ArrayList<Move> cand) {
		int minDelta=Integer.MAX_VALUE;
		cand.clear();
		for(int i=0;i<x.length;i++) {
			for(int v=x[i].getMinValue();v<=x[i].getMaxValue();v++) {
//				exploreNeighborhood(c, cand);
//				if(cand.size()==0) {
				int delta=c.getAssignDelta(x[i],v);
				
				if(delta <=0) {
					if(delta<minDelta) {
						cand.clear();
						cand.add((new Move(i,v)));
						minDelta=delta;
					}
					else if(delta ==minDelta) {
						cand.add(new Move(i,v));
					}
				}
			}
		
	}
	}
	
	public void search(IConstraint c, int maxInter) {
		this.x=c.getVariables();
		int it=0;
		Random R=new Random();
		ArrayList<Move> cand= new ArrayList<Move>();
		while(it<maxInter && c.violations()>0) {
			exploreNeighborhood(c,cand);
			if(cand.size()==0) {
				System.out.println("reach local optimum");break;
			}
			
			int idx=R.nextInt(cand.size());
			Move m=cand.get(idx);
			x[m.i].setValuePropagate(m.v);
			it++;
			System.out.println("Step "+it+" violations = "+c.violations());
		}
	}

	public static void main(String[] avgs) {
		int n=1000;
		
		LocalSearchManager ls=new LocalSearchManager();
		ConstraintSystem S=new ConstraintSystem(ls);
		
		VarIntLS[] x = new VarIntLS[n];
		for (int i = 0; i < n; i++){
			x[i] = new VarIntLS(ls, 0, n - 1);
		}
		
		S.post(new AllDifferent(x));
		
		IFunction[] f1=new IFunction[n];
		for (int i = 0; i < n; i++) 
			f1[i] =  new FuncPlus(x[i], i);
		S.post(new AllDifferent(f1));
		
		IFunction[] f2 = new IFunction[n];
		for (int i = 0; i < n; i++) f2[i] = new FuncPlus(x[i], -i);
		S.post(new AllDifferent(f2));
		
		ls.close();
		
		HimbClimbingSearch searcher=new HimbClimbingSearch();
		searcher.search(S, 10000);
	}
