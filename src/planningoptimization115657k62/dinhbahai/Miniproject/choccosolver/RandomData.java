package choccosolver;

import java.util.Random;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class RandomData {
	public int N;
	public int t0;
	public int[] e;
	public int[] I;
	public int[] r;
	public int[][] d;
	public int[][] t;
	public void run(int N) {
		Random input = new Random();
		this.N = N;
		t0 = input.nextInt(60)+300;
		e = new int[N+1];
		for(int i=1; i <= N; i++)
			e[i] = input.nextInt(20)+480;
		
		I = new int[N+1];
		for(int i=1; i <= N; i++)
			I[i] = input.nextInt(480)+e[i]+360;
		
		r = new int[N+1];
		for(int i=1; i <= N; i++)
			r[i] = input.nextInt(15)+5;
		I[0] = I[N] + r[N];
		
		d = new int[N+1][N+1];
		for(int i=0; i< N; i++)
			for(int j=i+1; j<= N; j++) {
				d[i][j] = input.nextInt(45)+15;
				d[j][i] = d[i][j];
				d[i][i] = 0;
				d[j][j] = 0;
			}
		
		t = new int[N+1][N+1];
		for(int i=0; i< N; i++)
			for(int j=i+1; j<= N; j++) {
				t[i][j] = input.nextInt(20)+5;
				t[j][i] = t[i][j];
				t[i][i] = 0;
				t[j][j] = 0;
			}
		System.out.println(N);
		System.out.println(t0);
		for(int i=1; i<= N; i++)
			System.out.print(e[i] + " ");
		
		System.out.println();
		for(int i=1; i<= N; i++)
			System.out.print(I[i] + " ");
		
		System.out.println();
		for(int i=1; i<= N; i++)
			System.out.print(r[i] + " ");
		
		System.out.println();
		for(int i=0; i<= N; i++) {
			for(int j=0; j<= N; j++)
				System.out.print(d[i][j]+ " ");
			System.out.println();
		}
		
		for(int i=0; i<= N; i++) {
			for(int j=0; j<= N; j++)
				System.out.print(t[i][j]+ " ");
			System.out.println();
		}
	}
	public static void main(String[] args) {
		RandomData test = new RandomData();
		test.run(40);
	}

}
