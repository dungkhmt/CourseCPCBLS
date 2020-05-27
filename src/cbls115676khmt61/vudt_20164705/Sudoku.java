package cbls115676khmt61.vudt_20164705;

import java.util.*;
import localsearch.model.VarIntLS;
import localsearch.model.LocalSearchManager;
import localsearch.model.ConstraintSystem;
import localsearch.constraints.alldifferent.AllDifferent;

public class Sudoku {
  private LocalSearchManager mgr;
  private ConstraintSystem constraints;
  private VarIntLS[][] sodokuTable;


  public Sudoku() {
    mgr = new LocalSearchManager();
    sodokuTable = new VarIntLS[9][9];
    for(int i = 0; i < 9; i++) {
      for(int j = 0; j < 9; j++) {
        sodokuTable[i][j] = new VarIntLS(mgr, 1, 9);
        sodokuTable[i][j].setValue(j + 1);
      }
    }
    constraints = new ConstraintSystem(mgr);
    // check rows violations
    for(int i = 0; i < 9; i++) {
      constraints.post(new AllDifferent(sodokuTable[i]));
    }
    // check columns violations
    for(int i = 0; i < 9; i++) {
      VarIntLS[] column = new VarIntLS[9];
      for(int j = 0; j < 9; j++) {
        column[j] = sodokuTable[j][i];
      }
      constraints.post(new AllDifferent(column));
    }
    // check square 3x3 violations
    for(int i = 0; i < 3; i++) {
      for(int j = 0; j < 3; j++) {
        VarIntLS[] square = new VarIntLS[9];
        for(int h = 0; h < 3; h++) {
          for(int k = 0; k < 3; k++) {
            square[h * 3 + k] = sodokuTable[i * 3 + h][j* 3 + k];
          }
        }
        constraints.post(new AllDifferent(square));
      }
    }
    mgr.close();
  }

  public void search(int maxIter, boolean debug) {
    class Move {
      int i;
      int j1;
      int j2;
      public Move(int i, int j1, int j2) {
        this.i = i;
        this.j1 = j1;
        this.j2 = j2;
      }
    }
    int iter = 0;
    Random random = new Random();
    while (iter < maxIter && constraints.violations() > 0) {
      if (debug) {
        System.out.println("Iter " + iter + ": " + constraints.violations());
      }
      ArrayList<Move> candidates = new ArrayList<>();
      int minDelta = Integer.MAX_VALUE;
      for(int i = 0; i < 9; i++) {
        for(int j1 = 0; j1 < 8; j1++) {
          for(int j2 = j1 + 1; j2 < 9; j2++) {
            int delta = constraints.getSwapDelta(
              sodokuTable[i][j1], sodokuTable[i][j2]);
            if (delta < minDelta) {
              candidates.clear();
              minDelta = delta;
              candidates.add(new Move(i, j1, j2));
            }
            else if (delta == minDelta) {
              candidates.add(new Move(i, j1, j2));
            }
          }
        }
      }
      Move currMove = candidates.get(random.nextInt(candidates.size()));
      sodokuTable[currMove.i][currMove.j1]
        .swapValuePropagate(sodokuTable[currMove.i][currMove.j2]);
      iter++;
    }
  }

  public void printSolution() {
    System.out.println("-----------------------------");
    System.out.println("Violation: " + constraints.violations());
    for(int i = 0; i < 9; i++) {
      for(int j = 0; j < 9; j++) {
        System.out.print(sodokuTable[i][j].getValue() + " ");
      }
      System.out.println();
    }
    System.out.println("-----------------------------");
  }

  public static void main(String[] args) {
    Sudoku app = new Sudoku();
    app.search(1000, true);
    app.printSolution();
  }

}

