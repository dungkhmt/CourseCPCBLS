import org.chocosolver.solver.Model;

import java.util.Scanner;

import org.chocosolver.memory.Except_0;
import org.chocosolver.memory.ICondition;
import org.chocosolver.solver.constraints.ISatFactory;
import org.chocosolver.solver.constraints.nary.automata.FA.ICostAutomaton;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.search.strategy.strategy.AbstractStrategy;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.ESat;


public class Phancongbomonhoc {
	public static void main(String[] arg) {
		//TODO Auto-generated method stub
		Model model = new Model("Example");
        
	    int N,P,L;
	    int alpha,beta,lamda,gama;
	    
	    Scanner sr = new Scanner(System.in);
	    System.out.println("nhap du lieu: ");
	    N = sr.nextInt();
	    P = sr.nextInt();
	    L = sr.nextInt();
	    
	    alpha = sr.nextInt();
	    gama = sr.nextInt();
	    lamda = sr.nextInt();
	    beta = sr.nextInt();
	    
		IntVar[][] X = new IntVar[P][N];
		//credit(i)
		int [] c = new int[N];
		for(int i=0;i < N;i++ ) {
			c[i] = sr.nextInt();
		}
		//
		int [] d1 = new int[L];
		int [] d2 = new int[L];
		for(int i=0;i < L;i++) {
			d1[i] = sr.nextInt();
			d2[i] = sr.nextInt();
		}
		
		
		for(int i =0 ;i < P ; i ++) {
			for(int j=0; j < N; j++) {
				X[i][j] =  model.intVar("X["+i+"]["+j+"]",0,1);
			}
		}
		// 
		int[] ones = new int[N];
		for(int i=0;i < N;i++) {
			ones[i] = 1;
		}
		
		for(int i=0;i < P;i++) {
			model.scalar(X[i], ones, ">=", alpha).post();
			model.scalar(X[i],ones,"<=",alpha).post();
			model.scalar(X[i],c , ">=", lamda).post();
			model.scalar(X[i], c, "<=", gama).post();
		
			
		}
		int[] ones2 =new int[P];
		for(int i=0;i < P;i++) {
			ones2[i] = 1;
		}
		for(int i=0;i < N;i++) {
			IntVar[] y = new IntVar[P];
			for(int j=0;j < P;j++) {
				y[j] = X[i][j];
			}
			model.scalar(y, ones2, "=", 1).post();
			
		}
		for(int i=0;i < L;i++) {
			for(int p = 0;p < P; p++ ) {
				for(int q=p;q < P;q++) {
					model.ifThen(model.arithm(X[q][d1[i]],"=",1), model.arithm(X[p][d2[i]],"=", 0));
				}
			}
		}
		
		for(int i=0;i < P;i++) {
			System.out.println("Hoc ky"+i+":");
			for(int j=0;j < N;j++) {
				if(X[i][j].getValue() == 1)
					System.out.print(j+" ");
				System.out.println();
			}
		}
		
	}
	

}
