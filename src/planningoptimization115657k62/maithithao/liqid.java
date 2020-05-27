

import org.chocosolver.solver.Model;

import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;




public class liqid {
 
	int N = 5;
	int M = 20;
	int[] e = {60,70,90,80,100};
	int[] c = {20,15,10,20,20,25,30,15,10,10,20,25,20,10,30,40,25,35,10,10};
	int[] d= {1,1,1,1,1};
	
	Model model;
	IntVar[][] x;
	
public void solve() {
	model= new Model("test1");
	x= new IntVar[N][M];
	
	
	for(int i=0;i<N;i++) {
		for(int j=0;j<M;j++) {
			x[i][j]=model.intVar("x[" + i + "]["+j+"]" ,0,1);
			// x[i][j]=1 chat long j thuoc thung i
		}
	}
	
	for(int i=0;i<N;i++) {
		model.ifThen(model.arithm(x[i][0], "=", 1), model.arithm(x[i][1], "=", 0));
		model.ifThen(model.arithm(x[i][1], "=", 1), model.arithm(x[i][0], "=", 0));
		
	}
	for(int i=0;i<N;i++) {
		model.ifThen(model.arithm(x[i][7], "=", 1), model.arithm(x[i][8], "=", 0));
		model.ifThen(model.arithm(x[i][8], "=", 1), model.arithm(x[i][7], "=", 0));
		
	}
	for(int i=0;i<N;i++) {
		model.ifThen(model.arithm(x[i][12], "=", 1), model.arithm(x[i][17], "=", 0));
		model.ifThen(model.arithm(x[i][17], "=", 1), model.arithm(x[i][12], "=", 0));
		
	}
	for(int i=0;i<N;i++) {
		model.ifThen(model.arithm(x[i][8], "=", 1), model.arithm(x[i][9], "=", 0));
		model.ifThen(model.arithm(x[i][9], "=", 1), model.arithm(x[i][8], "=", 0));
		
	}
	
    for(int i=0;i<N;i++) {
    	Constraint c1= model.and(model.arithm(x[i][1], "=", 1),model.arithm(x[i][2], "=", 1));
    	Constraint c3= model.arithm(x[i][9], "=", 0);
    	model.ifThen(c1, c3);
    	
    	Constraint c2= model.and(model.arithm(x[i][2], "=", 1),model.arithm(x[i][9], "=", 1));
    	Constraint c4= model.arithm(x[i][1], "=", 0);
    	model.ifThen(c2, c4);
    	
    	Constraint c5= model.and(model.arithm(x[i][1], "=", 1),model.arithm(x[i][9], "=", 1));
    	Constraint c6= model.arithm(x[i][2], "=", 0);
    	model.ifThen(c5, c6);
    }
	
    for(int i=0;i<N;i++) {
    	Constraint c1= model.and(model.arithm(x[i][0], "=", 1),model.arithm(x[i][9], "=", 1));
    	Constraint c3= model.arithm(x[i][12], "=", 0);
    	model.ifThen(c1, c3);
    	
    	Constraint c2= model.and(model.arithm(x[i][0], "=", 1),model.arithm(x[i][12], "=", 1));
    	Constraint c4= model.arithm(x[i][9], "=", 0);
    	model.ifThen(c2, c4);
    	
    	Constraint c5= model.and(model.arithm(x[i][9], "=", 1),model.arithm(x[i][12], "=", 1));
    	Constraint c6= model.arithm(x[i][0], "=", 0);
    	model.ifThen(c5, c6);
    	
    }
    
    for(int i = 0; i<N;i++) {
    		
    		IntVar[] y = new IntVar[M];
    	for(int j=0; j<M;j++)  y[j] = x[i][j];
    	for(int j=0;j<M;j++) {
    		model.scalar(y, c, ">=", 0).post();
            model.scalar(y, c, "<=", e[i]).post();
    		
    	}
    }
    
    for(int j = 0; j<M;j++) {
		
		IntVar[] y = new IntVar[N];
	for(int i=0; i<N;i++)  y[i] = x[i][j];
	for(int i=0;i<N;i++) {
		model.scalar(y, d, "=", 1).post();
        
		
	}
}
    model.getSolver().solve();

    for(int i=0;i<N;i++) {
		System.out.print("thung " +  i + " include ");
		for(int j=0;j<M;j++) {
			if(x[i][j].getValue()==1) {
				System.out.print(" chat long " + j);
				
				
			}
		}
		System.out.println();
	}
}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
       liqid app = new liqid();
       app.solve();
	}

}