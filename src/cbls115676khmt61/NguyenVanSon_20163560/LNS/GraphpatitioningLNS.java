package cbls115676khmt61.NguyenVanSon_20163560.LNS;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;

public class GraphpatitioningLNS {
	int N;
	int[][] c;
	HashSet<Edge>[] A;
	
	// decision variables
	int[] Z;// Z[i] = 1, neu node i thuoc tap X, = 0 neu node i thuoc tap Y
	boolean[] instantiated;
	ArrayList<Integer> relaxVars = new ArrayList<Integer>();
	IGPConstraint[] constraints;
	
	int f_best;// global best objective value
	int fn;// best objective value of the neighborhood
	int[] Zn;// best solution of the neighborhood
	
	Random R = new Random();
	public void readData(String fn){
		try{
			Scanner in = new Scanner(new File(fn));
			N = in.nextInt();
			c = new int[N][N];
			for(int i = 0; i < N; i++)
				for(int j = 0; j < N; j++)
					c[i][j] = in.nextInt();
			
			A = new HashSet[N];
			for(int i = 0;i < N; i++){
				A[i] = new HashSet<Edge>();
				for(int j = 0; j < N; j++) if(c[i][j] > 0){
					A[i].add(new Edge(j,c[i][j]));
				}
			}
			in.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void generateInitialSolution(){
		ArrayList<Integer> L = new ArrayList<Integer>();
		for(int i = 0; i < N; i++) Z[i] = 0;
		for(int i = 0; i < N; i++) L.add(i);
		
		for(int i = 0; i < N/2; i++){
			int idx = R.nextInt(L.size());
			Z[L.get(idx)] = 1;
			L.remove(idx);
		}
		for(int i = 0; i < N; i++)  instantiated[i] = true;
	}
	public void select(ArrayList<Integer> relaxVars, int k){
		relaxVars.clear();
		while(relaxVars.size() < k){
			int idx = R.nextInt(N);
			if(!relaxVars.contains(idx)){
				relaxVars.add(idx);
				instantiated[idx] = false;
			}
		}
	}
	private int computeObj(){
		int f = 0;
		for(int i = 0; i < N; i++){
			for(Edge e: A[i]){
				int j = e.node;
				if(Z[i] != Z[j]) f += e.w;
			}
		}
		f = f/2;
		return f;
	}
	public void printSol(){
		System.out.print("X = ");
		for(int i = 0; i < N; i++) if(Z[i] == 1) System.out.print(i + " "); System.out.println();
		System.out.print("Y = ");
		for(int i = 0; i < N; i++) if(Z[i] == 0) System.out.print(i + " "); System.out.println();
		
	}
	private void solution(){
		int f = computeObj();
		//printSol();
		if(fn > f){
			fn = f;
			//System.out.println("Local update fn = " + fn);
			for(int i = 0; i < N; i++) Zn[i] = Z[i];
		}
	}
	private void propagate(){
		// do nothing
	}
	private boolean check(int v, int k){
		// should be improve
		return true;
	}
	private boolean finalCheck(){
		for(int i = 0; i < constraints.length; i++)
			if(!constraints[i].check()) return false;
		return true;
	}
	private void printPartialSolution(){
		for(int i = 0; i < N; i++)
			if(instantiated[i]) System.out.print("Z[" + i + "] = " + Z[i] + " ");
		System.out.println();
	}
	private void TRY(int k){// thu gia tri cho Z[relaxVars[k]] : {0,1}
		int var = relaxVars.get(k);
		for(int val = 0; val <= 1; val++){
			if(check(val, var)){
				Z[var] = val;
				//System.out.println("TRY(" + k + "), assign Z[" + var + "] = " + val + ": ");
				instantiated[var] = true;
				//printPartialSolution();
				propagate();
				if(k == relaxVars.size()-1){
					
					if(finalCheck()){
						//for(int i = 0; i < N; i++) System.out.print(Z[i] + " ");
						//System.out.println("final check pass");
						solution();
					}
				}else{
					TRY(k+1);
				}
				instantiated[var] = false;
			}
		}
	}
	public int solve(){
		// tim bo gia tri tot nhat cho k bien trong relaxVars
		fn = Integer.MAX_VALUE;
		TRY(0);
		return fn;
	}
	public void buildModel(){
		Z = new int[N];
		Zn = new int[N];
		instantiated = new boolean[N];
		constraints = new IGPConstraint[1];
		constraints[0] = new GPBalance(Z, instantiated);
		
	}
	public void lns(int maxIter, int maxTime, int k){
		generateInitialSolution();
		int it = 0;
		f_best = computeObj();// khoi tao ky luc
		
		while(it < maxIter){
			select(relaxVars,k);// destroy k variables
			//System.out.print("relaxVars = ");for(int i: relaxVars) System.out.print(i + " "); System.out.println();
			fn = solve();// use CP search to re-optimize solution
			if(fn < f_best){
				f_best = fn;
				System.out.println("Update best " + f_best);
			}
			for(int i = 0; i < N; i++) Z[i] = Zn[i];// prepare for next loop
			System.out.println("Step " + it + ": fn = " + fn + ", f_best = " + f_best);
			it++;
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphpatitioningLNS app = new GraphpatitioningLNS();
		app.readData("data/GraphPartitioning/gp-1000.txt");
		app.buildModel();
		app.lns(1000, 10000, 8);
	}
}
