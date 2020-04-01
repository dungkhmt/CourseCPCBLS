package test;

import java.util.Scanner;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.IntVar;

public class BinPacking {
	public int N,W,H;
	public int[] h, w;
	Model model;
	public IntVar[] x,y,o;
	
	public void input(){
		Scanner inp = new Scanner(System.in);
		System.out.println("Nhap du lieu:");
		N = inp.nextInt();
		W = inp.nextInt();
		H = inp.nextInt();
		
		w = new int[N+10];
		h = new int[N+10];
		for(int i=1;i<=N;i++){
			w[i] = inp.nextInt();
			h[i] = inp.nextInt();
		}
		inp.close();
	}
	
	public void init(){
		model = new Model("BinPacking");
		x = new IntVar[N+10];
		y = new IntVar[N+10];
		o = new IntVar[N+10];
		for(int i=1;i<=N;i++){
			o[i]=model.intVar("o"+i, 0, 1);
			x[i]=model.intVar("x"+i, 1, W);
			y[i]=model.intVar("y"+i, 1, H);
		}
	}
	
	public void createConstraint(){
		for(int i=1;i<=N;i++){
			Constraint c1=model.arithm(model.intOffsetView(x[i], w[i]),"<=", W);
			Constraint c2=model.arithm(model.intOffsetView(y[i], h[i]), "<=", H);
			model.ifThen(model.arithm(o[i], "=", 0), model.and(c1,c2));
			
			Constraint c3=model.arithm(model.intOffsetView(x[i], h[i]),"<=", W);
			Constraint c4=model.arithm(model.intOffsetView(y[i], w[i]), "<=", H);
			model.ifThen(model.arithm(o[i], "=", 0), model.and(c3,c4));
		}
		
		for(int i=1;i<N;i++){
			for(int j=i+1;j<N;j++){
				Constraint c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 0));
				Constraint c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				Constraint c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				Constraint c4 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
				Constraint c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c5, c2, c3, c4));
				
				c1 = model.and(model.arithm(o[i], "=", 0), model.arithm(o[j], "=", 1));
				c2 = model.arithm(model.intOffsetView(x[i], w[i]), "<=", x[j]);
				c3 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				c4 = model.arithm(model.intOffsetView(y[i], h[i]), "<=", y[j]);
				c5 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c5, c2, c3, c4));
				
				c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 0));
				c2 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				c3 = model.arithm(model.intOffsetView(x[j], w[j]), "<=", x[i]);
				c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
				c5 = model.arithm(model.intOffsetView(y[j], h[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c5, c2, c3, c4));
				
				c1 = model.and(model.arithm(o[i], "=", 1), model.arithm(o[j], "=", 1));
				c2 = model.arithm(model.intOffsetView(x[i], h[i]), "<=", x[j]);
				c3 = model.arithm(model.intOffsetView(x[j], h[j]), "<=", x[i]);
				c4 = model.arithm(model.intOffsetView(y[i], w[i]), "<=", y[j]);
				c5 = model.arithm(model.intOffsetView(y[j], w[j]), "<=", y[i]);
				model.ifThen(c1, model.or(c5, c2, c3, c4));
			}
		}
	}
	
	public void solve(){
		input();
		System.out.println("Done input");
		init();
		createConstraint();
		Solver s = model.getSolver();
		System.out.println("---------");
		while(s.solve()){
			for(int i=1;i<=N;i++){
				System.out.println("Pack" + i +":" + x[i].getValue() +","+ y[i].getValue() +","+ o[i].getValue());
			}
			System.out.println("---------");
		}
	}

	public static void main(String[] args){
		BinPacking app = new BinPacking();
		app.solve();
	}
}
