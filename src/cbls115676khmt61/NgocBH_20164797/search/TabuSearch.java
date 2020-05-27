/*
// Filename: TabuSearch.java
// Description:
// Created by ngocjr7 on [15-04-2020 15:09:07]
*/
package cbls115676khmt61.ngocbh_20164797.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;
import localsearch.model.VarIntLS;

public class TabuSearch implements LocalSearch {
    /* 
    parameters:
        rand:
        max_iter:
        verbose:
    functions:
        void minimize_objective(IFunction f, Move move_temp)
        void search_two_phase(IConstraint c, Move con_move_temp, IFunction f, Move func_move_temp)
        void minimize_objective_with_constraint(IFunction f, IConstraint c, Move move_temp)
        void satisfy_constraint(IConstraint c, Move move_temp)
    */
    private Random rand;
    private boolean verbose;
    private int max_iter;
    private int tabu_size;
    private HashMap<Move, Integer> tabu;
    private int max_stable;

    public TabuSearch(int tabu_size,int max_stable) {
        this.rand = new Random();
        this.verbose = true;
        this.max_iter = 100;
        this.tabu_size = tabu_size;
        this.max_stable = max_stable;
        this.tabu = new HashMap<Move, Integer>();
    }

    public TabuSearch(int tabu_size, int max_stable, int max_iter) {
        this.rand = new Random();
        this.verbose = true;
        this.max_iter = max_iter;
        this.tabu_size = tabu_size;
        this.max_stable = max_stable;
        this.tabu = new HashMap<Move, Integer>();
    }

    public TabuSearch(int tabu_size, int max_stable, int max_iter, int seed) {
        this.rand = new Random(seed);
        this.verbose = true;
        this.max_iter = max_iter;
        this.tabu_size = tabu_size;
        this.max_stable = max_stable;
        this.tabu = new HashMap<Move, Integer>();
    }

    public TabuSearch(int tabu_size, int max_stable, int max_iter, int seed, boolean verbose) {
        this.rand = new Random(seed);
        this.verbose = verbose;
        this.max_iter = max_iter;
        this.tabu_size = tabu_size;
        this.max_stable = max_stable;
        this.tabu = new HashMap<Move, Integer>();
    }

    public void set_max_iteration(int max_iter) {
        this.max_iter = max_iter;
    }

    public void set_tabu_size(int tabu_size) {
        this.tabu_size = tabu_size;
    }

    private void assign_int(int[] x,VarIntLS[] y) {
        for (int i = 0; i < y.length; i++) 
            x[i] = y[i].getValue();
    }

    private void restart(VarIntLS[] y) {
        for(int i = 0; i < y.length; i++){
			int v = rand.nextInt(y[i].getMaxValue() - y[i].getMinValue()+1) + y[i].getMinValue();
			y[i].setValuePropagate(v);
		}
    }

