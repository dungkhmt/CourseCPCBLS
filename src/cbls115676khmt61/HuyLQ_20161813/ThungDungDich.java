package cbls115676khmt61.HuyLQ_20161813;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class ThungDungDich {
	int N; // so chat long
	int[] thetich; // the tich cac chat long
	int so_thung; //so thung
	int[] the_tich_thung; // the tich thung
	int so_rang_buoc;
	ArrayList<ArrayList<Integer>> rang_buoc;
	VarIntLS X[]; // X[i] = j: chat long i thuoc thung j
	LocalSearchManager mgr;
	ConstraintSystem S;
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		S = new ConstraintSystem(mgr);
		X = new VarIntLS[N];
		for (int i = 0; i < N; i++) {
			X[i] = new VarIntLS(mgr, 0, so_thung);
		}
		for (int i = 0; i < so_rang_buoc; i++) {
			ArrayList<VarIntLS> temp = new ArrayList<VarIntLS>();
			for (int j = 0; j < rang_buoc.get(i).size(); j++) {
				temp.add(X[rang_buoc.get(i).get(j)]);
			}
			S.post(new AllDifferent(arrayFromList(temp)));
		}
		for (int i = 0; i < so_thung; i++) {
			S.post(new LessOrEqual(new ConditionalSum(X, thetich, i), the_tich_thung[i]));
		}
		mgr.close();
	}
	public VarIntLS[] arrayFromList(ArrayList<VarIntLS> temp) {
		VarIntLS[] return_temp = new VarIntLS[temp.size()];
		for (int i = 0; i < temp.size(); i++) {
			return_temp[i] = temp.get(i);
		}
		return return_temp;
	}
	public void solve() {
		HillClimbingSearch h = new HillClimbingSearch();
		h.search(S, 10000);
	}
	
	public void printSol() {
		for (int i = 0; i < N; i++) {
			System.out.print("X[" + i + "] = " + X[i].getValue() + " ");
		}
	}
	
	public void readData(String filename) throws FileNotFoundException {
		Scanner s = new Scanner(new File(filename));
		N = s.nextInt();
		so_thung = s.nextInt();
		so_rang_buoc = s.nextInt();
		thetich = new int[N];
		the_tich_thung = new int[so_thung];
		for (int i = 0; i < N; i++) {
			thetich[i] = s.nextInt();
		}
		for (int i = 0; i < so_thung; i++) {
			the_tich_thung[i] = s.nextInt();
		}
		int n;
		rang_buoc = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < so_rang_buoc; i++) {
			n = s.nextInt();
			rang_buoc.add(new ArrayList<Integer>());
			for (int j = 0; j < n; j++) {
				rang_buoc.get(i).add(s.nextInt());
			}
		}
		s.close();
	}
	
	public static void main(String args[]) throws FileNotFoundException {
		ThungDungDich thungDungDich = new ThungDungDich();
		thungDungDich.readData("D:/Document/Java/Test-CBLS/File/thung_dung_dich.txt");
		thungDungDich.stateModel();
		thungDungDich.solve();
		thungDungDich.printSol();
	}
}
