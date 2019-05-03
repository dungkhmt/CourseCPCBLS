package khmtk60.app.BCA;

import choco.Choco;
import choco.Options;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.variables.integer.IntegerVariable;

public class BCA {
	int N;
	int M;
	int[] t;
	int alpha;
	int beta;
	
	int[] I;// (I[k], J[k]) conflict
	int[] J;
	// model variables
	IntegerVariable[][] Y;
	IntegerVariable f;
	
	public void solve(){
		CPModel m = new CPModel();
		Y = new IntegerVariable[N][M];
		for(int i = 0; i < N; i++)
			for(int j = 0; j < M; j++)
				Y[i][j] = Choco.makeIntVar("Y[" + i + "," + j + "]", 0,1);
		f = Choco.makeIntVar("obj", 0,N, Options.V_OBJECTIVE);
		
		for(int j = 0; j < M; j++){
			IntegerVariable[] z = new IntegerVariable[N];
			for(int i = 0; i < N; i++) z[i] = Y[i][j];
			
			m.addConstraint(Choco.eq(Choco.sum(z), 1));
			
			m.addConstraint(Choco.leq(Choco.scalar(z,t), beta));
			m.addConstraint(Choco.geq(Choco.scalar(z,t), alpha));
		
			m.addConstraint(Choco.leq(Choco.scalar(z,t), f));
		}		
		for(int k = 0; k < I.length; k++){
			for(int j = 0; j < M; j++){
				m.addConstraint(Choco.leq(Choco.plus(Y[I[k]][j], Y[J[k]][j]), 1));
			}
		}
		
		CPSolver s = new CPSolver();
		s.read(m);		
		s.minimize(true);
		
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