    private Move jump_by_value(IConstraint c, int iteration, int best) {
        VarIntLS[] y = c.getVariables();
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new AssignMove(0,0,false));
        int minDelta = Integer.MAX_VALUE;
        for (int i = 0; i < y.length; i++) {
            for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) 
                if ( y[i].getValue() != v ) {
                    int d = c.getAssignDelta(y[i], v);
                    AssignMove move = new AssignMove(i, v);
                    if ( tabu.getOrDefault(move, 0) <= iteration || d + c.violations() < best ) {
                        if ( d < minDelta ) {
                            candidates.clear();
                            candidates.add(move);
                            minDelta = d;
                        } else if ( d == minDelta ) {
                            candidates.add(move);
                        }
                    }
                }
        }
        Move sel_m = candidates.get(rand.nextInt(candidates.size()));
        return sel_m;
    }

    private Move jump_by_value(IFunction f, int iteration, int best) {
        VarIntLS[] y = f.getVariables();
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new AssignMove(0,0,false));
        int minDelta = Integer.MAX_VALUE;
        for (int i = 0; i < y.length; i++) {
            for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) 
                if ( y[i].getValue() != v ) {
                    int d = f.getAssignDelta(y[i], v);
                    AssignMove move = new AssignMove(i, v);
                    if ( tabu.getOrDefault(move, 0) <= iteration || d + f.getValue() < best ) {
                        if ( d < minDelta ) {
                            candidates.clear();
                            candidates.add(move);
                            minDelta = d;
                        } else if ( d == minDelta ) {
                            candidates.add(move);
                        }
                    }
                }
        }
        Move sel_m = candidates.get(rand.nextInt(candidates.size()));
        return sel_m;
    }

    private Move jump_by_swapping(IConstraint c, int iteration, int best) {
        VarIntLS[] y = c.getVariables();
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new SwapMove(0,0,false));
        int minDelta = Integer.MAX_VALUE;
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y.length; j++) 
                if ( y[i].getValue() != y[j].getValue() ) {
                    int d = c.getSwapDelta(y[i], y[j]);
                    SwapMove move = new SwapMove(i, j);
                    if ( tabu.getOrDefault(move, 0) <= iteration || d + c.violations() < best ) {
                        if ( d < minDelta ) {
                            candidates.clear();
                            candidates.add(new SwapMove(i, j));
                            minDelta = d;
                        } else if ( d == minDelta ) {
                            candidates.add(new SwapMove(i, j));
                        }
                    }
                }
        }
        Move sel_m = candidates.get(rand.nextInt(candidates.size()));
        return sel_m;
    }

    private Move jump_by_swapping(IFunction f, int iteration, int best) {
        VarIntLS[] y = f.getVariables();
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new SwapMove(0,0,false));
        int minDelta = Integer.MAX_VALUE;
        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y.length; j++) 
                if ( y[i].getValue() != y[j].getValue() ) {
                    int d = f.getSwapDelta(y[i], y[j]);
                    SwapMove move = new SwapMove(i, j);
                    if ( tabu.getOrDefault(move, 0) <= iteration || d + f.getValue() < best ) {
                        if ( d < minDelta ) {
                            candidates.clear();
                            candidates.add(new SwapMove(i, j));
                            minDelta = d;
                        } else if ( d == minDelta ) {
                            candidates.add(new SwapMove(i, j));
                        }
                    }
                }
        }
        Move sel_m = candidates.get(rand.nextInt(candidates.size()));
        return sel_m;
    }

    private Move jump_by_value(IConstraint c, IFunction f, VarIntLS[] y, int iteration, int bestViolations, int bestObjective) {
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new AssignMove(0, y[0].getValue(), false));
        int best_dc = Integer.MAX_VALUE;
        int best_df = Integer.MAX_VALUE;

        for (int i = 0; i < y.length; i++) {
            for (int v = y[i].getMinValue(); v <= y[i].getMaxValue(); v++) {
                    int dc = c.getAssignDelta(y[i], v);
                    int df = f.getAssignDelta(y[i], v);
                    AssignMove move = new AssignMove(i, v);

                    if ( tabu.getOrDefault(move, 0) <= iteration || dc + c.violations() < bestViolations || 
                        (dc + c.violations() == bestViolations && df + f.getValue() < bestObjective) ) {
                            if ( dc < 0 || ( dc == 0 && df < 0) ) {
                                if ( dc < best_dc || ( dc == best_dc && df < best_df) ) {
                                    candidates.clear();
                                    candidates.add(move);
                                    best_dc = dc;
                                    best_df = df;
                                } else if ( dc == best_dc && df == best_df ) {
                                    candidates.add(move);
                                }
                            } 
                    }
            }
        }
        
        int sel_idx = rand.nextInt(candidates.size());
        Move sel_m = candidates.get(sel_idx);
        return sel_m;
    }

    private Move jump_by_swapping(IConstraint c, IFunction f, VarIntLS[] y, int iteration, int bestViolations, int bestObjective) {
        ArrayList<Move> candidates = new ArrayList<Move>();
        candidates.add(new SwapMove(0, 0, false));
        int best_dc = Integer.MAX_VALUE;
        int best_df = Integer.MAX_VALUE;

        for (int i = 0; i < y.length; i++) {
            for (int j = 0; j < y.length; j++) 
                if ( y[i].getValue() != y[j].getValue()) {
                    int dc = c.getSwapDelta(y[i], y[j]);
                    int df = f.getSwapDelta(y[i], y[j]);
                    SwapMove move = new SwapMove(i, j);

                    if ( tabu.getOrDefault(move, 0) <= iteration || dc + c.violations() < bestViolations || 
                        (dc + c.violations() == bestViolations && df + f.getValue() < bestObjective) ) {
                            if ( dc < 0 || ( dc == 0 && df <= 0) ) {
                                if ( dc < best_dc || ( dc == best_dc && df < best_df) ) {
                                    candidates.clear();
                                    candidates.add(move);
                                    best_dc = dc;
                                    best_df = df;
                                } else if ( dc == best_dc && df == best_df ) {
                                    candidates.add(move);
                                }
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
        int nic = 0;
        VarIntLS[] y = c.getVariables();
        init_solution(y);
        int[] bval = new int[y.length];
        assign_int(bval, y);

        int best = c.violations();

        while ( it < max_iter && best > 0 ) {
            if (verbose) 
                System.out.printf("iteration %d: violations = %d\n",it, c.violations());

            if (move_temp instanceof AssignMove) {
                Move sel_m = jump_by_value(c, it, best);
                AssignMove sel_am = (AssignMove)sel_m;
                
                if ( sel_am.is_legal ) {
                    tabu.put(sel_am, it + tabu_size);
                    y[sel_am.i].setValuePropagate(sel_am.v);
                }
            } else if ( move_temp instanceof SwapMove) {
                Move sel_m = jump_by_swapping(c, it, best);
                SwapMove sel_sm = (SwapMove) sel_m;

                if ( sel_sm.is_legal ) {
                    tabu.put(sel_sm, it + tabu_size);
                    y[sel_sm.u].swapValuePropagate(y[sel_sm.v]);
                }
            }

            if ( c.violations() < best ) {
                best = c.violations();
                assign_int(bval, y);
                nic = 0;
            } else {
                nic++;
                if ( nic >= max_stable ) {
                    nic = 0;
                    restart(y);
                    System.out.println("restarting");
                    if ( c.violations() < best ) {
                        best = c.violations();
                        assign_int(bval, y);
                    }
                }
            }

            it++;
        }

        for (int i = 0; i < y.length; i++) 
            y[i].setValuePropagate(bval[i]);

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
        int[] bval = new int[y.length];
        assign_int(bval, y);

        int bestViolations = c.violations();
        int bestObjective = f.getValue();
        int nic = 0;

        while ( it < max_iter ) {
            if (verbose) 
                System.out.printf("iteration %d: objective = %d and violations = %d\n", it, f.getValue(), c.violations());

            if (move_temp instanceof AssignMove) {
                Move sel_m = jump_by_value(c, f, y, it, bestViolations, bestObjective);
                AssignMove sel_am = (AssignMove)sel_m;

                if ( sel_am.is_legal ) {
                    tabu.put(sel_am, it + tabu_size);
                    y[sel_am.i].setValuePropagate(sel_am.v);
                }
            } else if ( move_temp instanceof SwapMove) {
                Move sel_m = jump_by_swapping(c, f, y, it, bestViolations, bestObjective);
                SwapMove sel_sm = (SwapMove) sel_m;

                if ( !sel_sm.is_legal )  {
                    tabu.put(sel_sm, it + tabu_size);
                    y[sel_sm.u].swapValuePropagate(y[sel_sm.v]);
                }
            }

            if ( c.violations() < bestViolations || (c.violations() == bestViolations && f.getValue() < bestObjective) ) {
                bestViolations = c.violations();
                bestObjective = f.getValue();
                assign_int(bval, y);
                nic = 0;
            } else {
                nic++;
                if ( nic >= max_stable ) {
                    restart(y);
                    if ( c.violations() < bestViolations || (c.violations() == bestViolations && f.getValue() < bestObjective) ) {
                        bestViolations = c.violations();
                        bestObjective = f.getValue();
                        assign_int(bval, y);
                    }
                }
            }

            it++;
        }

        for (int i = 0; i < y.length; i++) 
            y[i].setValuePropagate(bval[i]);
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
        int nic = 0;
        VarIntLS[] y = f.getVariables();
        int[] bval = new int[y.length];
        init_solution(y);
        assign_int(bval, y);

        int best = f.getValue();

        while ( it < max_iter) {
            if (verbose) 
                System.out.printf("iteration %d: objective = %d\n",it, f.getValue());

            if (move_temp instanceof AssignMove) {
                Move sel_m = jump_by_value(f, it, best);
                AssignMove sel_am = (AssignMove)sel_m;
                
                if ( sel_am.is_legal ) {
                    tabu.put(sel_am, it + tabu_size);
                    y[sel_am.i].setValuePropagate(sel_am.v);
                }
            } else if ( move_temp instanceof SwapMove) {
                Move sel_m = jump_by_swapping(f, it, best);
                SwapMove sel_sm = (SwapMove) sel_m;

                if ( sel_sm.is_legal ) {
                    tabu.put(sel_sm, it + tabu_size);
                    y[sel_sm.u].swapValuePropagate(y[sel_sm.v]);
                }
            }

            if ( f.getValue() < best ) {
                best = f.getValue();
                assign_int(bval, y);
                nic = 0;
            } else {
                nic++;
                if ( nic >= max_stable ) {
                    restart(y);
                    if ( f.getValue() < best ) {
                        best = f.getValue();
                        assign_int(bval, y);
                    }
                }
            }

            it++;
        }

        for (int i = 0; i < y.length; i++) 
            y[i].setValuePropagate(bval[i]);

        if (verbose) 
            System.out.printf("Reach local optimum: objective = %d\n", f.getValue());
    }

    public void test() {
        tabu.put(new AssignMove(1,2),3);
        tabu.put(new AssignMove(1,2),4);
        System.out.print(2);
        System.out.print(tabu.get(new AssignMove(1,2)));
    }

    public static void main(String[] args) {
        System.out.print(1);
        TabuSearch searcher = new TabuSearch(5,5);
        searcher.test();
    }
}