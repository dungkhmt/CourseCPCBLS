package TSP;

import java.util.HashSet;

// Sinh tap con cua {0, 1, ... N-1}
public class SubsetGenerator{
	int N;
	int[] X;// represents binary sequence
	
	public SubsetGenerator(int N){
		this.N = N;
	}
	
	public HashSet<Integer> first(){
		X = new int[N];
		for(int i = 0; i < N; i++)
			X[i] = 0;
		HashSet<Integer> S = new
		HashSet<Integer>();
		for(int i = 0; i < N; i++)
			if(X[i] == 1) S.add(i);
		return S;
	}
	
	public HashSet<Integer> next(){
		int j = N-1;
		while(j >= 0 && X[j] == 1){
			X[j] = 0; j--;
		}
		if(j >= 0){
			X[j] = 1;
			HashSet<Integer> S = new HashSet<Integer>();
			for(int i = 0; i < N; i++)
				if(X[i] == 1) S.add(i);
					return S;
		}else{
			return null;
		}
	}
}
