package khmtk61.ledinhmanh;

public class Queen {
    int N;
    int[] X;
    boolean found;

    private boolean check(int v, int k){
        for (int i = 1 ; i < k; i++) {
            if (X[i] == v) return false;
            if (X[i]+i == v+k) return false;
            if (X[i]-i == v-k) return  false;
        }
        return true;
    }

    private  void Solution(){
        for (int i =1; i< N+1; i++){
            System.out.print(X[i] + " ");
            System.out.println();
        }
    }

    private void TRY (int k){
        for (int v = 1; v < N+1; v ++){
            if (found) return;
            if (check(v, k)){
                X[k] = v;
                if(k == N) {
                    found = true;
                    Solution();
                }
                else TRY(k+1);
            }
        }
    }

    public void Solve(int n){
        this.N=  n;
        X = new int[n+1];
        found = false;
        TRY(1);
    }

    public static void main(String[] args) {
        Queen q= new Queen();
        q.Solve(5);
    }
}
