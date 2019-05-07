package khmtk60.miniprojects.G6;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.chrono.IsoChronology;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.swing.plaf.synth.SynthSpinnerUI;

import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInput;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputBin;
import khmtk60.miniprojects.multiknapsackminmaxtypeconstraints.model.MinMaxTypeMultiKnapsackInputItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

class Ass{
	int index;
	int value;
	public Ass(int ind, int v) {
		index = ind; 
		value = v;
	}
}

class ListBin{
	public ArrayList<Integer> Bin ;
	public ListBin() {
		Bin =  new ArrayList<>();
	}
	
}
public class MinMaxTypeMultiKnapsackSolution {
	int Bins[][];// chua cac thong so ve R va T .kich thuoc nBin*2 . 0 = R 1 = T
	double Bind[][]; // chua cac thong so ve W , minload, P. kich thuoc nbin*3 0 =W, 1  = P , 2 = minload
	int Items[][]; // chua R va T cua Items . kich thuoc nItem*2 0 = r, 1 = t
	double Itemd[][]; // chua cac tham so  ve w va p cua Items kich thuoc nItem*2  0 = w 1 =p 
	int nItem,nBin;
	int X[]; // bien quyet dinh. _X[i] la bin ma item i duoc cho vao 
	int Cond[][]; // _Cond[i][j] = 0 neu item i cho vao bin j duoc 
	double binLoad[][];
	int nRT[][];
	int BSort[];
	int ISort[];
	double _V[]; // chua violation cua cac bin.
	int _FobList[];
	ListBin listBin[];// chua danh sach bin item co the cho vao 
	int Binstatus[];
	int BinR[][]; // so loai R trong bin
	int BinT[][]; // so loai T trong bin
	Map<Integer, Integer> mapR = new HashMap<>();
	Map<Integer, Integer> mapT = new HashMap<>();
	Set<Integer> setR  = new HashSet<>();
	Set<Integer> setT = new HashSet<>();
	ArrayList<Integer> arrR = new ArrayList<>();
	ArrayList<Integer> arrT = new ArrayList<>();
	public MinMaxTypeMultiKnapsackSolution() {
		MinMaxTypeMultiKnapsackInput Input1 = new MinMaxTypeMultiKnapsackInput();
		MinMaxTypeMultiKnapsackInput Input;
		MinMaxTypeMultiKnapsackInputItem[] IT ;
		MinMaxTypeMultiKnapsackInputBin[] BI;
		Input = Input1.loadFromFile(
				"C:\\Users\\ironman\\Desktop\\git\\CourseCPCBLS\\OpenCBLS\\src\\khmtk60\\miniprojects\\multiknapsackminmaxtypeconstraints\\MinMaxTypeMultiKnapsackInput-3000.json");
		
		IT = Input.getItems();
		BI = Input.getBins();
		nItem = IT.length;
		nBin = BI.length;
		X = new int[nItem];
		Items = new int[nItem][2];
		Itemd = new double[nItem][2];
		Bins = new int [nBin][2];
		Bind = new double[nBin][3];
		Cond = new int[nItem][nBin];
		nRT = new int[nBin][2];
		BSort = new int[nBin];
		ISort = new int[nItem];
		_V = new double[nBin];
		listBin = new ListBin[nItem];
		Binstatus = new int[nBin];
		binLoad = new double[nBin][2]; // chua min load cua cac bin hang 0 chua W hang 1 chua P
		setZero(binLoad);
		for(int i =0 ; i< nBin; i++) {
			Binstatus[i]  = 0;
			nRT[i][0] = nRT[i][1] =0;
		}
		for(int i = 0 ; i< nItem; i++) {
			ISort[i] = i;
			listBin[i] = new ListBin();
			for(int j = 0 ;j < nBin; j++) {
				Cond[i][j] = 0; 
			}
		}
		for(int i = 0 ; i < nItem; i++) {
			if(!setR.contains(IT[i].getR())) {
				mapR.put( IT[i].getR(),arrR.size());
				setR.add(IT[i].getR());
				arrR.add(IT[i].getR());
				
			}
			if(!setT.contains(IT[i].getT())) {
				mapT.put( IT[i].getT(),arrT.size());
				setT.add(IT[i].getT());
				arrT.add(IT[i].getT());
			}
			
			X[i] = -1; 
			Items[i][0] = IT[i].getR();
			Items[i][1] = IT[i].getT();
			Itemd[i][0] = IT[i].getW();
			Itemd[i][1] = IT[i].getP();
			for(int j = 0 ; j< IT[i].getBinIndices().length; j++) {
				Cond[i][IT[i].getBinIndices()[j]] = 1;
				listBin[i].Bin.add(IT[i].getBinIndices()[j]);
			}
				
		}
		
		BinR = new int[nBin][setR.size()];
		BinT = new int[nBin][setT.size()];
		for(int i = 0; i<nBin; i++) {
			for(int j=0; j<arrR.size();j++) {
				BinR[i][j] = 0;
			}
			for(int j=0; j< arrT.size(); j++) {
				BinT[i][j] = 0;
			}
		}
		
		for(int i = 0 ; i< nBin; i++) {
			BSort[i] = i;
			Bins[i][0] = BI[i].getR();
			Bins[i][1] = BI[i].getT();
			Bind[i][0] = BI[i].getCapacity();
			Bind[i][1] = BI[i].getP(); 
			Bind[i][2] = BI[i].getMinLoad();
		}
		
		
	}
	
	
	public void solve() {
		int _c = 0;
		
		int checkI[] = new int[nItem];// = 1 neu item da duoc load = 0 neu nguoc lai
		for(int i = 0 ; i< nItem; i++) {
			checkI[i] = 0;
		}
		Set<Integer> R = new HashSet<>();
		Set<Integer> T = new HashSet<>();
		
		
		for(int i = 0 ; i< nBin; i++) {
			_c = 0 ; 
			R.clear();
			T.clear();
			while(_c < 4) {
				if(_c == 0 ) {
					for(int j = 0 ; j<nItem; j++) {
						if(Cond[j][i] == 1 && checkI[j] == 0) {
							if(R.size() == 0) {
								if(Itemd[j][0] < Bind[i][0] && Itemd[j][1] < Bind[i][1]) {
									BinR[i][mapR.get(Items[j][0])]++ ;
									BinT[i][mapT.get(Items[j][1])]++;
									R.add(Items[j][0]);
									T.add(Items[j][1]);
									X[j] = i;
									checkI[j] = 1;
									binLoad[i][0] +=  Itemd[j][0] ;
									binLoad[i][1] +=  Itemd[j][1] ;
								}
								
							}else {
								if(R.contains(Items[j][0]) && T.contains(Items[j][1]) && (binLoad[i][0] + Itemd[j][0] <= Bind[i][0]) && (binLoad[i][1] + Itemd[j][1] <= Bind[i][1]) ) {
									BinR[i][mapR.get(Items[j][0])]++ ;
									BinT[i][mapT.get(Items[j][1])]++;
									X[j] = i;
									checkI[j] = 1;
									binLoad[i][0] +=  Itemd[j][0] ;
									binLoad[i][1] +=  Itemd[j][1] ;
								}
							}
						}
					}
				}
				if(_c == 1) {
					if(Bins[i][0] > Bins[i][1]) {
						for(int j = 0 ; j<nItem; j++) {
							if(Cond[j][i]  == 1 && checkI[j] == 0) {
								 
									if( T.contains(Items[j][1]) && (binLoad[i][0] + Itemd[j][0] <= Bind[i][0]) && (binLoad[i][1] + Itemd[j][1] <= Bind[i][1]) && (R.size() < Bins[i][0]) ) {
										BinR[i][mapR.get(Items[j][0])]++ ;
										BinT[i][mapT.get(Items[j][1])]++;
										R.add(Items[j][0]);
										X[j] = i;
										checkI[j] = 1;
										binLoad[i][0] +=  Itemd[j][0] ;
										binLoad[i][1] +=  Itemd[j][1] ;
									}
								
							}
						}
					}else {
						for(int j = 0 ; j<nItem; j++) {
							if(Cond[j][i]  == 1 && checkI[j] == 0) {
								 
									if( R.contains(Items[j][1]) && (binLoad[i][0] + Itemd[j][0] <= Bind[i][0]) && (binLoad[i][1] + Itemd[j][1] <= Bind[i][1]) && (T.size() < Bins[i][1]) ) {
										BinR[i][mapR.get(Items[j][0])]++ ;
										BinT[i][mapT.get(Items[j][1])]++;
										T.add(Items[j][1]);
										X[j] = i;
										checkI[j] = 1;
										binLoad[i][0] +=  Itemd[j][0] ;
										binLoad[i][1] +=  Itemd[j][1] ;
									}
								
							}
						}
					}
					
				}
				
				if(_c == 2) {
					if(Bins[i][0] <= Bins[i][1]) {
						for(int j = 0 ; j<nItem; j++) {
							if(Cond[j][i]  == 1 && checkI[j] == 0) {
								 
									if( T.contains(Items[j][1]) && (binLoad[i][0] + Itemd[j][0] <= Bind[i][0]) && (binLoad[i][1] + Itemd[j][1] <= Bind[i][1]) && (R.size() < Bins[i][0]) ) {
										BinR[i][mapR.get(Items[j][0])]++ ;
										BinT[i][mapT.get(Items[j][1])]++;
										R.add(Items[j][0]);
										X[j] = i;
										checkI[j] = 1;
										binLoad[i][0] +=  Itemd[j][0] ;
										binLoad[i][1] +=  Itemd[j][1] ;
									}
								
							}
						}
					}else {
						for(int j = 0 ; j<nItem; j++) {
							if(Cond[j][i]  == 1 && checkI[j] == 0) {
								 
									if( R.contains(Items[j][1]) && (binLoad[i][0] + Itemd[j][0] <= Bind[i][0]) && (binLoad[i][1] + Itemd[j][1] <= Bind[i][1]) && (T.size() < Bins[i][1]) ) {
										BinR[i][mapR.get(Items[j][0])]++ ;
										BinT[i][mapT.get(Items[j][1])]++;
										T.add(Items[j][1]);
										X[j] = i;
										checkI[j] = 1;
										binLoad[i][0] +=  Itemd[j][0] ;
										binLoad[i][1] +=  Itemd[j][1] ;
									}
								
							}
						}
					}
				}
				
				if(_c == 3) {
					for(int j = 0 ; j<nItem; j++) {
						if(Cond[j][i]  == 1 && checkI[j] == 0) {
							 //&& binLoad[i][0] < Bind[i][2] 
								if(  (binLoad[i][0] + Itemd[j][0] <= Bind[i][0]) && (binLoad[i][1] + Itemd[j][1] <= Bind[i][1]) && T.size()<Bins[i][1] && R.size()< Bins[i][0] ) {
									BinR[i][mapR.get(Items[j][0])]++ ;
									BinT[i][mapT.get(Items[j][1])]++;
									R.add(Items[j][0]);
									T.add(Items[j][1]);
									X[j] = i;
									checkI[j] = 1;
									binLoad[i][0] +=  Itemd[j][0] ;
									binLoad[i][1] +=  Itemd[j][1] ;
								}
							
						}
					}
				}
				
				_c++;
				
				
			}
			nRT[i][0] = R.size();
			nRT[i][1] = T.size();
		}
		
		for(int i = 0; i< nItem; i++) {
			Random R1 = new Random();
			int temp;
			if(X[i] <0 ) {
				temp = R1.nextInt(nBin);
				X[i] = temp;
				binLoad[temp][0] += Itemd[i][0];
				binLoad[temp][1] += Itemd[i][1];
				if(BinR[temp][mapR.get(Items[i][0])] > 0) {
					BinR[temp][mapR.get(Items[i][0])]++;
				}else {
					BinR[temp][mapR.get(Items[i][0])]++;
					nRT[temp][0] ++;
					
				}
				if(BinT[temp][mapT.get(Items[i][1])] > 0) {
					BinT[temp][mapT.get(Items[i][1])]++;
				}else {
					BinT[temp][mapT.get(Items[i][1])]++;
					nRT[temp][1] ++;
					
				}
				
			}
		}
		for(int i = 0 ; i< nBin; i++) {
			_V[i] = getViolation(i);
		}
		
		
	}
	
	
	public void updateBinstatus() {
		for(int i = 0; i< nBin; i++) {
			Binstatus[i] = 0;
		}
		for(int i=0; i <nItem; i++) {
			Binstatus[X[i]] = 1;
		}
	}
	public double getViolation(int indexBin) { 
		double violation = 0;
		double tmp;
		if(binLoad[indexBin][0] > Bind[indexBin][0]) {
			violation++;
		}
		if(binLoad[indexBin][0]>0 && binLoad[indexBin][0]< Bind[indexBin][2] ) {
			
//			tmp =  (Bind[indexBin][2] - binLoad[indexBin][0])/1000 + 15 ;
//			
//			violation = violation + Math.pow(2, tmp);
			violation+= 100;
		}
		
		if(nRT[indexBin][0] > Bins[indexBin][0]) {
			violation = violation + nRT[indexBin][0]- Bins[indexBin][0]; 
		}
		if(nRT[indexBin][1]> Bins[indexBin][1]) {
			violation = violation + nRT[indexBin][1] - Bins[indexBin][1];
 		}
		
		if(binLoad[indexBin][1] > Bind[indexBin][1]) {
			violation++;
		}
		if(violation > 0) {
			violation += 1000;
		}
		
		return violation;
		
		
	}
	
