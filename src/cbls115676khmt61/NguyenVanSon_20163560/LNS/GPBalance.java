package cbls115676khmt61.NguyenVanSon_20163560.LNS;

public class GPBalance implements IGPConstraint {

	int[] Z;
	boolean[] instantiated;
	
	public GPBalance(int[] Z, boolean[] instantiated){
		this.Z = Z;this.instantiated = instantiated;
	}
	
	@Override
	public boolean check() {
		// TODO Auto-generated method stub
		//for(int i = 0; i < instantiated.length; i++)
		//	if(instantiated[i] == false) return true;
	
		int cnt = 0;
		for(int i = 0; i < Z.length; i++) cnt += Z[i];
		//for(int i = 0; i < Z.length; i++) System.out.print(Z[i] + " "); System.out.println(", cnt = " + cnt);
		return cnt == Z.length/2;
	}

}
