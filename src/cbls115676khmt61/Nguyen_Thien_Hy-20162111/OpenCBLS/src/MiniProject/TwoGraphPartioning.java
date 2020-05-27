/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MiniProject;

import java.util.*;
import localsearch.constraints.basic.Implicate;
import localsearch.constraints.basic.IsEqual;
import localsearch.functions.sum.Sum;
import localsearch.model.*;

/**
 *
 * @author hydon
 */
public class TwoGraphPartioning {
    private int N = 6;
    private int[][] E = {
        {0, 3, 1},
        {0, 4, 1},
        {1, 2, 1},
        {1, 5, 1},
        {2, 5, 1},
        {3, 4, 1}
    };
    
    private int[][] Z = new int[N][N];
    
    private LocalSearchManager mgr;
    private VarIntLS[] x; // x[i] = 1 means i belongs to X
    private ConstraintSystem S;
    
    public void stateModel(){
        mgr = new LocalSearchManager();
        x = new VarIntLS[N];
        for(int i = 0 ; i < N ; i ++){
            x[i] = new VarIntLS(mgr, 0 , 1);
        }
        S = new ConstraintSystem(mgr);
        S.post(new IsEqual(new Sum(x), N / 2));
        
        mgr.close();
    }
}
