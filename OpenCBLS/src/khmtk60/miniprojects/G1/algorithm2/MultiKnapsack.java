package khmtk60.miniprojects.G1.algorithm2;

import java.io.FileReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import com.google.gson.Gson;

import khmtk60.miniprojects.G1.newlocalsearch.constraints.basic.CustomLessOrEqual;
import khmtk60.miniprojects.G1.newlocalsearch.constraints.basic.CustomLessOrEqualVarInt;
import khmtk60.miniprojects.G1.newlocalsearch.functions.conditionalsum.CustomConditionalSumVarInt;
import khmtk60.miniprojects.G1.newlocalsearch.model.CustomVarIntLS;
import khmtk60.miniprojects.G1.newlocalsearch.search.CustomOneVariableValueMove;
import khmtk60.miniprojects.G1.newlocalsearch.search.Move;
import khmtk60.miniprojects.G1.newlocalsearch.search.MoveType;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;

public abstract class MultiKnapsack {	
	Random rand = new Random(2018);
	CustomVarIntLS[] X;				//X[i]: bin has item i 
	CustomVarIntLS[] Y;				//Y[i]: num type in bin i 
	CustomVarIntLS[] Z;				//Z[i]: num class in bin i
	
	int[][] numTypePerBin;
	int[][] numClassPerBin;
	
	int M;						//num items
	int N;						//num bins
	int T;						//Num Type
	int R;						//Num Class
	
	//item attribute
	double[] w;
	double[] p;
	int[] t;
	int[] r;
	ArrayList<int[]> D;
	
	HashMap<Integer, Integer> r2idx;
	HashMap<Integer, Integer> t2idx;
	HashMap<CustomVarIntLS, Integer> varIntls2idx;
	HashSet<Integer> violationBins;
	
	//bin attribute
	double[] maxW;
	double[] minW;
	double[] maxP;
	int[] maxT;
	int[] maxR;	
	
	HashMap<Integer, Integer> oldBinId2NewId;
	HashMap<Integer, Integer> newBinId2OldId;
	HashMap<Integer, HashSet<Integer>> bin2Items;
	
	CustomConditionalSumVarInt[] sumWeight1;
	CustomConditionalSumVarInt[] sumWeight2;
	CustomLessOrEqual[] weight1LowerConstraints;
	CustomLessOrEqual[] weight1UpperConstraints;
	CustomLessOrEqual[] weight2UpperConstraints;
	CustomLessOrEqualVarInt[] typeConstraint;
	CustomLessOrEqualVarInt[] classConstraint;
	
	int realBinNumber;
	int realItemNumber;
	int numSatisItems;
	double _violations;
	
