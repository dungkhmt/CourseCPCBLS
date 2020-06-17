package localsearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class HillClimbingSearch {
	class AssignMove {
		int i;
		int v;
		public AssignMove(int i, int v) {
			this.i = i; this.v = v;
		}
	}
	public int N;
	public int t0;
	public int[] e;
	public int[] I;
	public int[] r;
	public int[][] d;
	public int[][] t;
	public int[] X;
	
	//loi giai moi cua thoa man rang buoc
	private void exploreNeighborhood(ArrayList<AssignMove> cand){
		cand.clear();
		int minDelta = Integer.MAX_VALUE;
		for(int i = 1; i < N; i++) {
			for(int v = i+1; v <= N; v++) {
				//System.out.println(i + " " + v);
				int d = violations(swap(X, i, v));
				if(d < minDelta) {
					cand.clear();
					cand.add(new AssignMove(i, v));
					minDelta = d;
				}else if(d == minDelta) {
					cand.add(new AssignMove(i, v));
				}
			}
		}
	}
	
	//loi giai moi cua ham muc tieu
	private void NextSolution(ArrayList<AssignMove> cand){
		cand.clear();
		int minDelta = function(X);
		for(int i = 1; i < N; i++) {
			for(int v = i+1; v <= N; v++) {
				//System.out.println(i + " " + v);
				int d = function(swap(X, i, v));
				if(d < minDelta) {
					cand.clear();
					cand.add(new AssignMove(i, v));
					minDelta = d;
				}else if(d == minDelta) {
					cand.add(new AssignMove(i, v));
				}
			}
		}
	}
	
	public void search(int maxIter) {
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
		Random R = new Random();
		int it = 0;
		
		//Tim loi gia thoa man rang buoc
		System.out.println("Step " + it + ", violations = " + violations(X));
		for(int i=0; i<= N; i++)
			System.out.print(X[i] + " -> ");
		System.out.println(0);
		while(it < maxIter && violations(X) > 0) {
			exploreNeighborhood(cand);
			int idx = R.nextInt(cand.size());
			AssignMove m = cand.get(idx);
			X = swap(X, m.i, m.v);
			System.out.println("Step " + it + ", violations = " + violations(X));
			for(int i=0; i<= N; i++)
				System.out.print(X[i] + " -> ");
			System.out.println(0);
			it++;
		}
		
		if(violations(X) != 0) {
			return;
		}
		
		//tim loi giai tot hon
		System.out.println("Step " + it + ", Obj = " + function(X));
		for(int i=0; i<= N; i++)
			System.out.print(X[i] + " -> ");
		System.out.println(0);
		it = 0;
		cand.clear();
		while(it < maxIter) {
			NextSolution(cand);
			if(cand.size() == 0)
				break;
			int idx = R.nextInt(cand.size());
			AssignMove m = cand.get(idx);
			X = swap(X, m.i, m.v);
			System.out.println("Step " + it + ", Obj = " + function(X));
			for(int i=0; i<= N; i++)
				System.out.print(X[i] + " -> ");
			System.out.println(0);
			it++;
		}
		
	}
	public int[] swap(int[] X, int i, int v) {
		int[] X2 = new int[X.length];
		for(int j=0; j< X.length; j++)
			X2[j] = X[j];
		
		int temp = X2[i];
		X2[i] = X2[v];
		X2[v] = temp;
		return X2;
	}
	
	//Ham thoa man rang buoc
	public int violations(int[] X) {
		int [] T = new int[N+2];
		T[0] = t0;
		for(int i=1; i<= N; i++) {
			T[i] = T[i-1] + t[X[i-1]][X[i]] + r[X[i-1]];
			if(T[i] < e[X[i]])
				T[i] = e[X[i]];
			if(T[i] > I[X[i]])
				return N+1-i;
		}
		T[N+1] = T[N] + t[X[N]][0] + r[X[N]];
		if(T[N+1] > I[0])
			return 1;
		return 0;
	}

	//Gia tri ham muc tieu
	public int function(int[] X) {
		int kq = 0;
		if(violations(X) != 0)
			return  Integer.MAX_VALUE;
		
		for(int i=1; i<= N; i++)
			kq = kq + d[X[i-1]][X[i]];
		
		kq = kq + d[X[N]][X[0]];
		return kq;
	}
	public void initData(String src){
		File f = new File(src);
		Scanner input = null;
		try{
			input = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		N = input.nextInt();
		X = new int[N+1];
		for(int i=0; i<= N; i++)
			X[i] = i;
		t0 = input.nextInt();
		e = new int[N+1];
		for(int i=1; i <= N; i++)
			e[i] = input.nextInt();
		
		e[0] = t0;
		I = new int[N+1];
		for(int i=1; i <= N; i++)
			I[i] = input.nextInt();
		
		r = new int[N+1];
		for(int i=1; i <= N; i++)
			r[i] = input.nextInt();
		I[0] = e[0] + 1440;
		
		d = new int[N+1][N+1];
		for(int i=0; i<= N; i++)
			for(int j=0; j<= N; j++)
				d[i][j] = input.nextInt();
		
		t = new int[N+1][N+1];
		for(int i=0; i<= N; i++)
			for(int j=0; j<= N; j++)
				t[i][j] = input.nextInt();
		//System.out.println(t[3][3]);
	}
	
	public static void main(String[] args) {
		HillClimbingSearch app = new HillClimbingSearch();
		long millis1 = System.currentTimeMillis();
		app.initData("./data/input15.txt");
		//System.out.println(app.violations(app.X));
		app.search(10000);
		long millis2 = System.currentTimeMillis();
		System.out.println("Violation: " + app.violations(app.X));
		System.out.println(millis2 - millis1);
	}
}
