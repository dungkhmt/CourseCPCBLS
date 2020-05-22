package planningoptimization115657k62.phamthanhdong;

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

public class Vehicle {
	int K = 5;// number of shelves -so kien hang
	int N = 4;// number of product - so san pham
	//int capacity = 11;// ??

	int[] need = { 0, 9, 1, 3, 6 };// need, chu y la san pham 0 thi ko co gi

	// distance
	int[][] c = { { 0, 16, 6, 4, 21, 4 }, { 14, 0, 3, 17, 17, 13 }, { 16, 1, 0, 15, 11, 20 }, { 91, 8, 3, 0, 25, 16 },
			{ 12, 4, 5, 10, 0, 24 }, { 8, 7, 18, 21, 25, 0 } };

	// Q
	int[][] Q = { { 11, 13, 6, 6, 9 }, { 11, 5, 14, 15, 5 }, { 3, 8, 9, 3, 4 }, { 6, 4, 6, 5, 7 } };

	ArrayList<Point> start;// =0, diem xuat phat
	ArrayList<Point> end;// = 0, ve
	ArrayList<Point> clientPoints;// quan trong - lo trinh cac diem di qua
	ArrayList<Point> allPoints;// toan bo, toan lo trinh !!!
	ArcWeightsManager awm;// luu tru trong so tren canh noi giua cac point
	NodeWeightsManager nwm;// luu tru so luong hang hoa tren cac point, chinh la Q
	HashMap<Point, Integer> mapPoint2ID;

// modelling
	VRManager mgr; // danh sach cac kho di qua
	VarRoutesVR XR;// bien loi giai (luu tap cac route) - lo trinh
	ConstraintSystemVR CS;
	LexMultiFunctions F;
	
	IFunctionVR obj;//luu tru so luong san pham can thiet
	IFunctionVR[] d;// d[k] la q cua mat hang k
	IFunctionVR[] cost;// cost[k] la chieu dai duong di cua route thu k
	// ********can toi uu***************
	Random R = new Random();// ok

	// khoi tao - ko co gi !!!
	public void mapping() {

		start = new ArrayList<Point>();
		end = new ArrayList<Point>();
		clientPoints = new ArrayList<Point>();
		allPoints = new ArrayList<Point>();
		mapPoint2ID = new HashMap<Point, Integer>();

// khoi tao cac diem bat dau va ket thuc cua xe (route)

		Point s = new Point(0);
		Point t = new Point(0);
		start.add(s);// them bat dau //bat dau tu 0
		end.add(t);// ve lai 0
		allPoints.add(s);// luc dau, lo trinh chi co diem 0
		allPoints.add(t);// ket thuc cung vay
		mapPoint2ID.put(s, 0);
		mapPoint2ID.put(t, 0);

		// nhap so san pham can thiet cua moi mat hang, clientPoint chinh la tung mat
		// hang
		for (int i = 1; i <= N; i++) 
		{
			Point p = new Point(i);
			clientPoints.add(p);
			allPoints.add(p);
			mapPoint2ID.put(p, i);
		}

		awm = new ArcWeightsManager(allPoints);//co bao nhieu trong so
		nwm = new NodeWeightsManager(allPoints);//co bao nhieu hang 

		for (Point p : allPoints) {
			for (Point q : allPoints) {
				int ip = mapPoint2ID.get(p);
				int iq = mapPoint2ID.get(q);
				awm.setWeight(p, q, c[ip][iq]);// nhap trong so duong di giua cac ke hang
			}
		}

		// thiet lap nhu cau cua tung loai hang
		for (Point p : allPoints)
			nwm.setWeight(p, need[mapPoint2ID.get(p)]);
	}
	
	// bo qua
	class Move {
		Point x;
		Point y;

		public Move(Point x, Point y) {
			this.x = x;
			this.y = y;
		}
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
		AccumulatedWeightNodesVR accq = new AccumulatedWeightNodesVR(XR, nwm);
		AccumulatedWeightEdgesVR accW = new AccumulatedWeightEdgesVR(XR, awm);

		d = new IFunctionVR[K];// q on routes

		Point tk = XR.endPoint(1);// diem cuoi cung cua route thu k
		d[0] = new AccumulatedNodeWeightsOnPathVR(accq, tk);
		CS.post(new Leq(d[0], capacity));

		cost = new IFunctionVR[1];

		Point tk = XR.endPoint(0);
		cost[0] = new AccumulatedEdgeWeightsOnPathVR(accW, tk);

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
				System.out.println("Reach local optimum - toi uu cuc bo");
			break;
//			Move m = cand.get(R.nextInt(cand.size()));
//			mgr.performOnePointMove(m.x, m.y);
//			System.out.println("Step " + it + ", XR = " + XR.toString() + "violations = " + CS.violations()
//					+ ", cost = " + obj.getValue());
//			it++;
		}
	}



	public static void main(String[] args) {
		Vehicle A = new Vehicle();
		A.mapping();
		A.stateModel();
		A.search(1000);
	}
}