	public void stateModel(HashSet<Integer> blackList, HashSet<Integer> itemBlacklist) {
		realBinNumber = M - blackList.size();
		realItemNumber = N - itemBlacklist.size();
		oldBinId2NewId = Utils.getRemapBinId(M, blackList);
		newBinId2OldId = Utils.invertMap(oldBinId2NewId);
		violationBins = new HashSet<>();
		bin2Items = new HashMap<>();
		for (int i = 0; i < realBinNumber + 1; i++) {
			bin2Items.put(i, new HashSet<>());
		}
		System.out.println("Real bin number: " + realBinNumber);
		System.out.println("Real item number: " + realItemNumber);
		
		X = new CustomVarIntLS[N];
		varIntls2idx = new HashMap<>();
		for (int i = 0; i < N; i++) {
			HashSet<Integer> domain = new HashSet<>();
			if(itemBlacklist.contains(i)) {
				domain.add(oldBinId2NewId.get(M));
				X[i] = new CustomVarIntLS(domain);
				bin2Items.get(oldBinId2NewId.get(M)).add(i);
				continue;
			}
//			ArrayList<Integer> tmpArr = new ArrayList<>();
			for (Integer binId: D.get(i)) {
				if(blackList.contains(binId)) {
					continue;
				}
				domain.add(oldBinId2NewId.get(binId));
//				tmpArr.add(oldBinId2NewId.get(binId));
			}
			domain.add(oldBinId2NewId.get(M));
			X[i] = new CustomVarIntLS(domain);
			varIntls2idx.put(X[i], i);
			X[i].setValue(realBinNumber);
			bin2Items.get(realBinNumber).add(i);
		}
		
		numTypePerBin = new int[realBinNumber + 1][T];
		for (int i = 0; i < N; i++) {
			numTypePerBin[X[i].getValue()][t2idx.get(t[i])] += 1;
		}
		
		Y = new CustomVarIntLS[realBinNumber + 1];
		for (int i = 0; i < realBinNumber + 1; i++) {
			int initVal = 0;
			Y[i] = new CustomVarIntLS(0, T);
			for (int type = 0; type < T; type++) {
				initVal += Math.min(1, numTypePerBin[i][type]);
			}
			Y[i].setValue(initVal);
		}
		
		numClassPerBin = new int[realBinNumber + 1][R];
		for (int i = 0; i < N; i++) {
			numClassPerBin[X[i].getValue()][r2idx.get(r[i])] += 1;
		}
		
		Z = new CustomVarIntLS[realBinNumber + 1];
		for (int i = 0; i < realBinNumber + 1; i++) {
			int initVal = 0;
			Z[i] = new CustomVarIntLS(0, R);
			for (int c = 0; c < R; c++) {
				initVal += Math.min(1, numClassPerBin[i][c]);
			}
			Z[i].setValue(initVal);
		}
		
		sumWeight1 = new CustomConditionalSumVarInt[realBinNumber+1];
		weight1LowerConstraints = new CustomLessOrEqual[realBinNumber+1];
		weight1UpperConstraints = new CustomLessOrEqual[realBinNumber+1];
		for (int i = 0; i < realBinNumber+1; i++) {
			sumWeight1[i] = new CustomConditionalSumVarInt(X, w, i, varIntls2idx);
			weight1LowerConstraints[i] = new CustomLessOrEqual(minW[newBinId2OldId.get(i)], sumWeight1[i]);
			weight1UpperConstraints[i] = new CustomLessOrEqual(sumWeight1[i], maxW[newBinId2OldId.get(i)]);
		}
		
		sumWeight2 = new CustomConditionalSumVarInt[realBinNumber+1];
		weight2UpperConstraints = new CustomLessOrEqual[realBinNumber+1];
		for (int i = 0; i < realBinNumber+1; i++) {
			sumWeight2[i] = new CustomConditionalSumVarInt(X, p, i, varIntls2idx);
			weight2UpperConstraints[i] = new CustomLessOrEqual(sumWeight2[i], maxP[newBinId2OldId.get(i)]);
		}
		
		typeConstraint = new CustomLessOrEqualVarInt[realBinNumber + 1];
		for (int i = 0; i < realBinNumber + 1; i++) {
			typeConstraint[i] = new CustomLessOrEqualVarInt(Y[i], maxT[newBinId2OldId.get(i)]);
		}
		classConstraint = new CustomLessOrEqualVarInt[realBinNumber + 1];
		for (int i = 0; i < realBinNumber + 1; i++) {
			classConstraint[i] = new CustomLessOrEqualVarInt(Z[i], maxR[newBinId2OldId.get(i)]);
		}
		initPropagate();
	}
	
	public void initPropagate() {
		_violations = 0;
		for (int i = 0; i <realBinNumber + 1; i++) {
			sumWeight1[i].initPropagate();
			sumWeight2[i].initPropagate();
			
			weight1LowerConstraints[i].initPropagate();
			weight1UpperConstraints[i].initPropagate();
			weight2UpperConstraints[i].initPropagate();
			
			typeConstraint[i].initPropagate();
			classConstraint[i].initPropagate();
			
			if(sumWeight1[i].getValue() > 0) {
				_violations += weight1LowerConstraints[i].violations();
			}
			_violations += weight1UpperConstraints[i].violations();
			_violations += weight2UpperConstraints[i].violations();
			_violations += 100 * typeConstraint[i].violations();
			_violations += 100 * classConstraint[i].violations();
		}
		_violations += (bin2Items.get(realBinNumber).size() - N + realItemNumber);
		
		numSatisItems = N - bin2Items.get(realBinNumber).size();
		for (int bin : getVioBins()) {
			violationBins.add(bin);
			numSatisItems -= bin2Items.get(bin).size();
		}
	}
	
	public void propagateInt(int i) {
		int oldValue = X[i].getOldValue();
		int v = X[i].getValue();
		
		sumWeight1[oldValue].propagateInt(X[i], v);
		sumWeight2[oldValue].propagateInt(X[i], v);
		sumWeight1[v].propagateInt(X[i], v);
		sumWeight2[v].propagateInt(X[i], v);
		
		if(bin2Items.get(oldValue).size() == 0) {
			sumWeight1[oldValue].resetValue();
		}
		
		weight1LowerConstraints[oldValue].propagateInt(X[i], v);
		weight1UpperConstraints[oldValue].propagateInt(X[i], v);
		weight2UpperConstraints[oldValue].propagateInt(X[i], v);
		
		weight1LowerConstraints[v].propagateInt(X[i], v);
		weight1UpperConstraints[v].propagateInt(X[i], v);
		weight2UpperConstraints[v].propagateInt(X[i], v);
		
		typeConstraint[oldValue].propagateInt(X[i], v);
		classConstraint[oldValue].propagateInt(X[i], v);
		typeConstraint[v].propagateInt(X[i], v);
		classConstraint[v].propagateInt(X[i], v);
	}	
	