	public Ass getDetal() {
		
		Random R = new Random();
		int count=0,count1 =0;
		Ass resut ;
		ArrayList<Ass> ArrAss = new ArrayList<>();
		int index,value;
		int CurVal1,CurVal ;
		int tmp; 
		double tmp1;
		double detal = Integer.MAX_VALUE;
		int i;
		
		while(count1 < 100) {
			i = R.nextInt(nItem);
			
			if(!checkFob(i) ) {
				CurVal1 = X[i];
				int j  =0;
				count =0;
				
				
				while(count < listBin[i].Bin.size()) {
					j = listBin[i].Bin.get(count);
					CurVal = X[i];
					
					if( j != CurVal1) {
						X[i] = j;
						updateRT(i, j, CurVal);
						
						
						tmp1 = getViolation(j) - _V[j] + getViolation(CurVal) - _V[CurVal];
						if(tmp1 < detal) {
							ArrAss.clear();
							ArrAss.add(new Ass(i,j));
							detal = tmp1;
						}else {
							if(tmp1 == detal) {
								ArrAss.add(new Ass(i,j));
							}
						}
					}
					count++;
				}
				
				
				j = X[i];
				X[i] = CurVal1;
				updateRT(i, CurVal1, j);
				
				
			}
			count1++;
		}
		
		if(ArrAss.size() == 0) {
			return null;
		}else {
			tmp = R.nextInt(ArrAss.size());
			
			resut = ArrAss.get(tmp);
			return resut;
		}
		
		
		
	}
	
	
	public boolean checkFob(int index) {
		boolean r = false;
		for(int i = 0; i< _FobList.length; i++) {
			if(_FobList[i] == index) {
				r = true;
				break;
			}
		}
		return r;
	}
	
