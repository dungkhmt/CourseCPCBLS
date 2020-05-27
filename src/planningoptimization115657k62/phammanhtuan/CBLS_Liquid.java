package phammanhtuan;

import java.util.ArrayList;
import java.util.Random;

import org.chocosolver.solver.constraints.Constraint;

import com.google.ortools.algorithms.main;

import localsearch.constraints.basic.AND;
import localsearch.constraints.basic.LessThan;
import localsearch.constraints.basic.NotEqual;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class CBLS_Liquid {

	int N = 5;
	int M = 20;
	int K = 6;
	int c[] = {20,15,10,20,20,25,30,15,10,10,20,25,20,10,30,40,25,35,10,10};
	int d[] = {60,70,90,80,100};
	int q[][] = {
			{0,1},
			{7, 8},
			{12, 17},
			{8, 9},
			{1, 2, 9},
			{0, 9, 12}
	};
	int cost[];
	class AssignMove{
		int i; int v;
		public AssignMove(int i, int v){
			this.i = i; this.v = v;
		}
	}
	public int AssignedDelta (VarIntLS x,int val,int i) {
		cost[x.getValue()] -= c[i];
		cost[val] += c[i];
		if (cost[x.getValue()]<=d[x.getValue()]) return -1;
		if (cost[val]>d[val]) return 1;
		return 0;
	}
	public boolean check() {
		for (int i=0;i<N;i++) {
			if (cost[i]>d[i]) return false;
		}
		return true;
	}
	public void solver () {
		LocalSearchManager mng = new LocalSearchManager();
		VarIntLS X[] = new VarIntLS[M];
		for (int i=0;i<M;i++) {
			X[i] = new VarIntLS(mng, 0,N-1);
		}
		ConstraintSystem S = new ConstraintSystem(mng);
		for (int k=0;k<K;k++) {
			if  (q[k].length==2) {
				S.post(new NotEqual(X[q[k][0]], X[q[k][1]]));
			} else {
				S.post(new AND(new NotEqual(X[q[k][0]], X[q[k][1]]),new NotEqual(X[q[k][1]], X[q[k][2]])));
			}
		}
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
		cost = new int[N];
		for (int i=0;i<N;i++) {
			for (int j=0;j<M;j++) {
				if (X[j].getValue()==i) {
					cost[i]+=c[j];
				}
			}
		}
		int it = 0;
		// F(S.violations(), f)
		Random R = new Random();
		while(it < 100000){
			cand.clear();
			int minDelta = Integer.MAX_VALUE;
			// explore neighborhood (kham pha lang gieng)
			// lang gieng: chon 1 bien va gan gia tri moi
			for(int i = 0; i < M; i++){
				for(int v = 0;v < N;v++) {
					int delta = AssignedDelta(X[i], v,i);
					if (delta<minDelta) {
						cand.clear();
						cand.add(new AssignMove(i, v));
						minDelta = delta;
					} else if (delta==minDelta) {
						cand.add(new AssignMove(i, v));
					}
				}
			}
			
			if(cand.size() == 0 && check()){
				System.out.println("Reach  local optimum"); break;
			}
			
			AssignMove m = cand.get(R.nextInt(cand.size()));
			
			X[m.i].setValuePropagate(m.v);
			for (int i=0;i<M;i++) {
				System.out.print(X[i].getValue()+" ");
			}
			System.out.println();
			it++;
		}
		for (int j=0;j<N;j++) {
			System.out.print("Thung "+j+": ");
			for (int i=0;i<M;i++) {
				if (X[i].getValue()==j) {
					System.out.print(i +" ");
				}
			}
			System.out.println();
		}
	}
	public static void main(String[] args) {
		CBLS_Liquid app = new CBLS_Liquid();
		app.solver();
	}
}
