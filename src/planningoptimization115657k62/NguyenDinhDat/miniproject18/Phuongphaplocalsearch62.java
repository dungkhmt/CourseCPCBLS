package miniproject18;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import org.chocosolver.solver.variables.IntVar;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.basic.FuncMult;
import localsearch.functions.sum.SumFun;
import localsearch.model.ConstraintSystem;
import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
//import HillClimbingSearch;

public class Phuongphaplocalsearch62 {
	class Move {
		int i;
		int j;
		int k;
		int v;

		public Move(int i,int j,int k, int v) {
			this.i = i;
			this.j = j;
			this.k = k;
			this.v = v;
		}
	}
	int N, D, K;
	// N là nhan vien, D la so ngay lam, K là kíp trong ngày
	public int alpha, beta;
	int best;
	long start;
	long end;

	public void input() {
		Scanner sr = new Scanner(System.in);
		System.out.println("nhap du lieu: ");
		N = sr.nextInt();
		D = sr.nextInt();
		K = 4;

		alpha = sr.nextInt();
		beta = sr.nextInt();
		sr.close();
	}

	LocalSearchManager mgr;
	VarIntLS[][][] X;
	ConstraintSystem S;
	private IFunction f;
	VarIntLS y;
	
	private List<Move> cand = new ArrayList<Move>();
	private Random R = new Random();

