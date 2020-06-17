package planningoptimization115657k62.levanlinh.Exercise;

import java.util.HashSet;

import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.linearsolver.MPSolver.ResultStatus;

public class BroadCast {
	static {
		System.loadLibrary("jniortools");
	}
	
	int N = 6;// nodes a
	int[][] E = {
			{0,2,3,3},
			{0,3,2,6},
			{1,3,9,5},
			{1,4,7,3},
			{1,5,5,8},
			{2,3,7,5},
			{2,4,6,2},
			{2,5,1,4},
			{3,4,8,4},
			{4,5,2,6}
	};
	int D = 13;
	
	class Edge{
		int node;
		int d;// transmission time
		int c;// cost
		public Edge(int node, int d, int c){
			this.node = node;
			this.c = c; this.d = d;
		}
	}
	
	HashSet<Edge>[] A;
	int s = 0;// source node
	
	// modelling
	MPSolver solver;
	MPVariable[][] X;// X[u][v] = 1, if a flow transmitted from u to v
	MPVariable[] t;// t[v] is the transmission time of from the source node to v
	public void mapping(){
		A = new HashSet[N];
		for(int u = 0; u < N; u++){
			A[u] = new HashSet<Edge>();
		}
		for(int k = 0; k < E.length; k++){
			int u = E[k][0];
			int v = E[k][1];
			int c = E[k][2];
			int d = E[k][3];
			//System.out.println("u = " + u + ", v = " + v);
			A[u].add(new Edge(v,d,c));
			A[v].add(new Edge(u,d,c));
		}
	}
	
	public void buildModel(){
		int totalD = 0;
		for(int k = 0; k < E.length; k++)
			totalD += E[k][3];
		
		solver = new MPSolver("Broad Casting",
				MPSolver.OptimizationProblemType.CBC_MIXED_INTEGER_PROGRAMMING);
		X = new MPVariable[N][N];
		t = new MPVariable[N];
		for(int k = 0; k < E.length; k++){
			int u = E[k][0];
			int v = E[k][1];
			X[u][v] = solver.makeIntVar(0, 1, "X[" + u + "," + v + "]");
		}
		for(int v = 0; v < N; v++)
			t[v] = solver.makeIntVar(0, totalD, "t[" + v + "]");
		
		
		
		
		MPObjective obj = solver.objective();
		for (int k = 0; k < E.length; k++){
			int u = E[k][0];
			int v = E[k][1];
			
			obj.setCoefficient(X[u][v], 1);
			obj.setCoefficient(X[v][u], 1);
		}
		
		
		ResultStatus rs = solver.solve();
		if (rs != ResultStatus.OPTIMAL) {
			System.out.println("cannot find optimal solution");
		} else {
			System.out.println("obj= " + obj.value());
			printSol();
		}
	}
	public void printSol(){
		for (int k = 0; k < E.length; k++){
			int u = E[k][0];
			int v = E[k][1];
			if(X[u][v].solutionValue() == 1) System.out.println("(" + u + "," + v + ")");
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BroadCast app = new BroadCast();
		app.mapping();
		app.buildModel();
	}

}
