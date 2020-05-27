package planningoptimization115657k62.nguyenthinhung.CVRP;



import java.util.*;

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
import localsearch.domainspecific.vehiclerouting.vrp.functions.TotalCostVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightEdgesVR;
import localsearch.domainspecific.vehiclerouting.vrp.invariants.AccumulatedWeightNodesVR;

public class Vrp_CblsVr{
	int K = 2;// number of routes
	int N = 6;// number of clients
	int capacity = 11;
	// int[] demand = { 0, 4, 2, 15, 12, 3, 5 };
	int[][] c = { { 0, 3, 2, 1, 4, 3, 7 },
			{ 2, 0, 2, 3, 5, 3, 9 }, 
			{ 1, 1, 0, 2, 4, 2, 4 }, 
			{ 5, 3, 2, 0, 1, 1, 7 },
			{ 3, 11, 5, 1, 0, 3, 6 }, 
			{ 6, 3, 2, 1, 4, 0, 9 }, { 2, 3, 2, 1, 2, 1, 0 } };

	ArrayList<Point> start;
	ArrayList<Point> end;
	ArrayList<Point> clientPoints;
	ArrayList<Point> allPoints;
	ArcWeightsManager awm;// luu tru trong so tren canh noi giua cac point
	// NodeWeightsManager nwm;// luu tru trong so tren cac point
	HashMap<Point, Integer> mapPoint2ID;
// modelling
	VRManager mgr;
	VarRoutesVR XR;// bien loi giai (luu tap cac route)
	ConstraintSystemVR CS;
	LexMultiFunctions F;
	IFunctionVR obj;
	IFunctionVR[] d;// d[k] la demand cua route k
	IFunctionVR[] cost;// cost[k] la chieu dai cua route thu k
	Random R = new Random();

	public void mapping() {
		start = new ArrayList<Point>();
		end = new ArrayList<Point>();
		clientPoints = new ArrayList<Point>();
		allPoints = new ArrayList<Point>();
		mapPoint2ID = new HashMap<Point, Integer>();
// khoi tao cac diem bat dau va ket thuc cua cac xe (route)
		for (int k = 1; k <= K; k++) {
			Point s = new Point(0);
			Point t = new Point(0);
			start.add(s);
			end.add(t);
			allPoints.add(s);
			allPoints.add(t);
			mapPoint2ID.put(s, 0);
			mapPoint2ID.put(t, 0);
		}

		for (int i = 1; i <= N; i++) {
			Point p = new Point(i);
			clientPoints.add(p);
			allPoints.add(p);
			mapPoint2ID.put(p, i);
		}
		awm = new ArcWeightsManager(allPoints);
		// nwm = new NodeWeightsManager(allPoints);
		for (Point p : allPoints) {
			for (Point q : allPoints) {
				int ip = mapPoint2ID.get(p);
				int iq = mapPoint2ID.get(q);
				awm.setWeight(p, q, c[ip][iq]);
			}
		}
		// for (Point p : allPoints)
			// nwm.setWeight(p, demand[mapPoint2ID.get(p)]);
	}

	public void stateModel() {
		mgr = new VRManager();
		XR = new VarRoutesVR(mgr);
		for (int i = 0; i < start.size(); i++) {
			Point s = start.get(i);
			Point t = end.get(i);	
			XR.addRoute(s, t);// them 1 route vao phuong an (s --> t)
		}
		for (Point p : clientPoints)
			XR.addClientPoint(p);// khai bao XR co the se di qua diem p
// thiet lap rang buoc
		CS = new ConstraintSystemVR(mgr);
		// AccumulatedWeightNodesVR accDemand = new AccumulatedWeightNodesVR(XR, nwm);
		AccumulatedWeightEdgesVR accW = new AccumulatedWeightEdgesVR(XR, awm);

		d = new IFunctionVR[K];// demand on routes
		// for (int k = 1; k <= K; k++) {
			// Point tk = XR.endPoint(k);// diem cuoi cung cua route thu k
			// d[k - 1] = new AccumulatedNodeWeightsOnPathVR(accDemand, tk);
			// CS.post(new Leq(d[k - 1], capacity));
		// }
		cost = new IFunctionVR[K];
		for (int k = 1; k <= K; k++) {
			Point tk = XR.endPoint(k);
			cost[k - 1] = new AccumulatedEdgeWeightsOnPathVR(accW, tk);
		}
		obj = new TotalCostVR(XR, awm);// tong khoang cach di chuyen cua K xe (route)
		mgr.close();
	}

	public void initialSolution() {
		ArrayList<Point> listPoints = new ArrayList<Point>();
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			listPoints.add(XR.startPoint(k));
		}
		for (Point p : clientPoints) {
			Point x = listPoints.get(R.nextInt(listPoints.size()));
			mgr.performAddOnePoint(p, x);
			System.out.println(XR.toString() + "violations = " + CS.violations() + ", cost = " + obj.getValue());
			listPoints.add(p);
		}
	}

	public void exploreNeighborhood(ArrayList<Move> cand) {
		cand.clear();
		int minDeltaC = Integer.MAX_VALUE;
		double minDeltaF = minDeltaC;
		for (int k = 1; k <= XR.getNbRoutes(); k++) {
			for (Point y = XR.startPoint(k); y != XR.endPoint(k); y = XR.next(y)) {
				for (Point x : clientPoints)
					if (x != y && x != XR.next(y)) {
						int deltaC = CS.evaluateOnePointMove(x, y);
						double deltaF = obj.evaluateOnePointMove(x, y);
						if (!(deltaC < 0 || deltaC == 0 && deltaF < 0))
							continue;
						if (deltaC < minDeltaC || deltaC == minDeltaC && deltaF < minDeltaF) {
							cand.clear();
							cand.add(new Move(x, y));
							minDeltaC = deltaC;
							minDeltaF = deltaF;
						} else if (deltaC == minDeltaC && deltaF == minDeltaF)
							cand.add(new Move(x, y));
					}
			}
		}
	}

	public void search(int maxIter) {
		initialSolution();
		int it = 0;
		ArrayList<Move> cand = new ArrayList<Move>();
		while (it < maxIter) {
			exploreNeighborhood(cand);
			if (cand.size() <= 0)
				System.out.println("Reach local optimum");
			break;
		}
	}

	class Move {
		Point x;
		Point y;

		public Move(Point x, Point y) {
			this.x = x;
			this.y = y;
		}
	}

	public static void main(String[] args) {
		Vrp_CblsVr A = new Vrp_CblsVr();
		A.mapping();
		A.stateModel();
		A.search(10000000);
	}
}