package cbls115676khmt61.HoangVD_20161728;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import localsearch.constraints.basic.IsEqual;
import localsearch.functions.sum.Sum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class GraphPartitioning_CBLS {
	int N;
	int c[][];
	VarIntLS x[];
	ConstraintSystem S;
	LocalSearchManager mgr;
	GraphPartitioningCost f;
	Random R;
	class SwapMove {
		int i, j;
		public SwapMove(int i, int j) {
			this.i = i; this.j = j;
		}
	}
	class AssignMove {
		int i, j;
		public AssignMove(int i, int j) {
			this.i = i; this.j = j;
		}
	}
	
	public void generateInitialSolutionBalance() {
		for (int i = 0; i < N; i++) {
			if (i < N / 2) {
				x[i].setValue(0);
			} else {
				x[i].setValue(1);
			}
		}
	}
	
	public void search1(int maxIter) {
		generateInitialSolutionBalance();
		printSolution();
		ArrayList<SwapMove> cand = new ArrayList<SwapMove>();
		int it = 0;
		while(it++ < maxIter) {
			cand.clear();
			int minDeltaF = Integer.MAX_VALUE;
			for (int i = 0; i < N; i++) {
				for (int j = i+1; j < N; j++) {
					if (x[i].getValue() != x[j].getValue()) {
						int d = f.getSwapDelta(x[i], x[j]);
						if (d < 0) {
							if (d < minDeltaF) {
								minDeltaF = d;
								cand.clear();
								cand.add(new SwapMove(i, j));
							}
							else if (d == minDeltaF) {
								cand.add(new SwapMove(i, j));
							}
						}
					}
				}
			}
			if (cand.size() == 0) {
				System.out.println("Reach local optimum");
				break;
			}
			SwapMove m = cand.get(R.nextInt(cand.size()));
			x[m.i].swapValuePropagate(x[m.j]);
			System.out.println("Step " + it + ", obj = " + f.getValue());
		}
				
	}
	
	public void search2(int maxIter) {
		ArrayList<AssignMove> cand = new ArrayList<AssignMove>();
		int it = 0;
		while (it++ < maxIter) {
			cand.clear();
			int minDeltaC = Integer.MAX_VALUE, minDeltaF = Integer.MAX_VALUE;
			for (int i = 0; i < N; i++) {
				int v = 1 - x[i].getValue();
				int deltaC = S.getAssignDelta(x[i], v);
				int deltaF = S.getAssignDelta(x[i], v);
				if (deltaC < 0 || deltaC == minDeltaC && deltaF < minDeltaF) {
					cand.clear();
					cand.add(new AssignMove(i, v));
				}
				else if (deltaC == minDeltaC && deltaF == minDeltaF) {
					cand.add(new AssignMove(i, v));
				}
			}
			if (cand.size() == 0) {
				System.out.println("Reach local optimum");
			}
			AssignMove m = cand.get(R.nextInt(cand.size()));
			x[m.i].setValuePropagate(m.j);
			System.out.println("Step " + it + ", obj = " + f.getValue() + ", violations = " + S.violations());
		}
	}
	
	public void stateModel() {
		mgr = new LocalSearchManager(); 
		S = new ConstraintSystem(mgr);
		R = new Random();
		x = new VarIntLS[N];
		for(int i = 0; i < N; i++) {
			x[i] = new VarIntLS(mgr, 0, 1);
		}
		S.post(new IsEqual(new Sum(x), N/2));
		f = new GraphPartitioningCost(c, x); 
		mgr.close();
	}
	
	@SuppressWarnings("resource")
	public void readfile(String source) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(source));
		N = scanner.nextInt();
		c = new int[N][N];
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				c[i][j] = scanner.nextInt();
			}
		}
	}
	
	public void printSolution() {
		System.out.print("Solution Found: ");
		for (int i = 0; i < N; i++) {
			System.out.print(x[i].getValue() + " ");
		}
		System.out.println("\nobj = " + f.getValue());
	}
	public static void main(String args[]) throws FileNotFoundException {
		GraphPartitioning_CBLS graphPartitioning_CBLS = new GraphPartitioning_CBLS();
		graphPartitioning_CBLS.readfile("D:\\Document\\Java\\Test-CBLS\\File\\gp-10.txt");
		graphPartitioning_CBLS.stateModel();
		graphPartitioning_CBLS.search1(10000);
		graphPartitioning_CBLS.printSolution();
		for(int i = 0; i < graphPartitioning_CBLS.N; i++) {
			for (int j = 0; j < graphPartitioning_CBLS.N; j++) {
				if (graphPartitioning_CBLS.x[i].getValue() != graphPartitioning_CBLS.x[j].getValue()) {
					System.out.print(graphPartitioning_CBLS.c[i][j] + " ");
				}
				else {
					System.out.print("0 ");
				}
			}
			System.out.println();
		}
	}
}
