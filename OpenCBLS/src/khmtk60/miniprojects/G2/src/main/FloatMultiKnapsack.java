package main;

import java.awt.List;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Random;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import localsearch.constraints.basic.FloatIsEqual;
import localsearch.constraints.basic.LessOrEqualFloat;
import localsearch.functions.conditionalsum.FloatConditionalSum;
import localsearch.functions.conditionalsum.FloatSum2;
import localsearch.model.FloatConstraintSystem;
import localsearch.model.FloatLocalSearchManager;
import localsearch.model.IFloatConstraint;
import localsearch.model.IFloatFunction;
import localsearch.model.VarIntLS;
import search.MultiStageGreedySearch;
import search.TabuSearch;

public class FloatMultiKnapsack {
	class AssignMove {
		int i;
		int v;

		public AssignMove(int i, int v) {
			this.i = i;
			this.v = v;
		}
	}

	class SwapMove {
		int i;
		int j1;
		int j2;

		public SwapMove(int i, int j1, int j2) {
			this.i = i;
			this.j1 = j1;
			this.j2 = j2;
		}
	}

	FloatLocalSearchManager mgr;
	FloatConstraintSystem S;
	public int n;// Number of item
	public int m;// Number of bin
	public int classes;// Number of class
	public int types;// Number of type

	public float[] w;// weight1 of item
	public float[] p;// weight2 of item
	public int[] t;// type of item
	public int[] r;// class of item

	public int[] t2;// type of item
	public int[] r2;// class of item

	public HashMap<Integer, Integer> mapt;
	public HashMap<Integer, Integer> mapr;

	public int[][] D;// domain of item

	public float[] W;// capacity 1
	public float[] LW;// minimum capacity
	public float[] P;// capacity 2
	public int[] T;// max type of item
	public int[] R;// max class of item

	public ArrayList<ArrayList<Integer>> binIndices;

	VarIntLS[][] X; // Mang 2 chieu Bin x item
	int[][] Z; // Mang 2 chieu luu tru cap bin - item ko duoc phep xep

	IFloatFunction[] sum1;
	IFloatFunction[] sum2;
	IFloatFunction[] sum3;
	IFloatFunction[] sum4;

	IFloatFunction[] test1;

	public void initializeX() {
		float[] w_bin_current = new float[m];
		float[] p_bin_current = new float[m];
		float[] t_bin_current = new float[m];
		float[] r_bin_current = new float[m];

		ArrayList<Set> typeBin = new ArrayList<>();
		ArrayList<Set> classBin = new ArrayList<>();

		Random random = new Random();

		for (int i = 0; i < m; i++) {
			Set<Integer> set_type = new HashSet<>();
			Set<Integer> set_class = new HashSet<>();
			typeBin.add(set_type);
			classBin.add(set_class);
		}

		for (int i = 0; i < n; i++) {
			int[] db = D[i];
			for (int j = 0; j < db.length; j++) {
				if (db[j] == 1) {
					if (w_bin_current[j] + w[i] <= W[j] && p_bin_current[j] + p[i] <= P[j]) {
						if ((t_bin_current[j] > T[j]) || (r_bin_current[j] > R[j]))
							continue;
						else if ((!classBin.get(j).contains(r[i]) && (r_bin_current[j] == R[j])))
							continue;
						else if ((!typeBin.get(j).contains(t[i]) && (t_bin_current[j] == T[j])))
							continue;
						else {
							if (typeBin.get(j).contains(t[i])) {
								X[i][j].setValue(1);
								w_bin_current[j] += w[i];
								p_bin_current[j] += p[i];
								if (!classBin.get(j).contains(r[i])) {
									r_bin_current[j] += 1;
									classBin.get(j).add(r[i]);
									break;
								}
								break;
							} else {
								X[i][j].setValue(1);
								w_bin_current[j] += w[i];
								p_bin_current[j] += p[i];
								if (!classBin.get(j).contains(r[i])) {
									typeBin.get(j).add(r[i]);
									r_bin_current[j] += 1;
									classBin.get(j).add(r[i]);
									break;
								}
								break;
							}

						}
					}
				}
			}
		}
		typeBin.clear();
		classBin.clear();
	}

