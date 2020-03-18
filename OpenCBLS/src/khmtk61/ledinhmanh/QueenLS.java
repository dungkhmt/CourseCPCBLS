package khmtk61.ledinhmanh;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Random;

public class QueenLS {

    int N;
    int [] X;
    Random R = new Random();

    private void generateInit(){
        for (int i = 1; i< N+1; i++)
            X[i] = R.nextInt(N) + 1;
    }

    int evaluate(int i, int v){
        int olvV = X[i];
        X[i] = v;
        int newviolation = violation();
        X[i] = olvV;
        return newviolation;
    }

    private int violation(){
        int v = 0;
        for (int i = 1; i < N +1; i++){
            for (int j = i + 1; j < N+1; j++){
                if (X[i] == X[j]) v++;
                if(X[i] + i == X[j] + j) v++;
                if(X[i] - i == X[j] - j) v++;
            }
        }
        return v;
    }

    private void localSearch() {
        generateInit();
        int it = 1;
        int v = violation();
        ArrayList<Pair <Integer, Integer>> cand = new ArrayList<Pair<Integer, Integer>>();
        while(it<=10000 && v > 0){
            int minE = Integer.MAX_VALUE;
            for (int i =1; i <= N; i++){
                for (int val = 1; val < N +1; val++){
                    int e = evaluate(i, val);
                    if(e < minE){
                        minE = e;
                        cand.clear();
                        cand.add(new Pair<Integer, Integer>(i, val));
                    } else if ( e == minE) {
                        cand.add(new Pair<Integer, Integer>(i, val));
                    }
                }
            }
            int idx = R.nextInt(cand.size());
            Pair<Integer, Integer> p = cand.get(idx);
            X[p.getKey()] = p.getValue();
            v = violation();
            System.out.println("Step " + it + ", new violation = " + v);
            it++;
        }
    }

    public void solve(int N){
        this.N = N;
        X = new int[N+1];
        localSearch();
    }

    public static void main(String[] args) {
        QueenLS q = new QueenLS();
        q.solve(100);
    }

}
