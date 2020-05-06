package planningoptimization115657k62.NguyenQuynhLoc;

import java.util.ArrayList;
import java.util.Random;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncPlus;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class QeenwithHCS {
	public static void main(String[] args) {
		int n=100;
		Random R= new Random();
		LocalSearchManager mgr= new LocalSearchManager();
		VarIntLS[] x= new VarIntLS[n];
		for(int i=0;i<n;i++) {
			x[i]= new VarIntLS(mgr, 0,n-1);
		}
		ConstraintSystem s= new ConstraintSystem(mgr);
		s.post(new AllDifferent(x));
		
		IFunction[] f1=new IFunction[n];
		IFunction[] f2=new IFunction[n];
		
		for(int i=0;i<n;i++) {
			f1[i]=new FuncPlus(x[i], i);
		}		
		for(int i=0;i<n;i++) {
			f2[i]=new FuncPlus(x[i],-i);
		}
		s.post(new AllDifferent(f1));
		s.post(new AllDifferent(f2));
		mgr.close();
		
		int k=0;
		ArrayList<Move> cand = new ArrayList<Move>();
		VarIntLS[] y=s.getVariables();
		System.out.println(s.violations());
		while(k<100000&&s.violations()>0) {
			cand.clear();
			int dmax= Integer.MAX_VALUE;
			for(int i=0;i<y.length;i++) {
				for(int v=y[i].getMinValue();v<=y[i].getMaxValue();v++) {
					int delta= s.getAssignDelta(y[i], v);
					if(delta<dmax) {
						cand.clear();
						cand.add(new Move(i, v));
						dmax=delta;
					}else if(delta==dmax) {
						cand.add(new Move(i, v));
					}
				}
			}
			Move m= cand.get(R.nextInt(cand.size()));
			y[m.i].setValuePropagate(m.v);
			System.out.println("step "+ k+","+ "violation = "+ s.violations());
			k++;
		}
	}
}

class Move{
	int i,v;
	public Move(int i,int v){
		this.i=i;
		this.v=v;
	}
}
