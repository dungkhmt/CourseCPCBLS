package solution;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;



public class MultiKnapsack {

    public int n;
    public int m;
    public int mt;
    public int mr;
//    public int[] w_item = new int[]{3,2,1,6,4,7,2,4,3,3,2,5,4,1,3,2};
//    public int[] p_item = new int[]{1,0,0,1,1,0,1,2,0,0,1,1,1,0,0,2};
//    public int[] t_item = new int[]{0,1,0,1,1,2,0,0,1,2,0,1,1,0,0,2};
//    public int[] r_item = new int[]{1,0,1,0,1,2,2,0,1,2,2,1,1,0,0,2};
//    public int[] maxW = new int[]{22,17,18};
//    public int[] maxP = new int[]{5,3,6};
//    public int[] maxT = new int[]{2,2,2};
//    public int[] maxR = new int[]{1,1,1};
//    public int[][] D = new int[][]{{0,1},{1,2},{0,2},{1,2},{0,1},{0,2},{1,2},{1,2},{0,2},{0,2},{1,2},
//            {0,1}, {0,2}, {1,2}, {0,1}, {0,2}};

    public double[] p,w;
    public int[] t, r;
    public ArrayList<ArrayList<Integer>> binIndices;

    public double[] W;//capacity 1
    public double[] LW;//minimum capacity
    public double[] P;//capacity 2
    public int[] T;//max type of item
    public int[] R;//max class of item

    public Set<Integer> convertArrayToSet(int array[]) {

        // Create an empty Set
        Set<Integer> set = new HashSet<>();

        // Iterate through the array
        for (int t : array) {
            // Add each element into the set
            set.add(t);
        }
        // Return the converted Set
        return set;
    }

    public void readDataJson(String fn) {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader(fn)) {
            Object obj = jsonParser.parse(reader);
            JSONObject jo = (JSONObject) obj;
            JSONArray bins = (JSONArray)jo.get("bins");
            JSONArray items = (JSONArray)jo.get("items");

            n = items.size();
            m = bins.size();

            w = new double[n];
            p = new double[n];
            t = new int[n];
            r = new int[n];
            binIndices = new ArrayList<ArrayList<Integer>>();

            for(int i = 0; i < items.size(); i++) {
                JSONObject item = (JSONObject)items.get(i);
                w[i] = (double)item.get("w");
                p[i] = (double)item.get("p");
                t[i] = (int)(long)item.get("t");
                r[i] = (int)(long)item.get("r");
                JSONArray binArray = (JSONArray)item.get("binIndices");
                ArrayList<Integer> idx = new ArrayList<Integer>();
                for(int j = 0; j < binArray.size(); j++) {
                    idx.add((int)(long)(binArray.get(j)));
                }
                binIndices.add(idx);
            }

            W = new double[m];
            LW = new double[m];
            P = new double[m];
            T = new int[m];
            R = new int[m];
            for(int j = 0; j < bins.size(); j++) {
                JSONObject bin = (JSONObject)bins.get(j);
                W[j] = (double)bin.get("capacity");
                LW[j] = (double)bin.get("minLoad");
                P[j] = (double)bin.get("p");
                T[j] = (int)(long)bin.get("t");
                R[j] = (int)(long)bin.get("r");
            }

            Set<Integer> setT = convertArrayToSet(t);
            Set<Integer> setR = convertArrayToSet(r);
            mt = setT.size();
            mr = setR.size();
            for (int i = 0; i < t.length; i++){
                t[i] = new ArrayList<>(setT).indexOf(t[i]);
            }

            for (int i = 0; i < r.length; i++){
                r[i] = new ArrayList<>(setR).indexOf(r[i]);
//                System.out.println(r[i]);
            }

        } catch (Exception e) {
            System.out.println(e);
        }
    }



    public static void main(String[] args) {
    	System.out.println("G9:");
    	System.out.println("Pham Ngoc Quang - MSSV: 20152980");
    	System.out.println("Nguyen Huu Hai - MSSV: 20151197");
    	System.out.println();
    	
        MultiKnapsack m = new MultiKnapsack();
        m.readDataJson("data/MinMaxTypeMultiKnapsackInput-1000.json");
        System.out.println("So item: " + m.n);
        
        double sumW = 0;
        double sumMin = 0;
        for (int i=0; i<m.n; i++) {
        	sumW += m.w[i];
        }
        System.out.println("Tong weight: " + sumW);
        for (int i=0; i<m.LW.length; i++) {
        	sumMin += m.LW[i];
        }
        System.out.println("Tong minLoad: " + sumMin);
        System.out.println();
        
        
//        int count = 0;
//        
//        Individual a = new Individual("heuristic", m.m, m.n, m.mt, m.mr, m.W, m.LW, m.P, m.T, m.R, m.binIndices, m.w, m.p, m.t, m.r);
//        for (int i=0; i<m.n; i++) {
//        	try {
//        		System.out.println(i+ " la: " + a.Indiv[i].bin);
//			} catch (Exception e) {
//				// TODO: handle exception
//				count++;
//			}
//        }
//        ArrayList<Integer> listBin = new ArrayList<Integer>();
//        for (int i=0; i<m.m; i++) {
//        	if (a.sumWeight[i] <= m.W[i] && a.sumWeight[i] >= m.LW[i] && a.sumPrice[i] <= m.P[i] && a.sumType[i] <= m.T[i] && a.sumRank[i] <= m.R[i]) {
//    			listBin.add(i);
//        	}
//        }
//        for (int b=0; b<listBin.size(); b++) {
//			for (int i=0; i<m.n; i++) {
//				if (a.Indiv[i].bin == listBin.get(b)) {
//					count++;
//				}
//			}
//		}
//        System.out.println(a.violations);
//        System.out.println(count);
        
        int numIter = 500;
        ArrayList<Double> savedViols = new ArrayList<Double>();
        
        Population list = new Population(m.m, m.n, m.mt, m.mr, m.W, m.LW, m.P, m.T, m.R, m.binIndices, m.w, m.p, m.t, m.r);
        
        for (int i=0; i<numIter; i++) {
        	list.Hybridization(m.m, m.n, m.mt, m.mr, m.W, m.LW, m.P, m.T, m.R, m.binIndices, m.w, m.p, m.t, m.r);
        	savedViols.add(list.Popu.get(0).violations);
        	System.out.println("Step " + (i+1) + ", Violations = " + list.Popu.get(0).violations);
        	
        	if ((i>=30) && (savedViols.get(i-30) - savedViols.get(i) < Math.pow(10, -10))) {
        		break;
        	}
        }
        list.printInfo(m.m, m.n, m.mt, m.mr, m.W, m.LW, m.P, m.T, m.R, m.binIndices, m.w, m.p, m.t, m.r);
    }
}
