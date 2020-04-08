package cbls115676khmt61.phamquangdung;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class GraphPartitioningCP {
	/*
	private int N = 10;
	private int[][] E = {
			{1,2,8},
			{1,3,2},
			{1,7,3},
			{2,3,8},
			{2,4,7},
			{2,7,4},
			{2,8,6},
			{0,2,5},
			{3,5,1},
			{3,8,5},
			{4,6,8},
			{4,7,9},
			{0,4,1},
			{5,9,5},
			{5,8,4},
			{6,9,4},
			{0,6,7},
			{0,8,2},
			{0,9,8}
	};
	*/
	int N = 6;
	int[][] E = {
			{0,3,1},
			{0,4,1},
			{1,2,1},
			{1,5,1},
			{2,5,1},
			{3,4,1}
	};
	public void solve(){
		int[] c = new int[E.length];
		int C = 0;
		for(int i = 0; i < E.length; i++){
			c[i] = E[i][2];
			C += c[i];
		}
		
		Model model = new Model("graph partitioning");
		IntVar[] z = new IntVar[E.length];
		IntVar[] x = new IntVar[N];
		
		IntVar obj = model.intVar("obj",0,C);
		for(int k = 0; k < E.length; k++){
			z[k] = model.intVar("z[" + k + "]",0,1);
		}
		for(int i=0; i < N; i++){
			x[i] = model.intVar("x[" + i + "]",0,1);
		}
		for(int k = 0; k < E.length; k++){
			model.ifThen(model.arithm(z[k], "=", 1), model.arithm(x[E[k][0]], "+", x[E[k][1]],"=",1));
			model.ifThen(model.arithm(x[E[k][0]], "+", x[E[k][1]],"=",1),model.arithm(z[k], "=", 1));
		}
		int[] one = new int[N];
		for(int i = 0; i < N; i++) one[i] = 1;
		model.scalar(x, one, "=",N/2).post();
		
		model.scalar(z, c, "=",obj).post();
		
		//model.setObjective(model.MINIMIZE, obj);
		model.setObjective(model.MAXIMIZE, obj);
		model.getSolver().solve();
		model.getSolver().printStatistics();
		
		for(int i = 0; i < N; i++){
			System.out.println("x[" + i + "] = " + x[i].getValue());
		}
		for(int k = 0; k < E.length; k++){
			System.out.println("z[" + E[k][0] + "," + E[k][1] + "] = " + z[k].getValue());
		}
		System.out.print("X = ");
		for(int i = 0; i < N; i++) if(x[i].getValue() == 0) System.out.print(i + " ");
		System.out.println();
		System.out.print("Y = ");
		for(int i = 0; i < N; i++) if(x[i].getValue() == 1) System.out.print(i + " ");
		System.out.println();
		for(int k = 0; k < E.length; k++)if(z[k].getValue() == 1)
			System.out.println("c[" + E[k][0] + "," + E[k][1] + "] = " + c[k]);
		System.out.println("obj = " + obj.getValue());
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GraphPartitioningCP app = new GraphPartitioningCP();
		app.solve();
	}

}
