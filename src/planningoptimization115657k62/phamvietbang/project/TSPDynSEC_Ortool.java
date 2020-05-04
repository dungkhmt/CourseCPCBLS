package planningoptimization115657k62.phamvietbang.project;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;

public class TSPDynSEC_Ortool {
	static {
	    System.loadLibrary("jniortools");
	}
	
		int N=5;
		int [][]c= {
				{0,4,2,5,6}, 
				{2,0,5,2,7}, 
				{1,2,0,6,3}, 
				{7,5,8,0,3}, 
				{1,2,4,3,0}
		};
		double inf = java.lang.Double.POSITIVE_INFINITY;
		MPSolver solver;
		MPVariable[][] x;
		private int findNext(int s) {
			for(int i=0;i<N;i++)
				if(i!=s && x[s][i].solutionValue()>0)
					return i;
			return -1;
		}
		public ArrayList<Integer> extractCycle(int s){
			ArrayList<Integer> L = new ArrayList<Integer>();
			int k=s;
			while(true) {
				L.add(k);
				k=findNext(k);
				int rep=-1;
				for(int i=0;i<L.size();i++)
					if(L.get(i)==k)
						{rep=i;break;}
				if(rep!=-1) {
					ArrayList<Integer>rl=new ArrayList<Integer>();
					for(int i=rep;i<L.size();i++)rl.add(L.get(i));
					return rl;
				}						
			}
		}
		private void createSEC(HashSet<ArrayList<Integer>>S) {
			for(ArrayList<Integer>c:S) {
				MPConstraint sc = solver.makeConstraint(0,c.size()-1);
				for(int i:c) {
					for(int j:c)if(i!=j) {
						sc.setCoefficient(x[i][j], 1);
					}
				}
			}
		}
		public void solve() {
			if(N>10) {
				System.out.println("value is too high");
				return;
			}
			solver = new MPSolver("TCP_optimal",
					MPSolver.OptimizationProblemType.valueOf("CBC_MIXED_INTEGER_PROGRAMMING"));
			x= new MPVariable[N][N];
			for(int i=0;i<N;i++) {
				for(int j=0;j<N;j++) {
					if(i!=j)x[i][j]=solver.makeIntVar(0, 1, "X[" + i + "," + j + "]");
				}
			}
			MPObjective obj = solver.objective();
			for(int i=0;i<N;i++) {
				for(int j=0;j<N;j++) {
					if(i!=j) {
					obj.setCoefficient(x[i][j], c[i][j]);
					}
				}
			}
			obj.setMinimization();
			for(int i=0;i<N;i++) {
				MPConstraint fc1 = solver.makeConstraint(1,1);
				
				for(int j=0;j<N;j++) {
					if(j!=i) {
						fc1.setCoefficient(x[i][j], 1);
						
					}					
				}
				MPConstraint fc2 = solver.makeConstraint(1,1);
				for(int j=0;j<N;j++) {
					if(j!=i)fc2.setCoefficient(x[j][i], 1);	
				}
			}
			HashSet<ArrayList<Integer>> S = new HashSet();
			boolean[] mark=new boolean[N];
			boolean found = false;
			while(!found) {
				
				createSEC(S);
				final MPSolver.ResultStatus resultStatus = solver.solve();
				if(resultStatus!=MPSolver.ResultStatus.OPTIMAL) {
					System.err.println("Can not find!");
					return;
				}
				System.out.print("obj="+solver.objective().value());
				for(int i=0;i<N;i++)mark[i]=false;
				for(int s=0;s<N;s++)if(!mark[s]) {
					ArrayList<Integer> C=extractCycle(s);
					if(0<C.size()&&C.size()<N) {
						System.out.print("Subtour deleted, C = ");
						for(int i:C)System.out.print(i+" ");System.out.println();
						S.add(C);
						for(int i:C)mark[i]=true;
					}else {
						System.out.println("Global tour detected, solution found!!");
						found=true;
						break;
					}
				}
			}
			ArrayList<Integer> tour = extractCycle(0);
			if (tour.size()>0) {
			    for (int i=0;i<tour.size();i++) System.out.print(tour.get(i)+"->");
			    System.out.println(tour.get(0));
			} else {
				for (int i=0;i<N;i++) 
					System.out.print(findNext(i)+"->");
				System.out.println(findNext(0));
			}		
}
		public static void main(String[] args) {
			TSPDynSEC_Ortool app = new TSPDynSEC_Ortool();
			app.solve();
		}		
}