	public void makeOneMove(Move move) {
		if(move.getType() == MoveType.OneVariableValueAssignment) {
			CustomOneVariableValueMove m1 = (CustomOneVariableValueMove) move;
			CustomVarIntLS x = m1.getVariable();
			int old_v = x.getValue();
			int v = m1.getValue();	
			int id = m1.getId();
			if(old_v == v)
				return;
//			System.out.println("*** " + id + " " + old_v + " " + v + " " + oneMoveAssignDelta(id, v));
			
			numSatisItems += bin2Items.get(realBinNumber).size();
			if(violationBins.contains(old_v)) {
				numSatisItems += bin2Items.get(old_v).size();
			}
			if(violationBins.contains(v)) {
				numSatisItems += bin2Items.get(v).size();
			}
			
			_violations += oneMoveAssignDelta(id, v);
//			double delta = oneMoveAssignDelta(id, v);
			bin2Items.get(old_v).remove(id);
			bin2Items.get(v).add(id);
			
			x.setValuePropagate(v);								
			
			numTypePerBin[old_v][t2idx.get(t[id])] -= 1;
			numTypePerBin[v][t2idx.get(t[id])] += 1;

			if(numTypePerBin[old_v][t2idx.get(t[id])] == 0)
				Y[old_v].setValuePropagate(Y[old_v].getValue() - 1);
			if(numTypePerBin[v][t2idx.get(t[id])] == 1)
				Y[v].setValuePropagate(Y[v].getValue() + 1);
			
			numClassPerBin[old_v][r2idx.get(r[id])] -= 1;
			numClassPerBin[v][r2idx.get(r[id])] += 1;
			
			if(numClassPerBin[old_v][r2idx.get(r[id])] == 0)
				Z[old_v].setValuePropagate(Z[old_v].getValue() - 1);
			if(numClassPerBin[v][r2idx.get(r[id])] == 1)
				Z[v].setValuePropagate(Z[v].getValue() + 1);
			propagateInt(id);
			
			if(isViolationBin(old_v)) {
				violationBins.add(old_v);
				numSatisItems -= bin2Items.get(old_v).size();
			} else
				violationBins.remove(old_v);
			if(isViolationBin(v)) {
				violationBins.add(v);
				numSatisItems -= bin2Items.get(v).size();
			} else
				violationBins.remove(v);
			numSatisItems -= bin2Items.get(realBinNumber).size();
		} 
	}
	
	public double oneMoveAssignDelta(int i, int v) {
		double deltaX = 0;
		double deltaY = 0;
		double deltaZ = 0;
		
		int oldValue = X[i].getValue();
		
		if(v == oldValue)
			return 0;	
		
		if(oldValue == realBinNumber) {
			deltaX -= 1;//bin2Items.get(realBinNumber).size() == 1 ? 1 : 0;
		}else if(v == realBinNumber) {
			deltaX += 1;//bin2Items.get(realBinNumber).size() == 0 ? 1 : 0;
		}
		
		deltaX += weight1UpperConstraints[oldValue].getAssignDelta(X[i], v) + 
				  	weight2UpperConstraints[oldValue].getAssignDelta(X[i], v);
		// old bin
		double old_c1 = 1;
		double old_v1 = weight1LowerConstraints[oldValue].violations(); 
		
		double new_c1 = bin2Items.get(oldValue).size() > 1 ? 1 : 0;
		double new_v1 = new_c1 > 0 ? weight1LowerConstraints[oldValue].violations() + weight1LowerConstraints[oldValue].getAssignDelta(X[i], v) : 0;
		deltaX += new_v1 - old_v1;
		// new bin
		if(v < realBinNumber) {
			old_c1 = bin2Items.get(v).size() > 0 ? 1 : 0;
			old_v1 = old_c1 > 0 ? weight1LowerConstraints[v].violations() : 0;
			
			new_c1 = 1; 
			new_v1 = weight1LowerConstraints[v].violations() + weight1LowerConstraints[v].getAssignDelta(X[i], v);
			deltaX += weight1UpperConstraints[v].getAssignDelta(X[i], v) + weight2UpperConstraints[v].getAssignDelta(X[i], v);
			deltaX += new_v1 - old_v1;
		}
		if(numTypePerBin[oldValue][t2idx.get(t[i])] == 1) {
			deltaY += 100*typeConstraint[oldValue].getAssignDelta(Y[oldValue], Y[oldValue].getValue() - 1);
		}
		if(numTypePerBin[v][t2idx.get(t[i])] == 0) {
			deltaY += 100*typeConstraint[v].getAssignDelta(Y[v], Y[v].getValue() + 1);
		}
		
		if(numClassPerBin[oldValue][r2idx.get(r[i])] == 1) {
			deltaZ += 100*classConstraint[oldValue].getAssignDelta(Z[oldValue], Z[oldValue].getValue() - 1);
		}
		if(numClassPerBin[v][r2idx.get(r[i])] == 0) {
			deltaZ += 100*classConstraint[v].getAssignDelta(Z[v], Z[v].getValue() + 1);
		}
		
//		System.out.println("*** " + deltaZ + " " + deltaX + " " + deltaY);		
		return deltaX + deltaY + deltaZ;
	}
	
