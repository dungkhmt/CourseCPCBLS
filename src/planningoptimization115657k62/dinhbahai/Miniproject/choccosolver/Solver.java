package choccosolver;
//12: 472 sau 15'
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

import com.google.ortools.algorithms.main;

public class Solver {
	public int N;
	public int t0;
	public int[] e;
	public int[] I;
	public int[] r;
	public int[][] d;
	public int[][] t;
	
	public void run() {
		Model m = new Model();
		IntVar[] X = new IntVar[N+1];
		IntVar[][] Y = new IntVar[N+1][N+1];
		int[] OneN = new int[N+1];
		int infinity = java.lang.Integer.MAX_VALUE;
		for(int i=0; i<= N; i++) {
			OneN[i] = 1;
		}
		//Khoi tao x[i]: moc thoi gian giao hang cho khach hang thu i
		for(int i=0; i<= N; i++) {
			X[i] = m.intVar(e[i], I[i]);
		}
		
		//Khoi tao bien Y[i][j] nhan gia tri 0 hoac 1
		for(int i=0; i <= N; i++)
			for(int j=0; j <= N; j++)
				if(i == j)
					Y[i][j] = m.intVar(0, 0);
				else
					Y[i][j] = m.intVar(0, 1);
		
		//rang buoc
		//Moi mot diem chi co 1 luong vao va 1 luong ra
		for(int i=0; i <= N; i++) {
			m.scalar(Y[i], OneN, "=", 1).post();
		}
		for(int j=0; j <= N; j++) {
			IntVar[] temp = new IntVar[N+1];
			for(int i=0; i<= N; i++) {
				temp[i] = Y[i][j];
			}
			m.scalar(temp, OneN, "=", 1).post();
		}
		
		//Neu ton tai duong di tu i (i khac 0) ->j thi thoi gian X[i] + t[i][j] r[i] <= X[j]
		for(int i=1; i<= N; i++)
			for(int j=0; j<= N; j++) {
				m.ifThen(
						m.arithm(Y[i][j], "=", 1), 
						m.scalar(new IntVar[] {X[j],  X[i]}, new int[] {1, -1}, ">=", t[i][j]+r[i])
				);
			}
		//ngay sau t0
		for(int j=1; j<= N; j++) {
			m.ifThen(
					m.arithm(Y[0][j], "=", 1),
					m.arithm(X[j], ">=", t[0][j]+t0)
			);
		}
		//Obj
		IntVar Obj = m.intVar(0, 999999);
		IntVar temp[] = new IntVar[(N+1)*(N+1)+1];
		int CoefficientObj[] = new int[(N+1)*(N+1)+1];
		for(int i=0; i<= N; i++)
			for(int j=0; j<= N; j++) {
				temp[i*(N+1)+j] = Y[i][j];
				CoefficientObj[i*(N+1)+j] = d[i][j];
			}
		
		temp[(N+1)*(N+1)] = Obj;
		CoefficientObj[(N+1)*(N+1)] = -1;
		m.scalar(temp, CoefficientObj, "=", 0).post();
		m.setObjective(Model.MINIMIZE, Obj);
		while(1>0) {
			ArrayList<Integer> ds = new ArrayList<Integer>();
			while((m.getSolver().solve())) {
				System.out.println("Obj = " + Obj.getValue());
				ds.clear(); // xoa danh sach ban dau
				//show lo trinh, mocthoi gian
				int k = 0; 
				ds.add(k);
				System.out.print("Lo trinh: " + k + " (" + t0 + ")" +" --> ");
				for(int i=0; i<= N; i++)
					if(Y[k][i].getValue() == 1) {
						//ds.add(k);
						k = i;
						break;
					}
				while(k != 0) {
					System.out.print(k + " (" + X[k].getValue() + ")" +" --> ");
					for(int i=0; i<= N; i++)
						if(Y[k][i].getValue() == 1) {
							ds.add(k);
							k = i;
							break;
					}
				}
				System.out.print(k + " (" + X[k].getValue() + ")");
				ds.add(k);
				System.out.println();
			};
			
			//System.out.println(ds);
			if(ds.size() == 0)
				break;
			if(ds.size() == N+2)
				break;
			else {
				m.getSolver().reset();
				IntVar temp2[] = null;
				if(ds.size() > 0) {
					temp2 = new IntVar[ds.size()-1];
				
					for(int i=0; i< ds.size()-1; i++)
						temp2[i] = Y[ds.get(i)][ds.get(i+1)];
					
					int OneTemp2[] = new int[ds.size()-1];
					for(int i=0; i < ds.size()-1; i++)
						OneTemp2[i] = 1;
					
					m.scalar(temp2, OneTemp2, "<=", temp2.length-1);
				}
			}
		}
	}
	
	public void initData(String src){
		File f = new File(src);
		Scanner input = null;
		try{
			input = new Scanner(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		N = input.nextInt();
		t0 = input.nextInt();
		e = new int[N+1];
		for(int i=1; i <= N; i++)
			e[i] = input.nextInt();
		
		e[0] = t0;
		I = new int[N+1];
		for(int i=1; i <= N; i++)
			I[i] = input.nextInt();
		
		r = new int[N+1];
		for(int i=1; i <= N; i++)
			r[i] = input.nextInt();
		I[0] = e[0] + 1440;
		
		d = new int[N+1][N+1];
		for(int i=0; i<= N; i++)
			for(int j=0; j<= N; j++)
				d[i][j] = input.nextInt();
		
		t = new int[N+1][N+1];
		for(int i=0; i<= N; i++)
			for(int j=0; j<= N; j++)
				t[i][j] = input.nextInt();
		//System.out.println(t[3][3]);
	}
	
	public static void main(String[] args) {
		Solver app = new Solver();
		long millis1 = System.currentTimeMillis();
		app.initData("./data/input8.txt");
		app.run();
		long millis2 = System.currentTimeMillis();
		System.out.println(millis2 - millis1);
	}
}
