package cbls115676khmt61.vudt_20164705.search;

import java.util.ArrayList;
import java.util.Random;

import localsearch.model.IConstraint;
import localsearch.model.VarIntLS;

public class HillClimbingSearch {
  class Move {
    int i;
    int v;
    public Move(int i, int v) {
      this.i= i;
      this.v= v;
    }
  }

  Random R = new Random();

  public ArrayList<Move> exploreNeighborhood(IConstraint constraints, VarIntLS[] x) {
    ArrayList<Move> candidates = new ArrayList<>();
    int minDelta = Integer.MAX_VALUE;
    for(int i = 0; i < x.length; i++) {
      for(int v = x[i].getMinValue(); v <= x[i].getMaxValue(); v++) {
        if(v == x[i].getValue()) continue;
        int delta = constraints.getAssignDelta(x[i], v);
        if(delta < minDelta) {
          minDelta = delta;
          candidates.clear();
        }
        if(minDelta == delta) {
          candidates.add(new Move(i, v));
        }
      }
    }
    System.out.println("minDelta:" + minDelta);
    return candidates;
  }

  public void initSolution(VarIntLS[] solution) {
    for(int i = 0; i < solution.length; i++) {
      int v = R.nextInt(solution[i].getMaxValue() - solution[i].getMinValue() + 1) + solution[i].getMinValue();
      solution[i].setValuePropagate(v);
    }
  }

  public void search(IConstraint constraints, int maxIter, boolean debug) {
    VarIntLS[] solution = constraints.getVariables();
    initSolution(solution);
    int it = 0;
    ArrayList<Move> candidates;
    while(it < maxIter && constraints.violations() > 0) {
      candidates = exploreNeighborhood(constraints, solution);
      if(candidates.size() == 0)
        break;
      Move move = candidates.get(R.nextInt(candidates.size()));
      solution[move.i].setValuePropagate(move.v);
      it++;
      if(debug) {
        System.out.println("Step " + it + ", violations:" + constraints.violations());
      }
    }
  }
}