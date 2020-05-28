package cbls115676khmt61.HoangVD_20161728;

import java.io.File;
import java.util.Scanner;


import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.constraints.basic.LessOrEqual;
import localsearch.constraints.basic.NotEqual;
import localsearch.functions.basic.FuncPlus;
import localsearch.functions.conditionalsum.ConditionalSum;
import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;
import sun.net.www.content.audio.x_aiff;

public class ExamSchedulingCBLS {
	public static class Conflict {
		public int a, b;

		public Conflict(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}
	
	int no_subjects;	// so mon thi
	int no_conflicts;	// so cap mon khong thi cung kip
	int no_rooms;		// so phong thi
	int[] no_students_of_subjects;	// so sinh vien thi cac mon
	Conflict[] conflicts;	// cac cap mon thi khong thi cung kip
	int[] room_capacities;	// suc chua cac phong thi
	int time_slot;
		
		
	LocalSearchManager mgr;
	VarIntLS[] X;
	VarIntLS[][] Y;
	ConstraintSystem S;
	
	public void readData(String filename) {
		try {
			Scanner scanner = new Scanner(new File(filename));
			no_subjects = scanner.nextInt();
			no_conflicts = scanner.nextInt();
			no_rooms = scanner.nextInt();
			
			time_slot = no_subjects;
			no_students_of_subjects = new int[no_subjects];
			conflicts = new Conflict[no_conflicts];
			room_capacities = new int[no_rooms];
			
			for(int i = 0; i < no_subjects; i++) {
				no_students_of_subjects[i] = scanner.nextInt();
			}
			
			for(int i = 0; i < no_rooms; i++) {
				room_capacities[i] = scanner.nextInt();
			}

			for(int i = 0; i < no_conflicts; i++) {
				conflicts[i] = new Conflict(scanner.nextInt(), scanner.nextInt());
			}
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public void stateModel() {
		mgr = new LocalSearchManager();
		X = new VarIntLS[no_subjects];
		Y = new VarIntLS[no_subjects][no_rooms];
		S = new ConstraintSystem(mgr);
		for(int s = 0; s < no_subjects; s++) {
			X[s] = new VarIntLS(mgr, 0, time_slot-1);
			for(int r = 0; r < no_rooms; r++) {
				Y[s][r] = new VarIntLS(mgr, 0, 1);
			}
		}
		
		// Add constraint
		// cac mon khong thi cung kip
		for(int c = 0; c < no_conflicts; c++) {
			S.post(new NotEqual(X[conflicts[c].a], X[conflicts[c].b]));
		}
		
		// tong suc chua cac phong thi mon s phai lon hon hoac bang 
		for(int s = 0; s < no_subjects; s++) {
			S.post(new LessOrEqual(no_students_of_subjects[s], new ConditionalSum(Y[s], room_capacities, 1)));
		}
		
		// hai mon cung kip thi khong cung phong thi
		
		for(int s1 = 0; s1 < no_subjects; s1++) {
			for(int s2 = 0; s2 < no_subjects; s2++) {
				if(s1 != s2) {
					for(int r = 0; r < no_rooms; r++) {
						S.post(new Implicate(new IsEqual(X[s1], X[s2]), new LessOrEqual(new FuncPlus(Y[s1][r], Y[s2][r]), 1)));
					}
				}
				
			}
		}
		mgr.close();
	}
	
	public void search() {
		System.out.println(S.getVariables());
		TabuSearch searcher = new TabuSearch(S);
		searcher.search(10000, 4, 100);
	}
	
	public void printSolution() {
		for(int s = 0; s < no_subjects; s++) {
			System.out.print("Mon " + s + " : kip " + X[s].getValue() + " phong: ");
			for(int r = 0; r < no_rooms; r++) {
				if(Y[s][r].getValue() == 1) {
					System.out.print(r + " ");
				}
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		ExamSchedulingCBLS eSheduling = new ExamSchedulingCBLS();
		String input = "./data/scheduling/gc_100_1";
		eSheduling.readData(input);
		long time_start = System.currentTimeMillis();
		eSheduling.stateModel();
		eSheduling.search();
		long time_end = System.currentTimeMillis();
		System.out.print("\n"+(time_end - time_start)/1000.0);
		eSheduling.printSolution();
	}

}
