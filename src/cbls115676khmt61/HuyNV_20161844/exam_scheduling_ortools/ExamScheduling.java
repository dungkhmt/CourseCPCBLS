//package scheduling;
import com.google.ortools.linearsolver.MPConstraint;
import com.google.ortools.linearsolver.MPObjective;
import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.linearsolver.MPVariable;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearExpr;
import java.util.Collections;
import java.util.Scanner;
import java.awt.print.Printable;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

public class ExamScheduling {
	public static class Conflict {
		public int a, b;

		public Conflict(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}

	static {
		System.loadLibrary("jniortools");
	}
	
	int no_subjects;	// so mon thi
	int no_conflicts;	// so cap mon khong thi cung kip
	int no_rooms;		// so phong thi
	int[] no_students_of_subjects;	// so sinh vien thi cac mon
	Conflict[] conflicts;	// cac cap mon thi khong thi cung kip
	int[] room_capacities;	// suc chua cac phong thi
	int time_slot ;
	
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
	
	// mo hinh hoa
	private static MPSolver createSolver(String solverType) {
		return new MPSolver("MIPDiet", MPSolver.OptimizationProblemType.valueOf(solverType));
	}
	// constraint programming
	public void cpSolver() {
		CpModel model = new CpModel();
		IntVar[] x = new IntVar[no_subjects];
		IntVar[][] y = new IntVar[no_subjects][no_rooms];
		for(int s = 0;s < no_subjects; s++) {
			x[s] = model.newIntVar(0, time_slot-1,"x["+s+"]");
			for(int r = 0; r < no_rooms; r++) {
				y[s][r] = model.newIntVar(0,1,"y["+s+"]["+r+"]");
			}
		}
		// x[ea] != x[eb]
		for(int e = 0; e < no_conflicts; e++) {
			model.addDifferent(x[conflicts[e].a], x[conflicts[e].b]);
		}
		// Room(S) >= d(s)
		for(int s = 0; s < no_subjects; s++) {
			model.addGreaterOrEqual(LinearExpr.scalProd(y[s], room_capacities), no_students_of_subjects[s]);
		}
		
		// bool b1
		// bool b2
		IntVar[][] b1 = new IntVar[no_subjects][no_subjects];
		IntVar[][][] b2 = new IntVar[no_subjects][no_subjects][no_rooms];
		for(int s1 = 0; s1 < no_subjects; s1++) {
			for(int s2 = 0 ; s2 < no_subjects; s2++) {
				b1[s1][s2] = model.newBoolVar("b1_"+s1+"_"+s2);
				for(int r = 0; r < no_rooms; r++) {
					b2[s1][s2][r] = model.newBoolVar("b2_"+s1+"_"+s2+"_"+r);
				}
			}
		}
		// b1 = x[s1] == x[s2]
		for(int s1 = 0; s1 < no_subjects; s1++) {
			for(int s2 = 0; s2 < no_subjects; s2++) {
				if(s1 != s2) {
					model.addEquality(x[s1], x[s2]).onlyEnforceIf(b1[s1][s2]);
					model.addDifferent(x[s1], x[s2]).onlyEnforceIf(b1[s1][s2].not());
					for(int r = 0; r < no_rooms; r++) {
						//b2 = y[s1][r]+y[s2][r] <= 1
						model.addLessOrEqual(LinearExpr.sum(new IntVar[] {y[s1][r], y[s2][r]}), 1).onlyEnforceIf(b2[s1][s2][r]);;
						model.addGreaterThan(LinearExpr.sum(new IntVar[] {y[s1][r], y[s2][r]}), 1).onlyEnforceIf(b2[s1][s2][r].not());
						// x[s1] == x[s2] => y[s1][r] + y[s2][r] <= 1
						model.addImplication(b1[s1][s2], b2[s1][s2][r]);
				}
				}
			}
		}
//		 heuristic constraint		
		IntVar[][] z = new IntVar[no_subjects][no_rooms];
		for(int s=0;s<no_subjects;s++) {
			for(int r=0;r<no_rooms;r++) {
				z[s][r] = model.newIntVar(1, 1000, "z["+s+"]["+r+"]");
			}
		}
		IntVar[][] b3 = new IntVar[no_subjects][no_rooms];
		for(int s=0;s<no_subjects;s++) {
			for(int r=0;r<no_rooms;r++) {
				b3[s][r] = model.newBoolVar("b3["+s+"]["+r+"]");
				model.addEquality(y[s][r], 1).onlyEnforceIf(b3[s][r]);
				model.addLessThan(y[s][r], 1).onlyEnforceIf(b3[s][r].not());
				model.addEquality(z[s][r], room_capacities[r]).onlyEnforceIf(b3[s][r]);
				model.addEquality(z[s][r], 1000).onlyEnforceIf(b3[s][r].not());
			}
		}
		IntVar[] minc = new IntVar[no_subjects];
		for(int s=0;s<no_subjects;s++) {
			minc[s] = model.newIntVar(1, 1000, "minc["+s+"]");
			model.addMinEquality(minc[s], z[s]);
			model.addLessOrEqualWithOffset(LinearExpr.scalProd(y[s],room_capacities),minc[s], - no_students_of_subjects[s]);
		}
		// add objective
		
		IntVar obj ;
		obj = model.newIntVar(0, time_slot - 1, "obj");
		model.addMaxEquality(obj, x);
		model.minimize(obj);
		
		CpSolver solver = new CpSolver();
		solver.getParameters().setMaxTimeInSeconds(600);
		CpSolverStatus status = solver.solve(model);
		System.out.println("--------------CP_Model--------------");
		System.out.println("solve status: " + status);
		if(status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
			System.out.print("Tong time slot: "+ (solver.objectiveValue()+1)+"\n");
			for (int s = 0; s < no_subjects; s++) {
				System.out.print("subject: " + s + "  time slot: " + solver.value(x[s]));
				System.out.print(" room: ");
				for (int r = 0; r < no_rooms; r++) {
					if (solver.value(y[s][r]) > 0) {
						System.out.print(r + "  ");
					}
				}
				System.out.print("\n");
			}
		}
		else {
			System.out.print("don't have solution!!!!!!");
		}
		return;
	}
	
