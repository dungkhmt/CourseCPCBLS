package cbls115676khmt61.ngodoannghia_20162905;


import localsearch.model.ConstraintSystem;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

public class Buoi21 {
	int N; // so con hau
	
	public Buoi21(int N){
		this.N=N;
	}
	// modelling (mo hinh hoa)
	LocalSearchManager mgr;
	VarIntLS[] x;// bien quyet dinh
	ConstraintSystem S;
	public void stateModel(){
		mgr = new LocalSearchManager();
		
	}
}
