package cbls115676khmt61.phamquangdung;

public class LinearX1ImplyY1 {
	int[] z = new int[7];
	
	private boolean check(int v, int k){
		if(k == 2){
			
		}else if(k == 3){
			if(v < 0) return false;
			if(z[2] > v) return false;
			if(2*z[1] > v) return false;
			if(2*z[1] + z[2] > v) return false;
			if(z[1] + z[2] > v) return false;
			if(z[1] <= v) return false;
		}else if(k == 4){
			
		}else if(k==5){
			
		}else if(k==6){
			if(v < 0) return false;
			if(z[5] > v) return false;
			if(2*z[4] > v) return false;
			if(2*z[4] + z[5] > v) return false;
			if(z[4] + z[5] > v) return false;
			if(z[1] <= z[3] && z[4] <= v) return false;
		}
		return true;
	}
	private void solution(){
		for(int i = 1; i <= 6; i++) System.out.println("z[" + i + "] = " + z[i]);
		System.out.println();
	}
	private void TRY(int k){
		//for(int i = 1; i <= k-1; i++) System.out.print(z[i] + ","); System.out.println();
		for(int v = -10; v <= 10; v++){
			if(check(v,k)){
				z[k] = v;
				if(k == 3) solution();
				else TRY(k+1);
			}
		}
	}
	public void solve(){
		TRY(1);
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LinearX1ImplyY1 app = new LinearX1ImplyY1();
		app.solve();
	}

}