	public double replaceMoveAssignDelta(int i, int j) {
		double deltaX = 0;
		double deltaY = 0;
		double deltaZ = 0;
		
		double old_c1, old_v1, new_c1, new_v1;
		int v = X[j].getValue();
		// new bin
		if(v < realBinNumber) {
			old_v1 = weight1LowerConstraints[v].violations();
//			System.out.println(sumWeight1[v].getSwapDelta(X[i], X[j]));
			new_v1 = weight1LowerConstraints[v].violations() + weight1LowerConstraints[v].getSwapDelta(X[i], X[j]);
			deltaX += weight1UpperConstraints[v].getSwapDelta(X[i], X[j]) + weight2UpperConstraints[v].getSwapDelta(X[i], X[j]);
			deltaX += new_v1 - old_v1;
		}

		if(t2idx.get(t[i]) != t2idx.get(t[j])) {
			int deltaType = 0;
			if(numTypePerBin[v][t2idx.get(t[j])] == 1) {
				deltaType -= 1;
			}
			if(numTypePerBin[v][t2idx.get(t[i])] == 0) {
				deltaType += 1;
			}
			if(numTypePerBin[v][t2idx.get(t[i])] == 0) {
				deltaY += 100*typeConstraint[v].getAssignDelta(Y[v], Y[v].getValue() + deltaType);
			}
		}
		if(r2idx.get(r[i]) != r2idx.get(r[j])) {
			int deltaClass = 0;
			if(numClassPerBin[v][r2idx.get(r[j])] == 1) {
				deltaClass -= 1;
			}
			if(numClassPerBin[v][r2idx.get(r[i])] == 0) {
				deltaClass += 1;
			}
			if(numClassPerBin[v][r2idx.get(r[i])] == 0) {
				deltaZ += 100*classConstraint[v].getAssignDelta(Z[v], Z[v].getValue() + deltaClass);
			}
		}
		return deltaX + deltaY + deltaZ;
	}
	