	public void updateRT(int index, int newvalue, int oldvalue) {
		int i = index;
		int j = oldvalue;
		int CurVal1 = newvalue;
		if(BinR[j][mapR.get(Items[i][0])] > 1) {
			BinR[j][mapR.get(Items[i][0])]--;
		}else {
			BinR[j][mapR.get(Items[i][0])]--;
			nRT[j][0]--;  
		}
		if(BinT[j][mapT.get(Items[i][0])] > 1) {
			BinT[j][mapT.get(Items[i][0])]--;
		}else {
			BinT[j][mapT.get(Items[i][0])]--;
			nRT[j][1]--;  
		}
		
		if(BinR[CurVal1][mapR.get(Items[i][0])] > 0) {
			BinR[CurVal1][mapR.get(Items[i][0])]++;
		}else {
			BinR[CurVal1][mapR.get(Items[i][0])]++;
			nRT[CurVal1][0]++;  
		}
		if(BinT[CurVal1][mapT.get(Items[i][0])] > 0) {
			BinT[CurVal1][mapT.get(Items[i][0])]++;
		}else {
			BinT[CurVal1][mapT.get(Items[i][0])]++;
			nRT[CurVal1][1]++;  
		}
		binLoad[newvalue][0] += Itemd[index][0];
		binLoad[newvalue][1] += Itemd[index][1];
		binLoad[oldvalue][0] -= Itemd[index][0];
		binLoad[oldvalue][1] -= Itemd[index][1];
		
	}
	
	
	public void mySearch(int maxInter, int maxInterUp, int lenFob) { // maxInterUp so vong lap toi da cho phep violation tang.
		Ass ass; 
		Random R = new Random();
		double tmp1,tmp2;
		int tmp,it =0;
		_FobList = new int[lenFob];
		for(int i = 0 ; i<lenFob; i++) {
			_FobList[i] = -1;
		}
		int it1 = 0,it2 =0;
		tmp2 = Integer.MAX_VALUE;
		int Xbest[] = new int[nItem];
		for(int i = 0 ; i< nItem; i++) {
			Xbest[i] = X[i];
		}
		
		double BestV = Integer.MAX_VALUE;
		double BestV2 = Integer.MAX_VALUE;
		double BestV3 = Integer.MAX_VALUE;
		int indexBin;
		
		while(tmp2 > 0 && it < maxInter  ) {
				it++;
				if(it1 < maxInterUp) {
					
						
						tmp1 = getV();
						ass = getDetal();
						
						if(ass != null) {
							tmp = X[ass.index];
							X[ass.index] = ass.value;
							updateRT(ass.index, ass.value, tmp);
							
							
							_V[tmp] = getViolation(tmp);
							_V[ass.value] = getViolation(ass.value);
							for(int i = 0; i< lenFob-1; i++) {
								_FobList[i] = _FobList[i+1];
							}
							_FobList[lenFob-1] = ass.index;
							
							tmp2 = getV();
							if(tmp2 >= tmp1) {
									
									it1 ++;
							}else {
								if(tmp2 < BestV ) {
									BestV = tmp2;
									for(int i = 0 ; i< nItem; i++) {
										Xbest[i] = X[i];
										
									}
									it1 = 0;
								}
								
							}
							
							it1++;
						}
						System.out.println("step "+ it + "    violation :  "+ getV());
						
					
					
				}else {
					for(int k = 0; k< nItem; k++) {
						tmp = X[k];
						X[k] = Xbest[k];
						updateRT(k, X[k], tmp);
						_V[X[k]] = getViolation(X[k]);
						_V[tmp] = getViolation(tmp);
					}
					System.out.println("reset---------------------------------------");
					it1 = 0;
					int index1 ;
					int index2; 
					index1 = R.nextInt(nItem);
					index2 = R.nextInt(nItem);
					tmp = X[index1];
					X[index1] = X[index2];
					updateRT(index1, X[index1], tmp);
					X[index2] = tmp;
					updateRT(index2, X[index2], X[index1]);
					_V[X[index1]] = getViolation(X[index1]);
					_V[X[index2]] = getViolation(X[index2]);
					
				}
				
				System.out.println("step "+ it + "    violation :  "+ getV());
			
		}
	}
	
