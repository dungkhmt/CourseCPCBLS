package cbls115676khmt61.ngodoannghia_20162905;


import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ConditionalSums {
	
	public static void main(String[] args) {
		LocalSearchManager mgr = new LocalSearchManager();
		
		int M = 7; // 7 ittem
		int N = 3; // 3 bin
		int w[] = {2,3,1,4,5,6,6};
		VarIntLS[] X = new VarIntLS[M];
		for(int i = 0; i < M; i++)
			X[i] = new VarIntLS(mgr, 0, N-1);
		IFunction f = new ConditionalSum(X, w, 1);
		
		mgr.close();
		X[0].setValuePropagate(0);
		X[1].setValuePropagate(1);
		X[2].setValuePropagate(2);
		X[3].setValuePropagate(0);
		X[4].setValuePropagate(1);
		X[5].setValuePropagate(1);
		X[6].setValuePropagate(0);
		
		System.out.print(f.getValue());
	}
}
