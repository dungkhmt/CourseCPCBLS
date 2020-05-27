/*
* Problem: HillClimbingSearch.java
* Description: 
* Created by ngocjr7 on [2020-03-28 21:16:09]
*/

package cbls115676khmt61.NgocBH_20164797.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class HillClimbingSearch implements LocalSearch{
    /* 
    parameters:
        rand:
        max_iter:
        verbose:
    functions:
        void minimize_objective(IFunction f, Move move_temp)
        void search_two_phase(IConstraint c, Move con_move_temp, IFunction f, Move func_move_temp)
        void minimize_objective_with_constraint(IFunction f, IConstraint c)
        void satisfy_constraint(IConstraint c)
        void minimize_objective_with_constraint(IFunction f, IConstraint c, Move move_temp)
        void satisfy_constraint(IConstraint c, Move move_temp)
    */
    private Random rand;
    private int max_iter;
    private boolean verbose;

    public HillClimbingSearch() {
        this.rand = new Random();
        this.verbose = true;
        this.max_iter = 100;
    }

    public HillClimbingSearch(int max_iter) {
        this.rand = new Random();
        this.verbose = true;
        this.max_iter = max_iter;
    }

    public HillClimbingSearch(int max_iter,int seed) {
        this.rand = new Random(seed);
        this.verbose = true;
        this.max_iter = max_iter;
    }

    public HillClimbingSearch(int max_iter,int seed, boolean verbose) {
        this.rand = new Random(seed);
        this.verbose = verbose;
        this.max_iter = max_iter;
    }

    public void set_max_iteration(int max_iter) {
        this.max_iter = max_iter;
    }

    private Move jump_by_value(IConstraint c) {
        VarIntLS[] y = c.getVariables();
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new AssignMove(0,0,false));
        int minDelta = Integer.MAX_VALUE;

        for (int i = 0; i < y.length; i++) {
            for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) 
                if ( v != y[i].getValue() ) {
                    int d = c.getAssignDelta(y[i], v);
                    if ( d < minDelta ) {
                        candidates.clear();
                        candidates.add(new AssignMove(i, v));
                        minDelta = d;
                    } else if ( d == minDelta ) {
                        candidates.add(new AssignMove(i,v));
                    }
                }
        }

        Move sel_m = candidates.get(rand.nextInt(candidates.size()));
        return sel_m;
    }

    private Move jump_by_swapping(IConstraint c) {
        VarIntLS[] y = c.getVariables();
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new SwapMove(0,0,false));
        int minDelta = Integer.MAX_VALUE;
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y.length; j++) 
                if ( y[i].getValue() != y[j].getValue() ) {
                    int d = c.getSwapDelta(y[i], y[j]);
                    if ( d < minDelta ) {
                        candidates.clear();
                        candidates.add(new SwapMove(i, j));
                        minDelta = d;
                    } else if ( d == minDelta ) {
                        candidates.add(new SwapMove(i, j));
                    }
                }
        }
        Move sel_m = candidates.get(rand.nextInt(candidates.size()));
        return sel_m;
    }

    private Move jump_by_value(IFunction f) {
        VarIntLS[] y = f.getVariables();
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new AssignMove(0,0,false));
        int minDelta = Integer.MAX_VALUE;
        for (int i = 0; i < y.length; i++) {
            for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) 
                if ( v != y[i].getValue() ) {
                    int d = f.getAssignDelta(y[i], v);
                    if ( d < minDelta ) {
                        candidates.clear();
                        candidates.add(new AssignMove(i, v));
                        minDelta = d;
                    } else if ( d == minDelta ) {
                        candidates.add(new AssignMove(i,v));
                    }
                }
        }
        Move sel_m = candidates.get(rand.nextInt(candidates.size()));
        return sel_m;
    }

    private Move jump_by_swapping(IFunction f) {
        VarIntLS[] y = f.getVariables();
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new SwapMove(0,0,false));
        int minDelta = Integer.MAX_VALUE;
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y.length; j++)
                if ( y[i].getValue() != y[j].getValue() ) {
                    int d = f.getSwapDelta(y[i], y[j]);
                    if ( d < minDelta ) {
                        candidates.clear();
                        candidates.add(new SwapMove(i, j));
                        minDelta = d;
                    } else if ( d == minDelta ) {
                        candidates.add(new SwapMove(i, j));
                    }
                }
        }
        Move sel_m = candidates.get(rand.nextInt(candidates.size()));
        return sel_m;
    }

    private Move jump_by_value(IConstraint c, IFunction f, VarIntLS[] y) {
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new AssignMove(0, y[0].getValue(), false));
        int best_dc = Integer.MAX_VALUE;
        int best_df = Integer.MAX_VALUE;

        for (int i = 0; i < y.length; i++) {
            for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) 
                if ( v != y[i].getValue() ) {
                    int dc = c.getAssignDelta(y[i], v);
                    int df = f.getAssignDelta(y[i], v);
                    if ( dc < 0 || ( dc == 0 && df < 0) ) {
                        if ( dc < best_dc || ( dc == best_dc && df < best_df) ) {
                            candidates.clear();
                            candidates.add(new AssignMove(i, v));
                            best_dc = dc;
                            best_df = df;
                        } else if ( dc == best_dc && df == best_df ) {
                            candidates.add(new AssignMove(i,v));
                        }
                    } 
            }
        }
        
        int sel_idx = rand.nextInt(candidates.size());
        Move sel_m = candidates.get(sel_idx);
        return sel_m;
    }

    private Move jump_by_swapping(IConstraint c, IFunction f, VarIntLS[] y) {
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new SwapMove(0, 0, false));
        int best_dc = Integer.MAX_VALUE;
        int best_df = Integer.MAX_VALUE;

        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y.length; j++) 
                if ( y[i].getValue() != y[j].getValue()) {
                    int dc = c.getSwapDelta(y[i], y[j]);
                    int df = f.getSwapDelta(y[i], y[j]);
                    if ( dc < 0 || ( dc == 0 && df <= 0) ) {
                        if ( dc < best_dc || ( dc == best_dc && df < best_df) ) {
                            candidates.clear();
                            candidates.add(new SwapMove(i, j));
                            best_dc = dc;
                            best_df = df;
                        } else if ( dc == best_dc && df == best_df ) {
                            candidates.add(new SwapMove(i, j));
                        }
                    } 
            }
        }
        // System.out.printf("%d %d %d\n",candidates.size(),best_df,best_dc);
        int sel_idx = rand.nextInt(candidates.size());
        Move sel_m = candidates.get(sel_idx);
        return sel_m;
    }

    void init_solution(VarIntLS[] x) {
        for (int i = 0; i < x.length; ++i) {
            final int v = this.rand.nextInt(x[i].getMaxValue() - x[i].getMinValue() + 1) + x[i].getMinValue();
            x[i].setValuePropagate(v);
        }
    }
    
    public void satisfy_constraint(IConstraint c, Move move_temp) {
        int it = 0;
        VarIntLS[] y = c.getVariables();
        init_solution(y);
        
        while ( it < max_iter && c.violations() > 0 ) {
            if (verbose) 
                System.out.printf("iteration %d: violations = %d\n",it, c.violations());

            if (move_temp instanceof AssignMove) {
                Move sel_m = jump_by_value(c);
                AssignMove sel_am = (AssignMove)sel_m;
                if ( !sel_am.is_legal ) break; 
                y[sel_am.i].setValuePropagate(sel_am.v);
            } else if ( move_temp instanceof SwapMove) {
                Move sel_m = jump_by_swapping(c);
                SwapMove sel_sm = (SwapMove) sel_m;
                if ( !sel_sm.is_legal ) break; 
                y[sel_sm.u].swapValuePropagate(y[sel_sm.v]);
            }
            it++;
        }
        if (verbose) 
            System.out.printf("Reach local optimum: violations = %d\n", c.violations());
    }

    public void minimize_objective_with_constraint(IFunction f, IConstraint c, Move move_temp) {
        int it = 0;
        Set<VarIntLS> varset = new HashSet<VarIntLS>();
        varset.addAll(Arrays.asList(f.getVariables()));
        varset.addAll(Arrays.asList(c.getVariables()));
        VarIntLS[] y = new VarIntLS[varset.size()];
        varset.toArray(y);
        init_solution(y);

        while ( it < max_iter ) {
            if (verbose) 
                System.out.printf("iteration %d: objective = %d and violations = %d\n", it, f.getValue(), c.violations());
            // for (int i = 0; i < y.length; i++) {
            //     System.out.printf("%d ", y[i].getValue());
            // }
            if (move_temp instanceof AssignMove) {
                Move sel_m = jump_by_value(c, f, y);
                AssignMove sel_am = (AssignMove)sel_m;
                if ( !sel_am.is_legal ) 
                    break; 
                y[sel_am.i].setValuePropagate(sel_am.v);
            } else if ( move_temp instanceof SwapMove) {
                Move sel_m = jump_by_swapping(c, f, y);
                SwapMove sel_sm = (SwapMove) sel_m;
                if ( !sel_sm.is_legal ) 
                    break; 
                y[sel_sm.u].swapValuePropagate(y[sel_sm.v]);
            }
            it++;
        }
        if (verbose) 
            System.out.printf("Reach local optimum: objective = %d and violations = %d\n", f.getValue(), c.violations());
    }

    public void satisfy_constraint(IConstraint c) {
        int it = 0;
        VarIntLS[] y = c.getVariables();
        init_solution(y);

        while ( it < max_iter && c.violations() > 0 ) {
            if (verbose) 
                System.out.printf("iteration %d: violations = %d\n", it, c.violations());
            
            Move sel_m = jump_by_value(c);
            
            if (sel_m instanceof AssignMove) {
                AssignMove sel_am = (AssignMove)sel_m;
                if ( !sel_am.is_legal ) break; 
                y[sel_am.i].setValuePropagate(sel_am.v);
            } else if ( sel_m instanceof SwapMove) {
                SwapMove sel_sm = (SwapMove) sel_m;
                if ( !sel_sm.is_legal ) break; 
                y[sel_sm.u].swapValuePropagate(y[sel_sm.v]);
            }
            it++;
        }
        if (verbose) 
            System.out.printf("Reach local optimum: violations = %d\n", c.violations());
    }

    public void minimize_objective_with_constraint(IFunction f, IConstraint c) {
        int it = 0;
        Set<VarIntLS> varset = new HashSet<VarIntLS>();
        varset.addAll(Arrays.asList(f.getVariables()));
        varset.addAll(Arrays.asList(c.getVariables()));
        VarIntLS[] y = new VarIntLS[varset.size()];
        varset.toArray(y);
        init_solution(y);

        while ( it < max_iter ) {
            if (verbose) 
                System.out.printf("iteration %d: objective = %d and violations = %d\n",it, f.getValue(), c.violations());

            Move sel_m = jump_by_value(c, f, y);
            if (sel_m instanceof AssignMove) {
                AssignMove sel_am = (AssignMove)sel_m;
                if ( !sel_am.is_legal ) 
                    break; 
                y[sel_am.i].setValuePropagate(sel_am.v);
            } else if ( sel_m instanceof SwapMove) {
                SwapMove sel_sm = (SwapMove) sel_m;
                if ( !sel_sm.is_legal ) 
                    break; 
                y[sel_sm.u].swapValuePropagate(y[sel_sm.v]);
            }
            it++;
        }
        if (verbose) 
            System.out.printf("Reach local optimum: objective = %d and violations = %d\n", f.getValue(), c.violations());
    }

    public void search_two_phase(IConstraint c, Move con_move_temp, IFunction f, Move func_move_temp) {
        System.out.println("Start phase 1: Satisfy constraint");
        satisfy_constraint(c, con_move_temp);
        System.out.println("Start phase 2: Minimize objective function and maintain constraint violations");
        minimize_objective_with_constraint(f, c, func_move_temp);
    }
    
    public void minimize_objective(IFunction f, Move move_temp) {
        int it = 0;
        VarIntLS[] y = f.getVariables();
        init_solution(y);

        while ( it < max_iter ) {
            if (verbose) 
                System.out.printf("iteration %d: objective = %d\n",it, f.getValue());
            
            if (move_temp instanceof AssignMove) {
                Move sel_m = jump_by_value(f);
                AssignMove sel_am = (AssignMove)sel_m;
                if (!sel_am.is_legal) break;
                y[sel_am.i].setValuePropagate(sel_am.v);
            } else if ( move_temp instanceof SwapMove) {
                Move sel_m = jump_by_swapping(f);
                SwapMove sel_sm = (SwapMove) sel_m;
                if (!sel_sm.is_legal) break;
                y[sel_sm.u].swapValuePropagate(y[sel_sm.v]);
            }
            it++;
        }
        if (verbose) 
            System.out.printf("Reach local optimum: objective final = %d\n", f.getValue());
    }

    public static void main(String[] args) {

    }
}