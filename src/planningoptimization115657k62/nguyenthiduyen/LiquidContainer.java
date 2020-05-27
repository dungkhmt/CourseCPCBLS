package planningoptimization115657k62.nguyenthiduyen;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.variables.IntVar;

public class test1 {

	// input
	int M = 5; // so thung
	int N = 20; // so chat long
	int[] e = { 60, 70, 80, 90, 100 }; // dung tich thung
	int[] c = { 20, 15, 10, 20, 20, 25, 30, 15, 10, 10, 20, 25, 20, 10, 30, 40, 25, 35, 10, 10 };// the tich chat long
	int[] oneM = { 1, 1, 1, 1, 1 };
	int[] oneN = { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 };
	Model model = new Model("test1");
	IntVar[][] x = new IntVar[M][N];// x[i][j] = 1 indicates that thung i chua chat long j

	public void Build() {
		model= new Model("test1");
		x= new IntVar[M][N];
		
		
		for(int i=0;i<M;i++) {
			for(int j=0;j<N;j++) {
				x[i][j]=model.intVar("x[" + i + "]["+j+"]" ,0,1);//x[i][j]=0 | 1
							
			}
		}
		
		for(int i=0;i<M;i++) {
			model.ifThen(model.arithm(x[i][0], "=", 1), model.arithm(x[i][1], "=", 0));// co chat long 0 thi khong co chat long 1
			model.ifThen(model.arithm(x[i][1], "=", 1), model.arithm(x[i][0], "=", 0));// ngc lai
			
		}
		for(int i=0;i<M;i++) {
			model.ifThen(model.arithm(x[i][7], "=", 1), model.arithm(x[i][8], "=", 0));//co chat long 7 thi khong co chat long 8
			model.ifThen(model.arithm(x[i][8], "=", 1), model.arithm(x[i][7], "=", 0));
			
		}
		for(int i=0;i<M;i++) {
			model.ifThen(model.arithm(x[i][12], "=", 1), model.arithm(x[i][17], "=", 0));//co chat long 12 thi khong chat long 17
			model.ifThen(model.arithm(x[i][17], "=", 1), model.arithm(x[i][12], "=", 0));
			
		}
		for(int i=0;i<M;i++) {
			model.ifThen(model.arithm(x[i][8], "=", 1), model.arithm(x[i][9], "=", 0));//cô chat long 8 thi khong co chat long 9
			model.ifThen(model.arithm(x[i][9], "=", 1), model.arithm(x[i][8], "=", 0));
			
		}
	    for(int i=0;i<M;i++) {
	    	Constraint c1= model.and(model.arithm(x[i][1], "=", 1),model.arithm(x[i][2], "=", 1));//co chat long 1, 2 thi khong co 9
	    	Constraint c3= model.arithm(x[i][9], "=", 0);
	    	model.ifThen(c1, c3);
	    	
	    	Constraint c2= model.and(model.arithm(x[i][2], "=", 1),model.arithm(x[i][9], "=", 1));
	    	Constraint c4= model.arithm(x[i][1], "=", 0);
	    	model.ifThen(c2, c4);
	    	
	    	Constraint c5= model.and(model.arithm(x[i][1], "=", 1),model.arithm(x[i][9], "=", 1));
	    	Constraint c6= model.arithm(x[i][2], "=", 0);
	    	model.ifThen(c5, c6);
	    }
		
	    for(int i=0;i<M;i++) {
	    	Constraint c1= model.and(model.arithm(x[i][0], "=", 1),model.arithm(x[i][9], "=", 1));//co chat long 0,9 thi khong co 12
	    	Constraint c3= model.arithm(x[i][12], "=", 0);
	    	model.ifThen(c1, c3);
	    	
	    	Constraint c2= model.and(model.arithm(x[i][0], "=", 1),model.arithm(x[i][12], "=", 1));
	    	Constraint c4= model.arithm(x[i][9], "=", 0);
	    	model.ifThen(c2, c4);
	    	
	    	Constraint c5= model.and(model.arithm(x[i][9], "=", 1),model.arithm(x[i][12], "=", 1));
	    	Constraint c6= model.arithm(x[i][0], "=", 0);
	    	model.ifThen(c5, c6);
	    	
	    }
	    // tong chat long  trong 1 thung khong vuot qua dung tich thung
	    for(int i = 0; i<M;i++) {
	    		IntVar[] y = new IntVar[N];
	    		
	    	for(int j=0; j<N;j++)  y[j] = x[i][j];
	    	for(int j=0;j<N;j++) {
	    		model.scalar(y, c, ">=", 0).post();
	            model.scalar(y, c, "<=", e[i]).post();
	    		
	    	}
	    }// chat long nam tron trong 1 thung
	    for(int j = 0; j<N;j++) {
			IntVar[] y = new IntVar[M];
		for(int i=0; i<M;i++)  y[i] = x[i][j];
		for(int i=0;i<M;i++) {
			model.scalar(y, oneM, "=", 1).post();
		}
	}
	    model.getSolver().solve();
	   //in kq
	    for(int j=0;j<N;j++) {
			System.out.print("chat long " +  j + "thuoc ");
			for(int i=0;i<M;i++) {
				if(x[i][j].getValue()==1) {
					System.out.print("thung" + i+"\n");
					
				}
			}
			
	}
	}
		public static void main(String[] args) {
			// TODO Auto-generated method stub
	       test1 app = new test1();
	       app.Build();
		}

	}