	public void readDataJson(String fn) {
		JSONParser jsonParser = new JSONParser();
		try (FileReader reader = new FileReader(fn)) {
			Object obj = jsonParser.parse(reader);
			JSONObject jo = (JSONObject) obj;
			JSONArray bins = (JSONArray) jo.get("bins");
			JSONArray items = (JSONArray) jo.get("items");

			n = items.size();
			m = bins.size();

			w = new float[n];
			p = new float[n];
			t = new int[n];
			r = new int[n];

			t2 = new int[n];
			r2 = new int[n];

			D = new int[n][m];
			binIndices = new ArrayList<ArrayList<Integer>>();

			for (int i = 0; i < n; i++) {
				for (int j = 0; j < m; j++) {
					D[i][j] = 0;
				}
			}

			for (int i = 0; i < items.size(); i++) {
				JSONObject item = (JSONObject) items.get(i);
				w[i] = (float) (double) item.get("w");
				p[i] = (float) (double) item.get("p");
				t[i] = (int) (long) item.get("t");
				r[i] = (int) (long) item.get("r");
				JSONArray binArray = (JSONArray) item.get("binIndices");
				for (int j = 0; j < binArray.size(); j++) {
					D[i][(int) (long) (binArray.get(j))] = 1;
				}
			}

			ArrayList<Integer> type = new ArrayList<Integer>();
			for (int i = 0; i < n; i++) {
				if (type.contains(t[i])) {
					continue;
				} else {
					type.add(t[i]);
				}
			}
			types = type.size();
			ArrayList<Integer> cl = new ArrayList<Integer>();
			for (int i = 0; i < n; i++) {
				if (cl.contains(r[i])) {
					continue;
				} else {
					cl.add(r[i]);
				}
			}
			classes = cl.size();

			mapr = new HashMap<Integer, Integer>();
			mapt = new HashMap<Integer, Integer>();

			for (int i = 0; i < types; i++) {
				mapt.put(type.get(i), i);
			}
			for (int i = 0; i < classes; i++) {
				mapr.put(cl.get(i), i);
			}

			for (int i = 0; i < n; i++) {
				t2[i] = mapt.get(t[i]);
			}

			for (int i = 0; i < n; i++) {
				r2[i] = mapr.get(r[i]);
			}
			cl.clear();
			type.clear();
			mapt.clear();
			mapr.clear();

			W = new float[m];
			LW = new float[m];
			P = new float[m];
			T = new int[m];
			R = new int[m];
			for (int j = 0; j < bins.size(); j++) {
				JSONObject bin = (JSONObject) bins.get(j);
				W[j] = (float) (double) bin.get("capacity");
				LW[j] = (float) (double) bin.get("minLoad");
				P[j] = (float) (double) bin.get("p");
				T[j] = (int) (long) bin.get("t");
				R[j] = (int) (long) bin.get("r");
			}

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void initX(ArrayList<Integer> bins, ArrayList<Integer> items) {

		Random random = new Random();
		int m1 = bins.size();
		int n1 = items.size();
		float[] bin_w = new float[m1];
		int[] bin_idx = new int[m1];

		for (int i = 0; i < m1; i++) {
			bin_idx[i] = bins.get(i);
			bin_w[i] = W[bins.get(i)];
		}

		for (int i = 0; i < m1 - 1; i++) {
			for (int j = i + 1; j < m1; j++) {
				if (bin_w[i] > bin_w[j]) {
					int temp = bin_idx[i];
					bin_idx[i] = bin_idx[j];
					bin_idx[j] = temp;
					Swap(bin_w, i, j);
				}
			}
		}

		ArrayList<Integer> checkItem = new ArrayList<>();
		for (int i = 0; i < m1; i++) {
			ArrayList<Integer> IteminBin = new ArrayList<>();
			Set<Integer> sum_types = new HashSet<>();
			Set<Integer> sum_classes = new HashSet<>();
			int sum_w1 = 0;
			int sum_p1 = 0;

			for (int j = 0; j < n1; j++) {
				if (D[items.get(j)][bin_idx[i]] == 1) {
					IteminBin.add(items.get(j));
				}
			}

			int max_t = Integer.MIN_VALUE;
			int best = -1;
			for (int k = 0; k < types; k++) {
				int dem = 0;
				for (int l : IteminBin) {
					if (r2[l] == k)
						dem++;
				}
				if (dem > max_t) {
					max_t = dem;
					best = k;
				}
			}
			ArrayList<Integer> ar = new ArrayList<>();
			for (int q : IteminBin) {
				if (r2[q] == best)
					ar.add(q);
			}
			do {
				int r = random.nextInt(ar.size());
				int item = ar.remove(r);
				if (checkItem.contains(item)) {
					continue;
				} else {
					X[item][bin_idx[i]].setValue(1);
					checkItem.add(item);
					sum_w1 += w[item];
					sum_p1 += p[item];
					sum_types.add(t2[item]);
					sum_classes.add(r2[item]);
				}
			} while (sum_p1 < P[i] && sum_types.size() < T[i] && sum_classes.size() <= R[i] && ar.size() != 0
					&& sum_w1 <= W[i]);
		}

	}

	public void stateModel() {
		mgr = new FloatLocalSearchManager();
		S = new FloatConstraintSystem(mgr);
		X = new VarIntLS[n][m];

		for (int i = 0; i < n; i++) {
			ArrayList<Integer> index = new ArrayList<Integer>();
			int[] db = D[i];
			for (int j = 0; j < m; j++) {
				if (db[j] != 0) {
					index.add(j);
					X[i][j] = new VarIntLS(mgr, 0, 1);
				} else {
					X[i][j] = new VarIntLS(mgr, 0, 0);
				}
			}
		}

		initializeX();
		
		// LW < tong trong so 1 < W
		sum1 = new IFloatFunction[m];
		for (int i = 0; i < m; i++) {
			VarIntLS[] XI = new VarIntLS[n];
			for (int j = 0; j < n; j++) {
				XI[j] = X[j][i];
			}
			sum1[i] = new FloatConditionalSum(XI, w, 1);
			S.post(new LessOrEqualFloat(sum1[i], W[i]));
			S.post(new LessOrEqualFloat(LW[i], sum1[i]));
		}

		// tong trong so 2 < p

		sum2 = new IFloatFunction[m];
		for (int i = 0; i < m; i++) {
			VarIntLS[] XI = new VarIntLS[n];
			for (int j = 0; j < n; j++) {
				XI[j] = X[j][i];
			}
			sum2[i] = new FloatConditionalSum(XI, p, 1);
			S.post(new LessOrEqualFloat(sum2[i], P[i]));
		}

		test1 = new IFloatFunction[n];
		for (int i = 0; i < n; i++) {
			test1[i] = new FloatConditionalSum(X[i], 1);
			S.post(new FloatIsEqual(test1[i], 1));
		}

		sum3 = new IFloatFunction[m];
		sum4 = new IFloatFunction[m];

		for (int i = 0; i < m; i++) {
			VarIntLS[] y_ = new VarIntLS[n];
			for (int j = 0; j < n; j++) {
				y_[j] = X[j][i];
			}
			S.post(new LessOrEqualFloat(new FloatSum2(y_, t2, 1, types), T[i]));
			S.post(new LessOrEqualFloat(new FloatSum2(y_, r2, 1, classes), R[i]));
		}

		// Mot item chi duoc xep vao 1 bin
		test1 = new IFloatFunction[n];
		for (int i = 0; i < n; i++) {
			test1[i] = new FloatConditionalSum(X[i], 1);
			S.post(new FloatIsEqual(test1[i], 1));
		}

		mgr.close();
	}

	public static void Swap(float[] a, int i, int j) {
		float temp = a[i];
		a[i] = a[j];
		a[j] = temp;
	}

	public void Swapping(IFloatConstraint c, int maxIter, ArrayList<Integer> rs1, ArrayList<Integer> rs2) {
		ArrayList<SwapMove> cand = new ArrayList<FloatMultiKnapsack.SwapMove>();
		int it = 0;
		Random random = new Random();
		while (it < maxIter && S.violations() > 0) {
			cand.clear();
			float deltaswap = Float.MAX_VALUE;
			for (int i = 0; i < rs2.size(); i++) {
				for (int j1 = 0; j1 < rs1.size(); j1++) {
					if (X[rs2.get(i)][rs1.get(j1)].getValue() == 1) {
						for (int j2 = 0; j2 < rs1.size(); j2++) {
							float delta = S.getSwapDelta(X[rs2.get(i)][rs1.get(j1)], X[rs2.get(i)][rs1.get(j2)]);
							if (delta < deltaswap) {
								cand.clear();
								cand.add(new SwapMove(rs2.get(i), rs1.get(j1), rs1.get(j2)));
								deltaswap = delta;
							} else if (delta == deltaswap) {
								cand.add(new SwapMove(rs2.get(i), rs1.get(j1), rs1.get(j2)));
							}
						}
						break;
					}

				}
			}

			SwapMove m = cand.get(random.nextInt(cand.size()));
			X[m.i][m.j1].swapValuePropagate(X[m.i][m.j2]);
			System.out.println("Step swap " + it + ", S = " + S.violations());
			it++;
			ArrayList<ArrayList<Integer>> result_current = computeResult();
		}

	}

	public void SwapSearch(IFloatConstraint c, int maxIter, ArrayList<Integer> rs1, ArrayList<Integer> rs2) {
		// rs1 binNotSave rs2 itemNotSave
		ArrayList<SwapMove> cand = new ArrayList<FloatMultiKnapsack.SwapMove>();
		int it = 0;
		ArrayList<AssignMove> cand2 = new ArrayList<AssignMove>();
		Random R = new Random();
		while (it < maxIter && S.violations() > 0) {
			cand.clear();
			// float minDelta = Float.MAX_VALUE;
			float deltaswap = Float.MAX_VALUE;
			for (int i = 0; i < rs2.size(); i++) {
				for (int j1 = 0; j1 < rs1.size(); j1++) {
					if (X[rs2.get(i)][rs1.get(j1)].getValue() == 1) {
						for (int j2 = 0; j2 < rs1.size(); j2++) {
							float delta = S.getSwapDelta(X[rs2.get(i)][rs1.get(j1)], X[rs2.get(i)][rs1.get(j2)]);
							if (delta < deltaswap) {
								cand.clear();
								cand.add(new SwapMove(rs2.get(i), rs1.get(j1), rs1.get(j2)));
								deltaswap = delta;
							} else if (delta == deltaswap) {
								cand.add(new SwapMove(rs2.get(i), rs1.get(j1), rs1.get(j2)));
							}
						}
						break;
					}

				}
			}

			VarIntLS[] y = c.getVariables();

			Random R2 = new Random();
			cand2.clear();
			float deltaassign = Float.MAX_VALUE;
			for (int i = 0; i < rs2.size(); i++) {
				for (int v = y[rs2.get(i)].getMinValue(); v <= y[rs2.get(i)].getMaxValue(); v++) {
					float d = c.getAssignDelta(y[rs2.get(i)], v);
					if (d < deltaassign) {
						cand2.clear();
						cand2.add(new AssignMove(rs2.get(i), v));
						deltaassign = d;
					} else if (d == deltaassign) {
						cand2.add(new AssignMove(rs2.get(i), v));
					}
				}
			}
			if (deltaassign > deltaswap) {
				SwapMove m = cand.get(R.nextInt(cand.size()));
				X[m.i][m.j1].swapValuePropagate(X[m.i][m.j2]);
				System.out.println("Step swap " + it + ", S = " + S.violations());
				it++;
			} else {
				int idx = R.nextInt(cand2.size());
				AssignMove m = cand2.get(idx);
				y[m.i].setValuePropagate(m.v);
				System.out.println("Step assign " + it + ", S = " + S.violations());
				it++;
			}
			ArrayList<ArrayList<Integer>> result_current = computeResult();
		}

	}

	public ArrayList<ArrayList<Integer>> computeResult() {
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();

		ArrayList<Integer> itemNotSave = new ArrayList<Integer>();
		ArrayList<Integer> binNotSatis = new ArrayList<Integer>();
		ArrayList<Integer> listBin = new ArrayList<>();
		ArrayList<Integer> itemSave = new ArrayList<Integer>();

		for (int i = 0; i < m; i++) {
			VarIntLS[] y = new VarIntLS[n];
			float sum_w = 0;
			float sum_p = 0;
			float sum_t = 0;
			float sum_r = 0;
			for (int j = 0; j < n; j++) {
				y[j] = X[j][i];
			}
			Set<Integer> ty = new HashSet<>();
			Set<Integer> cl = new HashSet<>();
			for (int k = 0; k < y.length; k++) {
				int value = y[k].getValue();
				if (value == 1) {
					sum_w += w[k];
					sum_p += p[k];
					ty.add(t[k]);
					cl.add(r[k]);
					sum_t = ty.size();
					sum_r = cl.size();
				}
			}

			if (sum_w <= W[i] && sum_w >= LW[i] && sum_p <= P[i] && sum_t <= T[i] && sum_r <= R[i]) {
				listBin.add(i);
			} else {
				binNotSatis.add(i);
			}

		}
		int sum = 0;
		for (int i = 0; i < listBin.size(); i++) {
			for (int j = 0; j < n; j++) {
				if (X[j][listBin.get(i)].getValue() == 1) {
					sum++;
					itemSave.add(j);
				}
			}
		}
		for (int i = 0; i < n; i++) {
			if (!itemSave.contains(i))
				itemNotSave.add(i);
		}
		System.out.println("So Item xep dung : " + sum);
		result.add(binNotSatis);
		result.add(itemNotSave);
		result.add(listBin);
		result.add(itemSave);
		return result;
	}

	public void solve() {
		stateModel();
		ArrayList<ArrayList<Integer>> rs = computeResult();
		
		// TabuSearch b = new TabuSearch();
		// b.search(S, 80, 20, 100, 100);
		// MultiStageGreedySearch s = new MultiStageGreedySearch();
		// s.search(S, 80, 200, true);
//		 Swap(S, 50, rs.get(0), rs.get(1));

		Swapping(S, 10, rs.get(0), rs.get(1));
		ArrayList<ArrayList<Integer>> result = new ArrayList<ArrayList<Integer>>();
		for(int i = 0; i < 100; i++) {
			System.out.println("--------------------");
			result = computeResult();
			Swapping(S, 100, result.get(0), result.get(1));
			JSONObject json = new JSONObject();
			for (int i1 : result.get(3)) {
				for (int j : result.get(2)) {
					if (X[i1][j].getValue() == 1) {
						json.put(i1, j);
					}
				}
			}
			for (int i1 : result.get(1)) {
				json.put(i1, -1);
			}
			try (FileWriter file = new FileWriter("rs100.json")) {
				file.write(json.toJSONString());
				file.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		ArrayList<ArrayList<Integer>> result1 = computeResult();
		initX(result1.get(0), result1.get(1));
		ArrayList<ArrayList<Integer>> result2 = computeResult();
	}

	public static void main(String args[]) {
		FloatMultiKnapsack mtks = new FloatMultiKnapsack();

		mtks.readDataJson("data/test100.json");

		mtks.solve();

	}
}
