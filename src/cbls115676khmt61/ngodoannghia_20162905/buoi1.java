package cbls115676khmt61.ngodoannghia_20162905;

import java.util.ArrayList;
import java.util.Random;


class Pair{
	
}

public class buoi1 {
	//Bai Example1: N-queue de quy quay lui
	
	int N;
	int[] x;
	int cost = 0;
	public boolean found;
	
	public void solution(){
		for(int i = 1; i <=  N; i++)
			System.out.print(x[i] + " ");
		found = true;
		System.out.print("\n");
	}
	
	public boolean check(int v, int k){
		for(int i = 1; i <= k -1; i++){
			if(x[i] == v) return false;
			if(x[i]+ i == v + k) return false;
			if(x[i] - i == v - k) return false;
		}
		return true;
	}
	
	public void TRY(int k){ // thu cac gia tri co the co
		for(int v = 1; v <= N; v++){
			if(check(v, k)){
				x[k] = v;
				if(k == N){
					solution();
				}
				else{
					TRY(k+1);
				}
			}
		}
	}
	
	public void slove(int N){
		this.N = N;
		x = new int[N + 1];
		found = false;
		TRY(1);
		if(found == false){
			System.out.print("Not found!");
		}
	}
	
	// cach 2; n-queue su dung phuowng phap cuc bo
	Random R = new Random();
	public void genarateInitSolution(){
		for(int  i = 1; i <= N; i++){
			x[i] = R.nextInt(N)+1;
		}
	}
	public int violation(){
		int v = 0;
		for(int i = 1; i < N; i++){
			for(int j = i + 1; j <= N; j++){
				if(x[i] == x[j]){
					v++;
				}
				if(x[i] +i == x[j] +j)
					v++;
				if(x[i] - i == x[j] - j)
					v++;
			}
		}
		return v;
	}
	public int evaluate(int i, int v){
		int oldV = x[i];
		x[i] = v;
		int newviolation = violation();
		x[i] = oldV;
		return newviolation;
	}
	public void localSearch(){
		genarateInitSolution();
		int it = 1;
		int v = violation();
		ArrayList<Pair> cand = new ArrayList<Pair>();
		
		while(it <= 100000 && v > 0){
			int minE = Integer.MAX_VALUE;
			int sel_i = -1; int sel_v = -1;
			cand.clear();
			for(int i  = 1; i <= N; i++){
				for(int val = 1; val <= N; val++){
					int e = evaluate(i, val);
					if(e < minE){
						minE = e;
						sel_i = i;
						sel_v = val;
					}
					else if(e == minE){
						
					}
				}
			}
			int idx = R.nextInt(cand.size());
			Pair p = cand.get(idx);
			x[sel_i] = sel_v;
			v= violation();
			System.out.println("Step "+ it+ ", new violation = "+ v);
			it++;
		}
	}
	public void slove1(int N){
		this.N = N;
		x = new int[N + 1];
		found = false;
		localSearch();
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		buoi1 queue = new buoi1();
		//queue.slove();
		queue.slove(8);
	}

}
