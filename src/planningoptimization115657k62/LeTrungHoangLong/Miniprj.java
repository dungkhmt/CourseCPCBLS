package planningoptimization115657k62.LeTrungHoangLong;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

import sun.net.www.content.audio.x_aiff;

public class Miniprj {
	int N = 3; //number of packages
	int M = 4;
	int H = 6;
	int[] Num= {1,1};
	int[] Teach= {1};
	int[] Load= {2};
	int Sits[]= {1,2};
	int First[]= {1,1};
	int[] w = { 3, 3, 1};
	int[] h = { 2, 4, 6};
	
	public void solver() {
		Model model =new Model("Timetable");
		IntVar X[]= new IntVar[N];
		IntVar Y[]= new IntVar[N];
		IntVar Z[]= new IntVar[N];
		for(int j=0;j<N;j++) {
			X[j]= model.intVar("x["+j+"]",0,M-1);
			Y[j]= model.intVar("y["+j+"]",0,9);
			Z[j]= model.intVar("z["+j+"]",1,6);
			
		}
		
		for(int i=0;i<N;i++) {
			for(int j=0;j<M;j++) {
				if(Num[i]>Sits[j]) {
					Constraint c1= model.arithm(X[i], "!=", j);
				}
				Constraint c4=model.arithm(var, op, cste);
				
			}
		}
		
		
		
		for(int i=0;i<N;i++) {
			for(int j=0;j<N && i!=j;j++) {
				
				
				if(Load[i]+Load[j]>6) {
					Constraint c2= model.arithm(Y[i],"!=",Y[j]);
					
				}
				
				if(Teach[i]==Teach[j]) {
					Constraint c3= model.or(model.arithm(Y[i], "!=", Y[j]),model.arithm(var, op, cste));
				}
			}
		} 
		
		
		while (model.getSolver().solve()) {
			System.out.println("Solution " + j++ + " found : ");
			for (int i = 0; i < N; i++) {
				System.out.println(p[i][0].getValue()+", " + p[i][1].getValue() + ", " +p[i][2].getValue());
			}
        }
		System.out.println("sdfa");
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Miniprj app = new Miniprj();
		
		app.solver();
	}

}
