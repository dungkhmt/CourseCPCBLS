package cbls115676khmt61.vudt_20164705;

import localsearch.model.VarIntLS;
import localsearch.selectors.MinMaxSelector;
import localsearch.model.LocalSearchManager;
import localsearch.model.ConstraintSystem;
import localsearch.model.IFunction;
import localsearch.constraints.alldifferent.AllDifferent;
import localsearch.functions.basic.FuncMinus;
import localsearch.functions.basic.FuncPlus;

class NQueens {
  private int nbOfQueens;
  private LocalSearchManager mgr;
  private ConstraintSystem constraints;
  private VarIntLS[] queensSolution;

  public NQueens(int nbOfQueens) {
    this.nbOfQueens = nbOfQueens;
  }

  public void initModel() {
    mgr = new LocalSearchManager();
    queensSolution = new VarIntLS[nbOfQueens];
    constraints = new ConstraintSystem(mgr);
    for(int i = 0; i < nbOfQueens; i++) {
      queensSolution[i] = new VarIntLS(mgr, 0, nbOfQueens - 1);
    }
    constraints.post(new AllDifferent(queensSolution));
    IFunction[] diagonalFunctions = new IFunction[nbOfQueens];
    for(int i = 0; i < nbOfQueens; i++) {
      diagonalFunctions[i] = new FuncPlus(queensSolution[i], i);
    }
    constraints.post(new AllDifferent(diagonalFunctions));
    IFunction[] antidiagonalFunctions = new IFunction[nbOfQueens];
    for(int i = 0; i < nbOfQueens; i++) {
      antidiagonalFunctions[i] = new FuncMinus(queensSolution[i], i);
    }
    constraints.post(new AllDifferent(antidiagonalFunctions));
    mgr.close();
  }

  public void search() {
    MinMaxSelector mns = new MinMaxSelector(constraints);
    int it = 0;
    int MAX_ITER = 10000;
    while (it < MAX_ITER && constraints.violations() > 0) {
      VarIntLS queenPos = mns.selectMostViolatingVariable();
      int queenMove = mns.selectMostPromissingValue(queenPos);
      queenPos.setValuePropagate(queenMove);
      it++;
    }
  }

  public void printResult() {
    System.out.println("-----------------------");
    System.out.println("Violations: " + constraints.violations());
    for(int i = 0; i < nbOfQueens; i++) {
      for(int j = 0; j < nbOfQueens; j++) {
        if(j == queensSolution[i].getValue()) {
          System.out.print("1 ");
        }
        else {
          System.out.print("0 ");
        }
      }
      System.out.println();
    }
    System.out.println();
    System.out.println("-----------------------");

  }

  public static void main(String[] args) {
    NQueens app = new NQueens(8);
    app.initModel();
    app.search();
    app.printResult();
  }
}