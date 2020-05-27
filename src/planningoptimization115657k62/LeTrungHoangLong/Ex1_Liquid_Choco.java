package planningoptimization115657k62.LeTrungHoangLong;
import java.util.Vector;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import sun.net.www.content.audio.x_aiff;



public class Ex1_Liquid_Choco {
	
	int N=20; 
	int M=5; 
	int [] V= {20,15,10,20,20,25,30,15,10,10,
			20,25,20,10,30,40,25,35,10,10}; 
	int []L= {60,70,90,80,100}; 
	int [] ones= {1,1,1,1,1};
	int [][] C = {{1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0},
						  {0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0},
						  {0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0},
						  {0,1,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0},
						  {1,0,0,0,0,0,0,0,0,1,0,0,1,0,0,0,0,0,0,0}};
	
	
	public void solver() {
		Model model =new Model("Ex1");
		IntVar [][] X = new IntVar[M][N]; // x[i][j] = 1 chat long j nam trong thung i
		
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				X[i][j]= model.intVar("X["+i+"]["+j+"]",0,1);
			}
		}
		
		for(int i=0;i<M;i++) {
			model.scalar(X[i], V, "<=", L[i]).post();
		}
		
		for(int j=0;j<N;j++) {
			IntVar []y= new IntVar[M];
			for(int i=0;i<M;i++) {
				y[i]=X[i][j];
			}
			model.scalar(y, ones, "=", 1).post();
		}
		
		
		for(int i=0;i<M;i++) {
			for(int k=0;k<6;k++) {
				model.scalar(X[i], C[k], "<=",1 ).post();
			}
		}
		model.getSolver().solve();
		for(int i=0;i<M;i++) {
			System.out.print("thung "+i+" chua: ");
			for(int j=0;j<N;j++) {
				if(X[i][j].getValue()==1) {
					System.out.print(j+" ");
				}
			}
			System.out.println();
		}
	}
	
		
		

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Ex1_Liquid_Choco app = new Ex1_Liquid_Choco();
		
		app.solver();
	}
	
	
}
