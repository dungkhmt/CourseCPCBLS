package planningoptimization115657k62.levanlinh.vehicleroutingproblem;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class GiaoHangTuKhoTT {
	int K = 2;// number of routes
    int N = 6;// number of clients
    int capacity = 11;
    int[] demand = {0,4,2,5,2,3,5};
    int[][] c = {
            {0,3,2,1,4,3,7},
            {2,0,2,3,5,3,9},
            {1,3,0,2,4,2,4},
            {5,3,2,0,1,1,7},
            {3,1,5,1,0,3,6},
            {6,3,2,4,4,0,9},
            {2,3,2,1,2,8,0}
    };
	/*
	int N = 11;
	int[] w = {1,3,5,7,6,4,2,4,2,6,1};
	int K = 3;
	int[] c = {10, 15, 20};
	int[][] d = { { 0, 29, 20, 21, 16, 31, 100, 12, 4, 31, 18 }, { 29, 0, 15, 29, 28, 40, 72, 21, 29, 41, 12 },
			{ 20, 15, 0, 15, 14, 25, 81, 9, 23, 27, 13 }, { 21, 29, 15, 0, 4, 12, 92, 12, 25, 13, 25 },
			{ 16, 28, 14, 4, 0, 16, 94, 9, 20, 16, 22 }, { 31, 40, 25, 12, 16, 0, 95, 24, 36, 3, 37 },
			{ 100, 72, 81, 92, 94, 95, 0, 90, 101, 99, 84 }, { 12, 21, 9, 12, 9, 24, 90, 0, 15, 25, 13 },
			{ 4, 29, 23, 25, 20, 36, 101, 15, 0, 35, 18 }, { 31, 41, 27, 13, 16, 3, 99, 25, 35, 0, 38 },
			{ 18, 12, 13, 25, 22, 37, 84, 13, 18, 38, 0 } }; */
	IntVar[] X, IR, L, W;
	Model model;
	const int INF = Integer.MAX_VALUE;
	
	void buildModel() {
		model = new Model("Routing");
		X = model.intVarArray("X", N + 2*K, 0, N+2*K);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
