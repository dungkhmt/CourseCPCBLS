package planningoptimization115657k62.hoangthanhlam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import localsearch.domainspecific.vehiclerouting.vrp.ConstraintSystemVR;
import localsearch.domainspecific.vehiclerouting.vrp.IFunctionVR;
import localsearch.domainspecific.vehiclerouting.vrp.VRManager;
import localsearch.domainspecific.vehiclerouting.vrp.VarRoutesVR;
import localsearch.domainspecific.vehiclerouting.vrp.constraints.leq.Leq;
import localsearch.domainspecific.vehiclerouting.vrp.entities.ArcWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.NodeWeightsManager;
import localsearch.domainspecific.vehiclerouting.vrp.entities.Point;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedEdgeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.AccumulatedNodeWeightsOnPathVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.LexMultiFunctions;
import localsearch.domainspecific.vehiclerouting.vrp.functions.MaxVR;
import localsearch.domainspecific.vehiclerouting.vrp.functions.plus.Plus;
import localsearch.domainspecific.vehiclerouting.vrp.functions.plus.PlusFunctionFunction;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;

public class MiniProject {
	int N = 6;
	int K = 2;
    
    int[] d;
    int[][] t;
    int totalD;
	
	public MiniProject (int N, int K) {
		this.N = N;
		this.K = K;
		
		Random rd = new Random();
		int r = 9;
		
		this.d = new int[N+1];
		this.t = new int[N+1][N+1];
		for (int i = 0; i <= N; i++) {
			d[i] = 1 + rd.nextInt(r);
			for (int j = 0; j <= N; j++) {
				if (i == j || j == 0) t[i][j] = 0;
				else {
					t[i][j] = 1 + rd.nextInt(r);
				}
			}
		}
		d[0] = 0;
		
		totalD = 0;
		for (int i = 0; i <= N; i++) totalD += d[i];
		totalD -= 1;
	}
	
	ArrayList<Point> start;
	ArrayList<Point> end;
	ArrayList<Point> clientPoint;
	ArrayList<Point> allPoint;
	
	ArcWeightsManager awm;
	NodeWeightsManager nwm;
	HashMap<Point, Integer> mapPoint2ID;
	
	VRManager mgr;
	VarRoutesVR XR;
	ConstraintSystemVR S;
	LexMultiFunctions F;
	
	IFunctionVR obj;
	IFunctionVR[] T;
	IFunctionVR[] D;
	IFunctionVR[] C;
	Random R = new Random();
	
	public void printDataInput() {
		System.out.println("t[][] = {");
		for (int i = 0; i <= N; i++) {
			System.out.print("\t");
			for (int j = 0; j <= N; j++) {
				System.out.print(t[i][j] + ", ");
			}
			System.out.println();
		}
		System.out.println("}");
		
		System.out.print("d[] = {");
		for (int i = 0; i <= N; i++) {
			System.out.print(d[i] + ", ");
		}
		System.out.println("}");
	}
	
	public void mapping() {
		start = new ArrayList<Point>();
		end = new ArrayList<Point>();
		clientPoint = new ArrayList<Point>();
		allPoint = new ArrayList<Point>();
		mapPoint2ID = new HashMap<Point, Integer>();
		
		for (int i = 0; i < K; i++) {
			Point s = new Point(0);
			Point t = new Point(0);
			start.add(s); end.add(t);
			allPoint.add(s); allPoint.add(t);
			mapPoint2ID.put(s, 0);
			mapPoint2ID.put(t, 0);
		}
		
		for (int i = 1; i <= N; i++) {
			Point p = new Point(i);
			clientPoint.add(p);
			allPoint.add(p);
			mapPoint2ID.put(p, i);
		}
		
		awm = new ArcWeightsManager(allPoint);
		nwm = new NodeWeightsManager(allPoint);
		
		for (Point p: allPoint) {
			int _p = mapPoint2ID.get(p);
			for (Point q: allPoint) {
				int _q = mapPoint2ID.get(q);
				awm.setWeight(p, q, t[_p][_q]);
			}
			nwm.setWeight(p, d[_p]);
		}
	}
	
