package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.Solver;

public class Liquid {
	int can[] = {60, 70, 90, 80, 100};
	int liqiud[] = {20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10};
	int c[][]= {{0,1},{7,8},{12,17}, {8,9}};
	int[][] conflict;
	int oneC[] = {1,1,1,1,1};
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Liquid app = new Liquid();
		app.conflict();
		app.solve();
	}
	private void conflict() {

		conflict = new int[20][20];
		for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
            	conflict[i][j] = 0;
            }}
		for(int i = 0; i < 4; i++) {
			conflict[c[i][0]][c[i][1]] = 1;
			conflict[c[i][1]][c[i][0]] = 1;
		}
	}
	public void solve() {
		Model model = new Model();
		IntVar[][] X = new IntVar[5][20];
		for(int j = 0; j < 20; j++)
			  for(int i = 0; i < 5; i++)
			    X[i][j] = model.intVar("X[" + i + "," + j+ "]",0,1);
		for(int i = 0; i < 19; i++) {
			for(int j = i+1; j < 20; j++) {
				for(int k = 0; k < 5; k++)
				if(conflict[i][j] == 1) {
					model.ifThen(model.arithm(X[k][i], "=", 1), 
                            model.arithm(X[k][j], "=", 0));}
			}
		}
		
		for(int i = 0; i < 5; i++) {
			model.scalar(X[i], liqiud, "<=", can[i]).post();;
		}
		for (int i = 0; i < 5; i++) {
			Constraint c1 = model.and(model.arithm(X[i][1], "=", 1), model.arithm(X[i][2], "=", 1));
			model.ifThen(c1,model.arithm(X[i][9], "=", 0));
			c1 = model.and(model.arithm(X[i][1], "=", 1), model.arithm(X[i][9], "=", 1));
			model.ifThen(c1,model.arithm(X[i][2], "=", 0));
			c1 = model.and(model.arithm(X[i][9], "=", 1), model.arithm(X[i][2], "=", 1));
			model.ifThen(c1,model.arithm(X[i][1], "=", 0));
			c1 = model.and(model.arithm(X[i][0], "=", 1), model.arithm(X[i][9], "=", 1));
			model.ifThen(c1,model.arithm(X[i][12], "=", 0));
			c1 = model.and(model.arithm(X[i][0], "=", 1), model.arithm(X[i][12], "=", 1));
			model.ifThen(c1,model.arithm(X[i][9], "=", 0));
			c1 = model.and(model.arithm(X[i][9], "=", 1), model.arithm(X[i][12], "=", 1));
			model.ifThen(c1,model.arithm(X[i][0], "=", 0));
		}
		for(int i = 0; i < 20; i++)	{
			IntVar []y = new IntVar[5];
			for(int j = 0; j < 5; j++) {
				y[j] = X[j][i];
			}
			model.scalar(y, oneC, "=", 1).post();
		}	  
		model.getSolver().solve();
		for(int i = 0; i < 5; i++) {
			System.out.println();
			System.out.print("Thung  "+ i +": ");
		for(int j = 0; j < 20; j++)
			   {
				  if(X[i][j].getValue() == 1) {
					  System.out.print(j + " ");
				  }
				  
			  }}
	}
}
