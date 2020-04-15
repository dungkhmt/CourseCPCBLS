package com.company.appliaction;

import localsearch.model.AbstractInvariant;
import localsearch.model.IFunction;
import localsearch.model.LocalSearchManager;
import localsearch.model.VarIntLS;

import java.util.HashMap;
import java.util.HashSet;

public class Graph_PartitionCost extends AbstractInvariant implements IFunction {

    LocalSearchManager mgr;
    private VarIntLS[]x;
    private int c[][];
    private int N;
    private int value, minValue, maxValue;

    HashMap<VarIntLS, Integer> map;
    HashSet<Edge> A[];

    class Edge{
        int node;
        int w;

        public Edge(int node, int w) {
            this.node = node;
            this.w = w;
        }
    }

    public Graph_PartitionCost(VarIntLS[] x, int[][] c) {
        this.x = x;
        this.c = c;
        N = x.length;
        mgr = x[0].getLocalSearchManager();
        map = new HashMap<>();
        A = new HashSet[N];

        minValue = 0;
        maxValue = 0;
        for(int i = 0; i < N; i++){
            for(int j = 0; j < N; j++){
                maxValue += c[i][j];
            }
        }

        for(int  i = 0 ; i<N;i++){
            map.put(x[i], i);
        }

        for(int i = 0; i < N; i++){
            A[i] = new HashSet<>();
            for(int j = 0; j < N; j++){
                if(c[i][j] > 0) A[i].add(new Edge(j, c[i][j]));
            }
        }


        mgr.post(this);
    }

    @Override
    public LocalSearchManager getLocalSearchManager() {
        return mgr;
    }


    @Override
    public int getMinValue() {
        return minValue;
    }

    @Override
    public void initPropagate() {
        value = 0;
        for(int i = 0; i < N; i++){
            for(int j = i + 1; j < N; j++){
                if(x[i].getValue() != x[j].getValue() && c[i][j] > 0){
                    value +=c[i][j];
                }
            }
        }
    }

    @Override
    public void propagateInt(VarIntLS z, int val) {
        int old = z.getOldValue();
        if(map.get(z) == null || z.getValue() == old){
            return;
        }
        int u = map.get(z);

        for(Edge e : A[u]){

        }
    }

    @Override
    public int getMaxValue() {
        return maxValue;
    }

    @Override
    public int getValue() {
        return value;
    }



    @Override
    public int getAssignDelta(VarIntLS varIntLS, int i) {
        return 0;
    }

    @Override
    public int getSwapDelta(VarIntLS varIntLS, VarIntLS varIntLS1) {
        return 0;
    }
}