	public void restartMaintainConstraint(int[][] tabu) {
		System.out.println("TabuSearch::restart.....");
		double delta;
		for (int i = 0; i < N; i++) {
			ArrayList<Move> candX = new ArrayList<>();
			for (int v: X[i].getDomain()) {
				if(v == X[i].getValue())
					continue;
				delta = oneMoveAssignDelta(i, v);
				if (delta <= 0) {
					candX.add(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], v, i));
				}
			}
			if(candX.size() == 0) {
				continue;
			}
			int idx = rand.nextInt(candX.size());
			makeOneMove(candX.get(idx));
		}
		for (int i = 0; i < tabu.length; i++) {
			for (int j = 0; j < tabu[i].length; j++)
				tabu[i][j] = -1;
		}
	}

	public void hardRestart(int[][] tabu) {
		ArrayList<Integer> cand = getVioBins();
		System.out.println("TabuSearch::hard restart.....");
		for (Integer binId : cand) {
			for (int i = 0; i < N; i++) {
				if(X[i].getValue() == binId) {
					makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], realBinNumber, i));
				}
			}
			for (int i = 0; i < tabu.length; i++) {
				for (int j = 0; j < tabu[i].length; j++)
					tabu[i][j] = -1;
			}
		}
	}
	
	public ArrayList<Integer> getVioBins() {
		double vio;
		ArrayList<Integer> cand = new ArrayList<>();
		for (int i = 0; i < realBinNumber; i++) {
			if(bin2Items.get(i).size() == 0)
				continue;
			vio = typeConstraint[i].violations(Y[i]) + classConstraint[i].violations(Z[i]);
			int id = newBinId2OldId.get(i);
			if(sumWeight1[i].getValue() < minW[id] || sumWeight1[i].getValue() > maxW[id] || sumWeight2[i].getValue() > maxP[id])
				vio += 1;
			if(vio > 0) {
				cand.add(i);
			}
		}
		return cand;
	}
	
	public boolean isViolationBin(int i) {
		if(bin2Items.get(i).size() == 0)
			return false;
		boolean result = false;
		double vio = typeConstraint[i].violations(Y[i]) + classConstraint[i].violations(Z[i]);
		int id = newBinId2OldId.get(i);
		if(sumWeight1[i].getValue() < minW[id] || sumWeight1[i].getValue() > maxW[id] || sumWeight2[i].getValue() > maxP[id])
			vio += 1;
		if(vio > 0) {
			result = true;
		}
		return result;
	}
	
	public int[] getSolution() {
		int[] result = new int[N];
		for (int i = 0; i < N; i++) {
			if(X[i].getValue() == realBinNumber)
				result[i] = -1;
			else
				result[i] = newBinId2OldId.get(X[i].getValue());
		}
		return result;
	}
	
	public void loadModel(String fn) {
		try{
			Gson gson = new Gson();
			Reader reader = new FileReader(fn);
			MinMaxTypeMultiKnapsackSolution I = gson.fromJson(reader, MinMaxTypeMultiKnapsackSolution.class);
			int[] x = I.getBinOfItem();
			for (int i = 0; i < N; i++) {
				int v = x[i];
				if(x[i] == -1)
					v = M;
				makeOneMove(new CustomOneVariableValueMove(MoveType.OneVariableValueAssignment, -1, X[i], oldBinId2NewId.get(v), i));
			}			
			System.out.println(_violations + " -- " + (N - bin2Items.get(realBinNumber).size()));
		}catch(Exception ex){
			ex.printStackTrace();
		}		
	}
	
	public void writeResultToFile(String fn) {
		int[] sol = getSolution();
		MinMaxTypeMultiKnapsackSolution solution = new MinMaxTypeMultiKnapsackSolution();
		solution.setBinOfItem(sol);
		try{
			PrintWriter out = new PrintWriter(fn);
			Gson gson = new Gson();
			String json = gson.toJson(solution);
			out.print(json);
			out.close();
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public void readData(String path) {
		MinMaxTypeMultiKnapsackInput tmp = new MinMaxTypeMultiKnapsackInput();
		MinMaxTypeMultiKnapsackInput inputs = tmp.loadFromFile(path);
		MinMaxTypeMultiKnapsackInputBin[] bins = inputs.getBins();
		MinMaxTypeMultiKnapsackInputItem[] items = inputs.getItems();
		
		N = items.length;
		M = bins.length;
		
		w = new double[N];
		p = new double[N];
		t = new int[N];
		r = new int[N];
		D = new ArrayList<>();
		for (int i = 0; i < N; i++) {
			w[i] = items[i].getW();
			p[i] = items[i].getP();
			t[i] = items[i].getT();
			r[i] = items[i].getR();
			D.add(items[i].getBinIndices());
		}
		
		t2idx = new HashMap<>();
		r2idx = new HashMap<>();
		HashSet<Integer> tSet = new HashSet<>();
		HashSet<Integer> rSet = new HashSet<>();
		for (int i = 0; i < N; i++) {
			tSet.add(t[i]);
			rSet.add(r[i]);
		}
		
		int k = 0;
		for (Integer integer : rSet) {
			r2idx.put(integer, k++);
		}
		
		k = 0;
		for (Integer integer : tSet) {
			t2idx.put(integer, k++);
		}
		
		T = t2idx.size();
		R = r2idx.size();
		
		maxW = new double[M+1];
		minW = new double[M+1];
		maxP = new double[M+1];
		maxT = new int[M+1];
		maxR = new int[M+1];
		for (int i = 0; i < M; i++) {
			 maxW[i] = bins[i].getCapacity();
			 minW[i] = bins[i].getMinLoad();
			 maxP[i] = bins[i].getP();
			 maxT[i] = bins[i].getT();
			 maxR[i] = bins[i].getR();
		}
		maxW[M] = 1e8;
		minW[M] = 0;
		maxP[M] = 1e8;
		maxT[M] = T;
		maxR[M] = R;
	}
}
