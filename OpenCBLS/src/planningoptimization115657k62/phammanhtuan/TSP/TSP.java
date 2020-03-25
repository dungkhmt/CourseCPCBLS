package TSP;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;
import com.google.ortools.linearsolver.MPSolverResponseStatus;
import com.google.ortools.linearsolver.MPVariable;

public class TSP {
	static {
		System.loadLibrary("jniortools");
	}
	private int N = 5;
	private int c[][] = {
			{0,4,2,5,6},
			{2,0,5,2,7},
			{1,2,0,6,3},
			{7,5,8,0,3},
			{1,2,4,3,0},};
	final double INF = java.lang.Double.POSITIVE_INFINITY;
	MPSolver solver;
	MPVariable[][] X;
	public int findNext (int k,MPVariable[][] X) {
		for (int i = 0;i<N;i++) {
			if (i!=k && X[k][i].solutionValue()>0) return i;
		}
	    return -1;
	}
	public ArrayList<Integer> extractCycle(int k,MPVariable[][] X) {
		ArrayList<Integer> L = new ArrayList<Integer>();
		int x = k;
		while(true) {
			L.add(x);x=findNext(x,X);
			int rep = -1;
			for (int i = 0;i<L.size();i++) {
				if (L.get(i)==x) {
					rep = i;
					break;
				}
			}
			if (rep!=-1) {
				ArrayList<Integer> rL = new ArrayList<Integer>();
				for (int i=0;i<rep;i++) rL.add(L.get(i));
				return rL;
			}
		}
	}
	public void createSEC(HashSet<ArrayList<Integer>> S) {
		for (ArrayList<Integer> C: S) {
			MPConstraint c = solver.makeConstraint(0,C.size()-1);
			for (int i :C)
				for (int j : C) 
					if (i!=j) c.setCoefficient(X[i][j], 1);
		}
	}
	public void solve () {
		HashSet<ArrayList<Integer>> S = new HashSet<ArrayList<Integer>>();
		boolean[] mark = new boolean[N];
		boolean found = false;
		while (!found) {
			solver = new MPSolver("TSP",MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING) ;
			X = new MPVariable[N][N];
			for (int i=0;i<N;i++) 
				for (int j=0;j<N;j++) {
				    if (i!=j) X[i][j] = solver.makeIntVar(0, 1, "X["+i+"]["+j+"]");
				}
			MPObjective obj = solver.objective();
			for (int i=0;i<N;i++)
				for (int j=0;j<N;j++) {
					if (i!=j) obj.setCoefficient(X[i][j], c[i][j]);
				}
			obj.setMinimization();
			for (int i=0;i<N;i++) {
				MPConstraint c1 = solver.makeConstraint(1,1);
				for (int j=0;j<N;j++) {
					if (i!=j) c1.setCoefficient(X[i][j], 1);
				}
				MPConstraint c2 = solver.makeConstraint(1,1);
				for (int j=0;j<N;j++) {
					if (i!=j) c2.setCoefficient(X[j][i], 1);
				}
			}
			createSEC(S);
			final MPSolver.ResultStatus rs = solver.solve();
			if (rs != MPSolver.ResultStatus.OPTIMAL) {
				System.err.println("The problem does not have an optimal solution!");
		        return;
			}
			System.out.println("obj = " + solver.objective().value());
			for (int i = 0;i<N;i++) mark[i]=false;
			for (int s=0;s<N;s++) {
				if (!mark[s]) {
					ArrayList<Integer> C = extractCycle(s,X);
					if (0<C.size() && C.size()<N) {
						System.out.print("SubTour deteted, C = "); 
						for(int i: C) System.out.print(i + " "); System.out.println();
				        S.add(C);
				        for(int i: C) mark[i] = true;
					} else {
						System.out.println("Global tour detected, aolution found !!!");
						found = true;break;
					}
				}
			}
			ArrayList<Integer> tour = extractCycle(0,X);
			if (tour.size()>0) {
			    for (int i=0;i<tour.size();i++) System.out.print(tour.get(i)+"->");
			    System.out.println(tour.get(0));
			} else {
				for (int i=0;i<N;i++) 
					System.out.print(findNext(i, X)+"->");
				System.out.println(findNext(0, X));
			}			
		}
	}
	public static void main(String[] args) {
		TSP a = new TSP();
		a.solve();
	}
}
