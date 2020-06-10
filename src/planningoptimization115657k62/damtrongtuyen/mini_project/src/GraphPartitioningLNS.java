package planningoptimization115657k62.damtrongtuyen.mini_project.src;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import localsearch.model.VarIntLS;

public class GraphPartitioningLNS {
	int N;
	int[][] c;
	HashSet<Integer>[] A;
	Random R = new Random();
	HashSet<Integer> X = new HashSet<Integer>();
	HashSet<Integer> Y = new HashSet<Integer>();
	
	int[] x;// x[0..N-1] la phuong an hien tai: x[i] = 1 (i thuoc X), x[i] = 0 (i thuoc Y)
	int[] bx;// backup4
	int[] gx_best;// global best solution
	int[] Z;// Z[i] = 0 (i thuoc Y), 1(i thuoc X)
	int[] Z_best;// store best value of Z
	int f_best;// store quality of the best solution of LNS
	int gf_best;// global objective value
	public GraphPartitioningLNS(int N, int[][] c){
		this.N = N; this.c = c;		
		A = new HashSet[N];
		for(int i = 0; i < N; i++){
			A[i] = new HashSet<Integer>();
			for(int j = 0; j < N; j++) if(i != j && c[i][j] > 0)
				A[i].add(j);
		}
	}
	
	public ArrayList<Integer> select(int k){
		ArrayList<Integer> L = new ArrayList<Integer>();
		ArrayList<Integer> selected = new ArrayList<Integer>();
		for(int i = 0; i < N; i++) L.add(i);
		for(int i = 0; i < k; i++){
			int idx = R.nextInt(L.size());
			selected.add(L.get(idx));
			L.remove(idx);
		}
		return selected;
	}
	private void destroy(ArrayList<Integer> K){
		for(int v: K){
			X.remove(v); Y.remove(v);
		}
	}
	private void backup(){
		for(int i = 0; i < x.length; i++) bx[i] = x[i];
	}
	private void restore(){
		for(int i = 0; i < bx.length; i++) x[i] = bx[i];
	}
	private int computeCost(int[] x){
		int f = 0;
		for(int i = 0; i < N; i++){
			for(int j: A[i])if(x[i] != x[j]){
				f += c[i][j];
			}
		}
		return f/2;
	}
	private void solution(ArrayList<Integer> K){
		// bo gia tri moi Z: gia tri cua dinh K.get(i) = Z[i]
		backup();
		for(int i = 0; i < K.size(); i++){
			x[K.get(i)] = Z[i]; 
		}
		// query quality
		int s = 0;
		for(int i = 0; i < x.length; i++) s += x[i];
		if(s == N/2){
			int f = computeCost(x);
			if(f < f_best){
				f_best = f;
				for(int j = 0; j < Z.length; j++) Z_best[j] = Z[j];
			}
		}
		restore();
		
	}
	private void TRY(int i, ArrayList<Integer> K){
		//int node = K.get(i);
		for(int v = 0; v <= 1; v++){
			Z[i] = v;
			if(i == K.size()-1){
				solution(K);
			}else{
				TRY(i+1,K);
			}
		}
	}
	// solution la 2 tap X va Y
	public int lns(ArrayList<Integer> K){
		// K la ds cac dinh se duoc gan lai
		//destroy(K);// loai bo cac dinh trong K ra khoi solution
		
		f_best = 10000000;
		Z = new int[K.size()]; Z_best = new int[K.size()];
		TRY(0,K);
		return f_best;		
	}
	public void solveLNS(int k, VarIntLS[] z){
		x = new int[N];
		bx = new int[N];
		gx_best = new int[N];
		for(int i = 0; i < x.length; i++) x[i] = 0;
		for(int i = 0; i < x.length/2; i++) x[i] = 1;
		//for(int i = 0; i < x.length; i++) x[i] = z[i].getValue();
		gf_best = computeCost(x);
		System.out.println("init LNS, gf_best = " + gf_best);
		while(true){
			ArrayList<Integer> K = select(k);
			int eval = lns(K);
			System.out.println("selected K = ");
			for(int i: K) System.out.print(i + " "); 
			System.out.println(" eval = " + eval);
			if(eval < gf_best){
				gf_best = eval;
				for(int i  = 0; i < x.length; i++) gx_best[i] = x[i];
				System.out.println("best = " + gf_best);
			}else{
				System.out.println("LNS Reach local optimal"); break;
			}
		}
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
