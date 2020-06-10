package exam_scheduling;

import java.io.File;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;


public class ExamScheduling {
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
	public ExamScheduling() {
		
	}
	
	public void cp_solver_choco() {
		Model model = new Model();
		IntVar[] x = model.intVarArray(no_subjects, 0, time_slot-1);
		IntVar[][] y = model.intVarMatrix("y", no_subjects, no_rooms, 0, 1);
		// Constraint conflicts
		for(int e=0;e<no_conflicts;e++) {
			model.arithm(x[conflicts[e].a], "!=", x[conflicts[e].b]).post();
		}
		//  Constraint capacity 
		for(int s=0;s<no_subjects;s++) {
			model.scalar(y[s], room_capacities, ">=", no_students_of_subjects[s]).post();
		}
		// Constraint rooms
		for(int s1 =0; s1<no_subjects;s1++) {
			for(int s2 =0 ;s2<no_subjects;s2++) {
				if(s1 != s2) {
					for(int r=0;r<no_rooms;r++) {
						model.ifThen(model.arithm(x[s1],"=",x[s2]), model.arithm(y[s1][r], "+", y[s2][r],"<=", 1));
					}
				}
			}
		}
		
		IntVar obj = model.intVar(0, time_slot-1);
		for(int s=0;s<no_rooms;s++) {
			model.arithm(obj, ">=", x[s]).post();
		}
		model.setObjective(Model.MINIMIZE,obj);
		Solver solver = model.getSolver();
		solver.limitTime(600000);
		int[] times = new int[no_subjects];
		int[][] rooms = new int[no_subjects][no_rooms];
		while(solver.solve()) {
			for(int s=0;s<no_subjects;s++) {
				for(int t=0;t<time_slot;t++) {
					times[s] = x[s].getValue();
				}
				for(int r=0;r<no_rooms;r++) {
					rooms[s][r] = y[s][r].getValue();
				}
			}
		}
//		Solution solution = solver.findSolution();
//		if(solution != null) {
//			for(int s=0;s<S;s++) {
//				for(int t=0;t<T;t++) {
//					time_slot[s] = x[s].getValue();
//				}
//				for(int r=0;r<R;r++) {
//					rooms[s][r] = y[s][r].getValue();
//				}
//			}
//		}
//		else {
//			System.out.print("xxxxxxxxxxxxx Can't find solution xxxxxxxxxxxxx");
//			return;
//		}
		
		System.out.print("---------------CP_Solver_Choco------------------ \n");
		for (int s = 0; s < no_subjects; s++) {
//			System.out.print("time slot used: "+ obj.getValue()+"\n");
			System.out.print("subject: " + s + "  time slot: " + times[s]);
			System.out.print(" room: ");
			for (int r = 0; r< no_rooms; r++) {
				if (rooms[s][r] > 0) {
					System.out.print(r + "  ");
				}
			}
			System.out.print("\n");
			
		}
	}
	
	public static void main(String[] args) {
		
		ExamScheduling es = new ExamScheduling();
		String input = "C:\\Users\\Huy\\eclipse-workspace\\exam-scheduling-project\\src\\exam_scheduling\\data\\gc_50_3";
		es.readData(input);
		long time_start = System.currentTimeMillis();
		es.cp_solver_choco();
		long time_end = System.currentTimeMillis();
		System.out.print("\n"+(time_end - time_start)/1000.0);
	}
}