	public int getV() {
		int tmp = 0 ;
		for(int i = 0 ; i< nBin; i++) {
			tmp+= _V[i];
		}
		return tmp;
	}
	public void setZero(double arr[][]) {
		for(int i = 0 ; i< arr.length; i++) {
			for(int j = 0; j< arr[i].length ; j++) {
				arr[i][j] = 0;
			}
		}
	}
	
	public void printResult() {
		for(int i = 0 ; i< nItem; i++) {
			System.out.print("   " + X[i]);
		}
	}
	
	public void printLoad() {
		System.out.println();
		for(int i = 0; i< nBin; i++) {
			System.out.print("   " + binLoad[i][0]);
		}
	}
	
	public void printMinload() {
		System.out.println();
		for(int i = 0 ; i< nBin; i++ ) {
			System.out.print("  " + Bind[i][2]);
		}
	}
	
	public void printMaxLoad() {
		System.out.println();
		for(int i = 0 ; i< nBin; i++) {
			System.out.print("  "+ Bind[i][0]);
		}
	}
	
	public void printMaxP() {
		System.out.println();
		for(int i = 0 ; i< nBin; i++) {
			System.out.print("  "+ Bind[i][1]);
		}
	}
	
	public void printViolation() {
		System.out.println("violation :");
		for(int i = 0 ; i< nBin; i++) {
			System.out.print("       "+i+" = "+_V[i]);
		}
		System.out.println();
	}
	