	// mixed integer programing
	public void mipSolver(String solverType) {
		MPSolver solver = createSolver(solverType);
		double infinity = MPSolver.infinity();

		/** variables */
		// x[s][t] = 1 when subject s is organized in the t(th) exam
		MPVariable[][] x = new MPVariable[no_subjects][time_slot];
		for (Integer s = 0; s < no_subjects; s++) {
			x[s] = solver.makeBoolVarArray(time_slot);
		}
		// y[s][r] = 1 when subject s is organized in room r
		MPVariable[][] y = new MPVariable[no_subjects][no_rooms];
		for (Integer s = 0; s < no_subjects; s++) {
			y[s] = solver.makeBoolVarArray(no_rooms);
		}
		// z[t] = 1 when time slot t is used
		MPVariable[] z = solver.makeBoolVarArray(time_slot);
		// minimize number of time slots are used
		MPObjective obj = solver.objective();
//		obj.setCoefficient(v, 1);
		for (MPVariable objVar : z) {
			obj.setCoefficient(objVar, 1);
		}

		/** add constraint 1 */
		//SUMt (x[s][t]) = 1 
		MPConstraint[] constraint1 = new MPConstraint[no_subjects];
		for (int s = 0; s < no_subjects; s++) {
			constraint1[s] = solver.makeConstraint(1, 1);
			for (int t = 0; t < time_slot; t++) {
				constraint1[s].setCoefficient(x[s][t], 1);
			}
		}

		/** add constraint 2 */
		//SUMr (y[s][r]*c[r] >= d[s]
		MPConstraint[] constraint2 = new MPConstraint[no_subjects];
		for (int s = 0; s < no_subjects; s++) {
			constraint2[s] = solver.makeConstraint(no_students_of_subjects[s], infinity);
			for (int r = 0; r < no_rooms; r++) {
				constraint2[s].setCoefficient(y[s][r], room_capacities[r]);
			}
		}

		/** add constraint 3 */
		// x[s1][t] + s[s2][t] <= z[t] <=> x[s1][t] + x[s2][t] <= 1 and z[t] >= x[s][t]
		MPConstraint[][] constraint3 = new MPConstraint[no_conflicts][time_slot];
		for (int i = 0; i < no_conflicts; i++) {
			for (int t = 0; t < time_slot; t++) {
				constraint3[i][t] = solver.makeConstraint(-infinity, 0);
				constraint3[i][t].setCoefficient(x[conflicts[i].a][t], 1);
				constraint3[i][t].setCoefficient(x[conflicts[i].b][t], 1);
				constraint3[i][t].setCoefficient(z[t], -1);
			}
		}

		/** add constraint 4 */
		// x[s1][t] + x[s2][t] = 2 => y[s1][r] + y[s2][r] <= 1
		int bigM = 50;
		MPConstraint[][][][] constraint4 = new MPConstraint[no_subjects][no_subjects][time_slot][no_rooms];
		for (int s1 = 0; s1 < no_subjects; s1++) {
			for (int s2 = s1 + 1; s2 < no_subjects; s2++) {
				for (int t = 0; t < time_slot; t++) {
					for (int r = 0; r < no_rooms; r++) {
						if (s1 != s2) {
							constraint4[s1][s2][t][r] = solver.makeConstraint(0, 2 * bigM + 1);
							constraint4[s1][s2][t][r].setCoefficient(x[s1][t], bigM);
							constraint4[s1][s2][t][r].setCoefficient(x[s2][t], bigM);
							constraint4[s1][s2][t][r].setCoefficient(y[s1][r], 1);
							constraint4[s1][s2][t][r].setCoefficient(y[s2][r], 1);
						} else {
						}
					}
				}
			}
		}

		/** Minimize by default */
		obj.setMinimization();
		final MPSolver.ResultStatus resultStatus = solver.solve();
		
		/** printing */
		if (resultStatus != MPSolver.ResultStatus.OPTIMAL) {
			System.err.println("The problem does not have an optimal solution!");
			return;
		} else {
			int[] swap_array = new int[time_slot];
			int	count = 0;
			for (int t=0;t<time_slot;t++) {
				if(z[t].solutionValue() > 0) {
					swap_array[t] = count;
					count++;
				}
			}
			int[] t_subject = new int[no_subjects];
			for (int s = 0; s < no_subjects; s++) {
				for (int t = 0; t < time_slot; t++) {
					if (x[s][t].solutionValue() > 0) {
						t_subject[s] = swap_array[t];
						break;
					}
				}
			}
			System.out.print("Result:" +solver.objective().value()+ "time slot\n");
			for (int s = 0; s < no_subjects; s++) {
				System.out.print("subject: " + s + "  time slot: " +t_subject[s]);
				System.out.print(" room: ");
				for (int r = 0; r< no_rooms; r++) {
					if (y[s][r].solutionValue() > 0) {
						System.out.print(r + "  ");
					}
				}
				System.out.print("\n");
				
			}
		}
		return;
	}

	public static void main(String[] args) {
		
		ExamScheduling eSheduling = new ExamScheduling();
		String input = "./data/gc_15_1";
		eSheduling.readData(input);
		long time_start = System.currentTimeMillis();
		eSheduling.cpSolver();
//		eSheduling.mipSolver("CBC_MIXED_INTEGER_PROGRAMMING");
		long time_end = System.currentTimeMillis();
		System.out.print("\n"+(time_end - time_start)/1000.0);
	}
}