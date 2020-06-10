package planningoptimization115657k62.NguyenQuynhLoc;


import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class CovidLS {
	int N; // so doan khach
	int M; // so khu cach ly
	int [] s; // so luong nguoi trong moi doan khach
	int [] c; // so luong nguoi ma khu cach ly chua toi da
	Random Rd= new Random();
	LocalSearchManager mgr;
	VarIntLS [] X;
	ConstraintSystem S;
	IFunction [] V;
	public void ReadFile(String path_) {
 		// doc du lieu tu file
 		// Input:
 		//Dong dau chua 2 so nguyen N, M
 		//Dong thu 2 chua N so nguyen la so luong nguoi s cua moi doan khach
 		// Dong thu 3 chua M so nguyen la gioi han c nguoi cua khu cach ly
 		// Ham doc file doc tu 1.
 		Scanner sc;
		try {
			sc = new Scanner(new File(path_));
			 N = sc.nextInt();
	
		     M = sc. nextInt();
		     s=new int[N];
		     c=new int[M];
		     //System.out.println(N);
		     //System.out.println(M);
		     
		       for(int i = 0; i < N; i++) {
		    	   s[i] = sc.nextInt();
		    	   //System.out.println(i+" : " +s[i]);
		       }
		       for(int i = 0; i < M; i++) {
		    	   c[i] = sc.nextInt();
		    	   //System.out.println(i+" : " +c[i]);
		       }	 
		       System.out.println("Reading Complete!");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	class Move{
		int i;
		int v;
		public Move(int x,int y){
			this.i=x;
			this.v=y;
		}
	}
	public void stateModel() {
		mgr= new LocalSearchManager();
		X= new VarIntLS[N];
		S=new ConstraintSystem(mgr);
		V=new IFunction[M];
		for(int i=0;i<N;i++)
			X[i]=new VarIntLS(mgr, 0,M-1);
		// constraint
		V=new IFunction[M];
		for(int j=0;j<M;j++) {
			V[j]=new ConditionalSum(X, s, j);
			S.post(new LessOrEqual(V[j], c[j]));
		}
		mgr.close();
	}
	public void getresult() {
		int dis_max=Integer.MIN_VALUE;
		for(int i=0;i<N;i++)
			dis_max=Math.max(dis_max, X[i].getValue()+N-i);
		for(int i=0;i<M;i++) {
			System.out.print("khu "+(i+1)+": ");
			for(int j=0;j<N;j++) {
				if(X[j].getValue()==i) {
					System.out.print((j+1)+" ");
				}
			}
			System.out.println();
		}
		System.out.println("dis max =" + dis_max);
		System.out.println("Violations= "+S.violations());
	}
	
	public int getAssignDelta(int pos, int val) {
		int deltanew=Integer.MIN_VALUE;
		int deltaold=Integer.MIN_VALUE;
		for(int i=0;i<N;i++) {
			deltaold=Math.max(deltaold, X[i].getValue()+N-i);
		}
		for(int i=0;i<N;i++) {
			if(i==pos) {
				deltanew=Math.max(deltanew, val+N-pos);
			}else {
				deltanew=Math.max(deltanew, X[i].getValue()+N-i);
			}
		}
		return deltanew-deltaold;
	}
	
	public void Search1(VarIntLS[] X,ConstraintSystem S,int maxInter,int tbl,int maxStable) {
		int [][] tabu= new int[N][M];
		for(int i=0;i<N;i++)
			for(int j=0;j<M;j++)
				tabu[i][j]=-1;
		int nic=0;
		ArrayList<Move> cand= new ArrayList<>();
		int it=0;
		int best=S.violations();
		while(it<maxInter&&S.violations()>0) {
			int mindelta=Integer.MAX_VALUE;
			for(int i=0;i<N;i++) {
				for(int v=X[i].getMinValue();v<X[i].getMaxValue();v++) {
					if(X[i].getValue()!=v) {
						int delta=S.getAssignDelta(X[i], v);
						if(tabu[i][v]<=it||delta+S.violations()<best) {
							if(delta<mindelta) {
								mindelta=delta;
								cand.clear();
								cand.add(new Move(i,v));
							}else if(delta==mindelta){
								cand.add(new Move(i,v));
							}
						}
					}
				}
			}
			Move m = cand.get(Rd.nextInt(cand.size()));
			X[m.i].setValuePropagate(m.v);
			tabu[m.i][m.v]=it+tbl;
			if(S.violations()<best) {
				best=S.violations();
				nic=0;
			}else {
				nic++;
				if(nic>=maxStable) { //restart
					for(int i = 0; i < N; i++) {
						X[i].setValuePropagate(Rd.nextInt(M));
					}
					if(S.violations()<best)
						best=S.violations();
				}
			}
			//System.out.println("Step " + it + " violations = " + S.violations());
			it++;
		}
	}
	public boolean checkstop(VarIntLS [] X) {
		for(int i=0;i<M;i++) {
			if(X[i].getValue()!=i)
				return false;
		}
		return true;
	}
	
	public void Search2_v1(VarIntLS[] X,ConstraintSystem S,int maxInter,int maxStable) {
		ArrayList<Move> cand= new ArrayList<>();
		int it=0;
		int nic=0;
		while(it<maxInter&&checkstop(X)!=true) {
			int mindeltaF=Integer.MAX_VALUE;
			//cand.clear();
			for(int i=0;i<N;i++) {
				for(int v=X[i].getMinValue();v<X[i].getMaxValue();v++) {
					if(X[i].getValue()!=v) {
						int deltaS=S.getAssignDelta(X[i], v);
						if(deltaS>0)
							continue;
						int deltaF=getAssignDelta(i, v);
						if(deltaF<=0) {
							if(deltaF<mindeltaF) {
								mindeltaF=deltaF;
								cand.clear();
								cand.add(new Move(i,v));
							}else if(deltaF==mindeltaF){
								cand.add(new Move(i,v));
							}
						}
					}
				}
			}
			if(cand.size()==0)
				break;
			int pos_new=Rd.nextInt(cand.size());
			Move m = cand.get(pos_new);
			X[m.i].setValuePropagate(m.v);
			if(mindeltaF<0) {
				nic=0;
			}else {
				nic+=1;
				if(nic>=maxStable)
					break;
			}
			System.out.println("Step " + it + " mindelta = " + mindeltaF);
			it++;
		}
	}
	
	
	public void Search2_v2(VarIntLS[] X,ConstraintSystem S,int maxInter,int tbl,int maxStable) {
		ArrayList<Move> cand= new ArrayList<>();
		int it=0;
		int nic=0;
		int [][]tabu = new int[N][M];
		for(int i=0;i<N;i++)
			for(int j=0;j<M;j++)
				tabu[i][j]=-1;
		while(it<maxInter&&checkstop(X)!=true) {
			int mindeltaF=Integer.MAX_VALUE;
			//cand.clear();
			for(int i=0;i<N;i++) {
				for(int v=X[i].getMinValue();v<X[i].getMaxValue();v++) {
					if(X[i].getValue()!=v) {
						int deltaS=S.getAssignDelta(X[i], v);
						if(deltaS>0)
							continue;
						int deltaF=getAssignDelta(i, v);
						if(deltaF<0) {
							if(deltaF<mindeltaF) {
								cand.clear();
								mindeltaF=deltaF;
							}
							cand.add(new Move(i,v));
						}
						else if(deltaF==0&&tabu[i][v]<=it) {
							cand.add(new Move(i,v));
						}
					}
				}
			}
			if(cand.size()==0)
				break;
			Move m = cand.get(Rd.nextInt(cand.size()));
			tabu[m.i][m.v]=it+tbl;
			X[m.i].setValuePropagate(m.v);
			System.out.println("Step " + it + " bestdelta = " + mindeltaF);
			it++;
			if(mindeltaF<0) {
				nic=0;
			}else {
				nic+=1;
				if(nic>=maxStable)
					break;
			}
		}
	}
	public void solve() {
		ReadFile("/home/lnq/Desktop/20192/tulkh/dataproject/1000s750c.txt");
		stateModel();
		boolean checksearch1=false;
		for(int i=0;i<1000;i++) {
			Search1(X, S, 10000, 5, 5);
			if(S.violations()==0) {
				System.out.println("complete Search 1");
				checksearch1=true;
				break;
			}				
		}
		if(!checksearch1) {
			System.out.println("no solution");
			return;
		}
		Search2_v1(X,S,1000,350);
		//Search2_v2(X, S, 1000,10,400);
	}
	public static void main(String[] args) {
		CovidLS app= new CovidLS();
		long t=System.currentTimeMillis();
		app.solve();
		app.getresult();
		System.out.println("time = "+(System.currentTimeMillis()-t)/1000.0);
	}
}