	public void stateModel() {
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		
		for (int i = 0; i < K; i++) {
			Point s = start.get(i);
			Point t = end.get(i);
			XR.addRoute(s, t);
		}
		
		for (Point p: clientPoint) {
			XR.addClientPoint(p);
		}
		
		S = new ConstraintSystemVR(mgr);
		AccumulatedWeightEdgesVR accE = new AccumulatedWeightEdgesVR(XR, awm);
		AccumulatedWeightNodesVR accN = new AccumulatedWeightNodesVR(XR, nwm);
		
		T = new IFunctionVR[K];
		D = new IFunctionVR[K];
		C = new IFunctionVR[K];
		for (int i = 1; i <= K; i++) {
			Point t = XR.endPoint(i);
			D[i-1] = new AccumulatedNodeWeightsOnPathVR(accN, t);
			S.post(new Leq(1, D[i-1]));
			C[i-1] = new AccumulatedEdgeWeightsOnPathVR(accE, t);
			T[i-1] = new PlusFunctionFunction(D[i-1], C[i-1]); // ???
		}
		obj = new MaxVR(C);
		mgr.close();
	}
	
	public void initSolution() {
		ArrayList<Point> listPoint = new ArrayList<Point>();
		for (int i = 1; i <= K; i++) {
			listPoint.add(XR.startPoint(i));
		}
		
		for (Point p: clientPoint) {
			Point x = listPoint.get(R.nextInt(listPoint.size()));
			mgr.performAddOnePoint(p, x);
			System.out.println(XR.toString() + "violations = " + S.violations() + ", obj = " + obj.getValue());
			listPoint.add(p);
		}
	}
	
	class Move {
		Point x; Point y;
		public Move(Point x, Point y) {
			this.x = x; this.y = y;
		}
	}
	
	public void exploreNeighborhood(ArrayList<Move> candicate) {
		candicate.clear();
		int minDeltaC = Integer.MAX_VALUE;
		double minDeltaF = minDeltaC;
		for (int k = 1; k <= K; k++) {
			for (Point y = XR.startPoint(k); y != XR.endPoint(k); y = XR.next(y)) {
				for (Point x: clientPoint) {
					if (x != y && x != XR.next(y)) {
						int deltaC = S.evaluateOnePointMove(x, y);
						// System.out.println("deltaC = " + deltaC);
						double deltaF = obj.evaluateOnePointMove(x, y);
						// System.out.println("deltaF = " + deltaF);
						if (!(deltaC < 0 || deltaC == 0 && deltaF < 0)) continue;
						if (deltaC < minDeltaC || deltaC == minDeltaC && deltaF < minDeltaF) {
							candicate.clear();
							candicate.add(new Move(x, y));
							minDeltaC = deltaC; minDeltaF = deltaF;
						} else if (deltaC == minDeltaC && deltaF == minDeltaF) {
							candicate.add(new Move(x, y));
						}
					}
				}
			}
		}
	}
	
	public void search(int maxIter) {
		initSolution();
		System.out.println("---oOo---");
		int it = 0;
		ArrayList<Move> candicate = new ArrayList<Move>();
		while (it < maxIter) {
			exploreNeighborhood(candicate);
			if (candicate.size() <= 0) {
				System.out.println("Reach local optimum");
				break;
			}
			Move m = candicate.get(R.nextInt(candicate.size()));
			mgr.performOnePointMove(m.x, m.y);
			System.out.println("Step "+ it + ":\n" + XR.toString() + "violations = " + S.violations() + ", obj = " + obj.getValue());
			it++;
		}
	}
	
	public static void main (String[] args) {
		MiniProject mini = new MiniProject(20, 4);
		mini.printDataInput();
		mini.mapping();
		mini.stateModel();
		mini.search(100);
	}
}