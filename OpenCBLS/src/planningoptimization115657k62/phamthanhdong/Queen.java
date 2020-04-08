import java.util.*;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.IntVar;

public class Queen {
	
	
	public void solution(int n) {
		Model m = new Model();
		IntVar[] x = m.intVarArray(n, 0, n - 1);
		IntVar[] c1 = new IntVar[n];
		IntVar[] c2 = new IntVar[n];

		for (int i = 0; i < n; i++) {
			c1[i] = m.intOffsetView(x[i], i);
			c2[i] = m.intOffsetView(x[i], -i);
		}

		m.allDifferent(x).post();
		m.allDifferent(c1).post();
		m.allDifferent(c2).post();

		m.getSolver().solve();
		
		for (int i = 0; i < n; i++) 		
			System.out.println("X[" + i + "] = " + x[i].getValue());
		
		//in bàn cờ :D
		for(int i = 0;i < n ;i++)
			System.out.print(" _");
		
		System.out.println("");
		for(int i = 0;i < n ;i++)
		{
			for(int j = 0;j<n;j++)
			{
				if(j == ((int) x[i].getValue() ))
					System.out.print("|x̲");
				else
					System.out.print("|_");		
			}
			System.out.println("|");
		}
		
	}

	public static void main(String[] args)
	{
		Queen exam = new Queen();
		exam.solution(8);
	}
}

/*
 * public class Queen { int n; int[] X; private void geneSolution() { }
 * 
 * private int violation() { int v = 0; for(int i = 1;i<=n-1;i++) for(int j =
 * i+1;j<=n;j++) { if(X[i]==X[j]) v++; if(X[i]+i==X[j]+j) v++; if(X[i
 * -i]==X[j]-j) v++;
 * 
 * } return v; }
 * 
 * //private int evaluate(int i,int v) {
 * 
 * } public static void main(String[] args) { // TODO Auto-generated method stub
 * 
 * }
 * 
 * }
 */
