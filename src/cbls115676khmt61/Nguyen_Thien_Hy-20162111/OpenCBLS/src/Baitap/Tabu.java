/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Baitap;

import java.util.*;
import localsearch.model.*;

/**
 *
 * @author hydon
 */
public class Tabu {
    
    class AssignMove{
        int i;
        int v;
        public AssignMove(int i , int v){
            this.i = i;
            this.v = v;
        }
    }
    
    VarIntLS[] X;
    IConstraint c;
    
    int tabu[][];
    int tbl; // độ dài danh sách tabu
    
    int nic = 0; // quyết định xem có nên khởi động lại quá trình tìm kiếm
    int maxStable; // giá trị tối đa cho nic
    
    int D;// max Domain
    int N;// số lượng biến
    
    int bestViolations;
    Random R = new Random();
    
    public Tabu(IConstraint c){
        this.c = c;
        X = c.getVariables();
        N = X.length;
        D = 0;
        for(int i = 0 ; i < X.length ; i ++){
            if(D < X[i].getMaxValue()){
                D = X[i].getMaxValue();
            }
            else{
                D = D;
            }
        }
        tabu = new int[N][D + 1];
        for(int i = 0 ; i < N ; i ++){
            for(int j = 0 ; j <= D ; j ++){
                tabu[i][j] = -1;
            }
        }
    }
    
    private void restart(){
        for(int i = 0 ; i < N ; i ++){
            int v = R.nextInt(X[i].getMaxValue() - X[i].getMinValue() + 1)
                    + X[i].getMinValue();
            X[i].setValuePropagate(v);
        }
        if(c.violations() < bestViolations){
            bestViolations = c.violations();
        }
    }
    
    public void search(int maxIter , int tblen , int maxStable){
        this.tbl = tblen;
        bestViolations = c.violations();
        ArrayList<AssignMove> cand = new ArrayList<>();
        nic = 0;
        int it = 0;
        while(it <= maxIter && bestViolations > 0){
            int minDelta = Integer.MAX_VALUE;
            cand.clear();
            for(int i = 0 ; i < N ; i ++){
                for(int v = X[i].getMinValue() ; v <= X[i].getMaxValue() ; v ++){
                    if(X[i].getValue() != v){
                        int delta = c.getAssignDelta(X[i], v);
                        if(tabu[i][v] <= it || delta + c.violations() < bestViolations){
                            if(delta < minDelta){
                                cand.clear();
                                cand.add(new AssignMove(i, v));
                                minDelta = delta;
                            }
                            else if(delta == minDelta){
                                cand.add(new AssignMove(i, v));
                            }
                        }
                    }
                }
            }
            
            AssignMove m = cand.get(R.nextInt(cand.size()));
            X[m.i].setValuePropagate(m.v);
            tabu[m.i][m.v] = it + tbl;
            if(c.violations() < bestViolations){
                bestViolations = c.violations();
                nic = 0;
            }
            else{
                nic ++;
                if(nic >= maxStable){
                    restart();
                }
            }
            System.out.println("Step " + it + " violations = " + c.violations()
                    + ", bestViolations = " + bestViolations);
            it++;
        }
    }
}
