/*
// Filename: LocalSearch.java
// Description:
// Created by ngocjr7 on [16-04-2020 00:06:58]
*/
package cbls115676khmt61.ngocbh_20164797.search;

import localsearch.model.IConstraint;
import localsearch.model.IFunction;

public interface LocalSearch{
	public void minimize_objective(IFunction f, Move move_temp);
	public void search_two_phase(IConstraint c, Move con_move_temp, IFunction f, Move func_move_temp);
	public void minimize_objective_with_constraint(IFunction f, IConstraint c, Move move_temp);
	public void satisfy_constraint(IConstraint c, Move move_temp);
}