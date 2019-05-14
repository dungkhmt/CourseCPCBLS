package khmtk60.miniprojects.G8;

import java.util.HashSet;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackSolution;

public class Checker {
	private boolean[] checkBins;
	
	public boolean[] getCheckBins() {
		return checkBins;
	}
	
	public int violations(MinMaxTypeMultiKnapsackInput I, MinMaxTypeMultiKnapsackSolution S){
		int nbBins = I.getBins().length;
		double[] loadWeight = new double[nbBins];
		double[] loadP = new double[nbBins];
		HashSet<Integer>[] loadType = new HashSet[nbBins];
		HashSet<Integer>[] loadClass = new HashSet[nbBins];
		
		for(int b = 0; b < nbBins; b++){
			loadWeight[b] = 0;
			loadP[b] = 0;
			loadType[b] = new HashSet<Integer>();
			loadClass[b] = new HashSet<Integer>();
		}
		int nbItemNotScheduled = 0;
		int violations = 0;
		int[] X = S.getBinOfItem();
		
		MinMaxTypeMultiKnapsackInputItem[] items = I.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bins = I.getBins();
		
		for(int i = 0; i < X.length; i++){
			if(X[i] < 0 || X[i] >= nbBins){
				nbItemNotScheduled++;
			}else{
				int b = X[i];
				loadWeight[b] += items[i].getW();
				loadP[b] += items[i].getP();
				loadType[b].add(items[i].getT());
				loadClass[b].add(items[i].getR());
				
				boolean ok = false;
				for(int j = 0; j < items[i].getBinIndices().length; j++){
					if(b == items[i].getBinIndices()[j]){
						ok = true; break;
					}
				}
				if(!ok){
					violations++;
				}
			}
		}
		for(int b = 0; b < nbBins; b++){
			if(loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity()))
				violations++;
//			if(loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad())
//				violations++;
			if(loadP[b] > bins[b].getP())
				violations++;
			if(loadType[b].size() > bins[b].getT())
				violations++;
			if(loadClass[b].size() > bins[b].getR())
				violations++;
		}
				
		return violations;
	}
	
	public boolean[] checkBins(MinMaxTypeMultiKnapsackInput I, MinMaxTypeMultiKnapsackSolution S) {
		int nbBins = I.getBins().length;
		double[] loadWeight = new double[nbBins];
		double[] loadP = new double[nbBins];
		HashSet<Integer>[] loadType = new HashSet[nbBins];
		HashSet<Integer>[] loadClass = new HashSet[nbBins];
		
		checkBins = new boolean[nbBins];
		for(int i = 0; i < nbBins; i++)
			checkBins[i] = true;
		
		int[] violationsOfBin = new int[nbBins];
		for(int i = 0; i < nbBins; i++)
			violationsOfBin[i] = 0;
		
		for(int b = 0; b < nbBins; b++){
			loadWeight[b] = 0;
			loadP[b] = 0;
			loadType[b] = new HashSet<Integer>();
			loadClass[b] = new HashSet<Integer>();
		}
		int nbItemNotScheduled = 0;
		int[] X = S.getBinOfItem();
		
		MinMaxTypeMultiKnapsackInputItem[] items = I.getItems();
		MinMaxTypeMultiKnapsackInputBin[] bins = I.getBins();
		
		for(int i = 0; i < X.length; i++){
			if(X[i] < 0 || X[i] >= nbBins){
				nbItemNotScheduled++;
			}else{
				int b = X[i];
				loadWeight[b] += items[i].getW();
				loadP[b] += items[i].getP();
				loadType[b].add(items[i].getT());
				loadClass[b].add(items[i].getR());
				
				boolean ok = false;
				for(int j = 0; j < items[i].getBinIndices().length; j++){
					if(b == items[i].getBinIndices()[j]){
						ok = true; break;
					}
				}
				if(!ok){
					violationsOfBin[X[i]]++;
				}
			}
		}
		for(int b = 0; b < nbBins; b++){
			if(loadWeight[b] > 0 && (loadWeight[b] > bins[b].getCapacity()))
				violationsOfBin[b]++;
			if(loadWeight[b] > 0 && loadWeight[b] < bins[b].getMinLoad())
				violationsOfBin[b]++;
			if(loadP[b] > bins[b].getP())
				violationsOfBin[b]++;
			if(loadType[b].size() > bins[b].getT())
				violationsOfBin[b]++;
			if(loadClass[b].size() > bins[b].getR())
				violationsOfBin[b]++;
		}
		
		for(int i = 0; i < nbBins; i++)
			if(violationsOfBin[i] > 0)
				checkBins[i] = false;
		
		return checkBins;
	}
}