	public void printDetail() {
		
		updateBinstatus();
		
		int countB =0;
		int vR=0,vT =0, vW =0, vmW =0,vP =0;
		
		for(int j = 0; j<nBin; j++) {
			
			if(Binstatus[j] ==1) {
				countB++;
				System.out.println();
				int Vp=0,Vw=0,Vmw=0,Vr=0,Vt=0;
				int violation = 0;
				double w=0,p=0;
				Set<Integer> nR = new HashSet<>();
				Set<Integer> nT = new HashSet<>();
				System.out.println("    bin "+ j);
				for(int i = 0 ; i<nItem; i++) {
					if(X[i]==j) {
						w = w + Itemd[i][0];
						p = p + Itemd[i][1];
						if(!nR.contains(Items[i][0])) {
							nR.add(Items[i][0]);
							
						}
						if(!nT.contains(Items[i][1])) {
							nT.add(Items[i][1]);
						}
					}
				}
				if(w > Bind[j][0]) {
					vW++;
					System.out.print(" maxload "+ 1);
				}
				if(w>0 && w< Bind[j][2] ) {
					vmW++;
					System.out.print("   minload "+ 1);
				}
				
				if(nR.size() > Bins[j][0]) {
					vR++;
					violation =  nR.size()- Bins[j][0]; 
					System.out.print("       ViolatioR "+ violation);
					 
				}
				if(nT.size()> Bins[j][1]) {
					vT++;
					violation =   nT.size() - Bins[j][1];
					System.out.print("       ViolatioT "+ violation);
		 		}
				
				if(p > Bind[j][1]) {
					vP++;
					System.out.print(" maxP "+ 1);
				}
				
			}
		}
		System.out.println();
		System.out.println("countBin: "+countB);
		System.out.println("vR "+vR);
		System.out.println("vT "+vT);
		System.out.println("vmW "+vmW);
		System.out.println("vW "+vW);
		System.out.println("vP "+vP);
		
		
	}
	public static void main(String[] args ) {
		MinMaxTypeMultiKnapsackSolution C = new MinMaxTypeMultiKnapsackSolution();
		C.solve();
		C.mySearch(100000, 6000, 100);
		C.printResult();
		C.printViolation();
		C.printDetail();
	}
	
}