	private void state() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[N + 5][D + 5][K + 5];
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= D; j++) {
				for (int k = 1; k <= K; k++) {
					X[i][j][k] = new VarIntLS(mgr, 0, 1);
				}
			}
		}
		S = new ConstraintSystem(mgr);
		// nhan vien lam nhieu nhat 1 ca trong ngay
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j <= D; j++) {
				X[i][j][0] = new VarIntLS(mgr,0,0);
				IFunction[] tem = new IFunction[K + 1];
				for (int k = 0; k <= K; k++) {
					tem[k] = new FuncMult(X[i][j][k], 1);
				}
				SumFun s = new SumFun(tem);
				IConstraint c = new LessOrEqual(s, 1);
				S.post(c);
			}
		}

		// C2 ngay hom truoc lam ca dem thi hom sau duoc nghi
		for (int i = 1; i <= N; i++) {
			for (int j = 1; j < D; j++) {
				IFunction[] tem = new IFunction[K+1];
				for (int k = 0; k <= K; k++) {
					if(k == 0) {
						tem[k] = new FuncMult(X[i][j][4],1);
					}
					if(k != 0){
						tem[k] = new FuncMult(X[i][j+1][k],1);
					}	
				}
				SumFun s = new SumFun(tem);
				IConstraint c = new LessOrEqual (s, 1);
				S.post(c);
			}
		}
		// c3 moi ngay trong moi ngay co it anpha nhan vien va nhieu nhat beta nhan vien
		for (int j = 1; j <= D; j++)
			for (int k = 1; k <= K; k++) {
				X[0][j][k] = new VarIntLS(mgr,0,0);
				IFunction[] tem = new IFunction[N + 1];
				for (int i = 0; i <= N; i++) {
					tem[i] = new FuncMult(X[i][j][k], 1);
				}
				SumFun s = new SumFun(tem);
				SumFun s1 = new SumFun(tem);

				IConstraint c = new LessOrEqual(alpha, s);

				IConstraint c1 = new LessOrEqual(s, beta);
				S.post(c1);
				S.post(c);
			}
		// so ca dem lon nhat ma 1 n/v duoc phan

		y = new VarIntLS(mgr, 0, D);

		for (int i = 1; i <= N; i++) {
			IFunction[] tp = new IFunction[D + 1];
			X[i][0][4] = new VarIntLS(mgr,0,0);
			for (int j = 0; j <= D; j++) {
				tp[j] = new FuncMult(X[i][j][4], 1);
			}
			SumFun s = new SumFun(tp);
			IConstraint c = new LessOrEqual(s, y);
			S.post(c);
		}

		mgr.close();
	}
	public void move() {
		Move m = cand.get(R.nextInt(cand.size()));
		X[m.i][m.j][m.k].setValuePropagate(m.v);

		int deltaold1 = Integer.MIN_VALUE;
		int [] tong = new int[N];
		for(int i=0;i < N;i++) {
			for(int j = 0;j < D;j++) {
				tong[i] += X[i+1][j+1][4].getValue();
			}
			deltaold1 = Math.max(deltaold1, tong[i]);
		}
		//if (best > deltaold1) {
			best = deltaold1;
	//	}
	}
    
	int max = 0 ;
	private void search() {
		HillClimbingSearch searcher = new HillClimbingSearch();
		searcher.search(S, 100000);

		for (int i = 1; i <= N; i++) {
			// System.out.println("staff" + i + ":");
			int tong = 0;
			for (int j = 1; j <= D; j++) {
			//	System.out.print(X[i][j][4].getValue() + " ");
			//	System.out.println(X[i][j][1].getValue() + " "+X[i][j][2].getValue()+" "+X[i][j][3].getValue()+" ");
				tong += X[i][j][4].getValue();
			}
			max = Math.max(max, tong);
		//	System.out.println(" Night : " + tong+" ");
		}
		
		int it = 0;
		best = max;
		System.out.println("-------------");
		while(it < 1000) {
//			for(int i =0;i < S.getVariables().length;i++) {
//				System.out.println(S.getVariables()[i].getValue());
//			}
			exploreNeighborhoodFunctionMaintainConstraint(X, f, cand);
			System.out.println(" violations = "+ S.violations()+",obj = "+ best);
			if(cand.size() == 0) {
				System.out.println("local optimum");
				break;	
			}
			move();
			System.out.println("Step "+ it +": violations = "+ S.violations()+",obj = "+ best);
			it++;
		}
		
		
		for (int i = 1; i <= N; i++) {
			System.out.println("staff" + i + ":");
			int tong = 0;
			for (int j = 1; j <= D; j++) {
				System.out.print(X[i][j][4].getValue() + " ");
			//	System.out.println(X[i][j][1].getValue() + " "+X[i][j][2].getValue()+" "+X[i][j][3].getValue()+" ");
				tong += X[i][j][4].getValue();
			}
			max = Math.max(max, tong);
			System.out.println(" Night : " + tong+" ");
		}
		
		System.out.println("So ca dem lon nhat ma 1 nhan vien phai lam : "+ max);
	}

	private void exploreNeighborhoodFunctionMaintainConstraint(VarIntLS[][][] x2, IFunction f2,
			List<Move> cand) {
		// TODO Auto-generated method stub
		//int minDeltaC = Integer.MAX_VALUE;
		 cand.clear();
		 int minDeltaF = Integer.MAX_VALUE;
		 for(int i= 1;i <= N;i++) {
			 for(int j=1;j <= D;j++) {
				 for(int k = 1;k <= K;k++) {
					 for(int v= x2[i][j][k].getMinValue(); v <= x2[i][j][k].getMaxValue();v++) {
					//	 int deltaC = s2.getAssignDelta(x2[i][j][k],v); 
						 int deltaF = getdelta(X[i][j][k], v);
					//	 System.out.println("deltaC  + deltaF "+deltaC + " " + deltaF + " " + minDeltaC + " " + minDeltaF);
					//	 if(deltaC > 0) continue;
						 if( deltaF < 0) {
							 if( deltaF < minDeltaF) {
								 cand.clear();
								 cand.add(new Move(i,j,k,v));
							//	 minDeltaC = deltaC;
								 minDeltaF = deltaF;
								
							 }else if(deltaF == minDeltaF) {
								 cand.add(new Move(i,j, k, v));
							 }
//						 if(deltaC > 0) continue;// ignore worse constraint violations
//						 if(deltaF < 0){// consider only better neighbors in hill climbing
//						 if(deltaF < minDeltaF){
//						 cand.clear(); cand.add(new Move(i,j,k,v)); minDeltaF = deltaF;
//						 }else if(deltaF == minDeltaF)
//						 cand.add(new Move(i,j,k,v));
//						 }

						 }
					 }
				 }
			 }
		 } 
		
	}
	private int getdelta(VarIntLS pos, int val) {
		// TODO Auto-generated method stub
		int deltanew1 = Integer.MIN_VALUE;
		
		int deltaold1 = Integer.MIN_VALUE;
	
		int [] tong = new int[N];
		for(int i=0;i < N;i++) {
			for(int j = 0;j < D;j++) {
				tong[i] += X[i+1][j+1][4].getValue();
			}
			
			deltaold1 = Math.max(deltaold1, tong[i]);
		}
		int oldValue = pos.getValue();
		pos.setValue(val);
		for(int i=0;i < N;i++) {
			for(int j =0;j < D;j++) {
				tong[i] += X[i+1][j+1][4].getValue();
			}
			deltanew1 = Math.max(deltanew1,tong[i]);
   		}
		pos.setValue(oldValue);
		
		return deltanew1-deltaold1;
	
	}
	public void solve() {
		state();
		search();
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Phuongphaplocalsearch62 app = new Phuongphaplocalsearch62();
		app.input();
		System.out.println("waiting ------");
		app.start  = System.currentTimeMillis();
		app.solve();
		app.end = System.currentTimeMillis();
		System.out.println("time: "+ (app.end-app.start) );
	}

}
