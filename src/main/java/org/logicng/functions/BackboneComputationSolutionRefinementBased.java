///////////////////////////////////////////////////////////////////////////
//                   __                _      _   ________               //
//                  / /   ____  ____ _(_)____/ | / / ____/               //
//                 / /   / __ \/ __ `/ / ___/  |/ / / __                 //
//                / /___/ /_/ / /_/ / / /__/ /|  / /_/ /                 //
//               /_____/\____/\__, /_/\___/_/ |_/\____/                  //
//                           /____/                                      //
//                                                                       //
//               The Next Generation Logic Library                       //
//                                                                       //
///////////////////////////////////////////////////////////////////////////
//                                                                       //
//  Copyright 2015-2016 Christoph Zengler                                //
//                                                                       //
//  Licensed under the Apache License, Version 2.0 (the "License");      //
//  you may not use this file except in compliance with the License.     //
//  You may obtain a copy of the License at                              //
//                                                                       //
//  http://www.apache.org/licenses/LICENSE-2.0                           //
//                                                                       //
//  Unless required by applicable law or agreed to in writing, software  //
//  distributed under the License is distributed on an "AS IS" BASIS,    //
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or      //
//  implied.  See the License for the specific language governing        //
//  permissions and limitations under the License.                       //
//                                                                       //
///////////////////////////////////////////////////////////////////////////

package org.logicng.functions;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFunction;
import org.logicng.formulas.Literal;
import org.logicng.solvers.MiniSat;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * A function that computes the backbone for a given formula, i.e. a set of literals that is fix in all satisfying
 * assignments.
 * <p>
 * @version 1.0
 * @since ?
 */
public class BackboneComputationSolutionRefinementBased implements FormulaFunction<Collection<Literal>> {


  /**
   * Computes the backbone of the given formula. I.e. a set of literals that is fix in all satisfying assignments.
   * @param input   the formula whos backbone is to be computed
   * @param cache   - not supported at the moment -
   * @return the backbone of the formula. An empty SortedSet if there is no backbone.
   *
   * @throws IllegalArgumentException if the input formula is unsatisfiable.
   */
  @Override
  public SortedSet<Literal> apply(Formula input, boolean cache) {
    MiniSat solver = MiniSat.miniSat(input.factory());
    solver.add(input);
    if (solver.sat() != Tristate.TRUE)
      return new TreeSet<>();
    SortedSet<Literal> backbone = null;
    do {
      Assignment solution = solver.model();
      if (solution == null) {
        return backbone;
      }
      if (backbone == null) {
        backbone = solution.literals();
      }
      else {
        backbone.retainAll(solution.literals());
      }
      Formula blockingClause = solution.blockingClause(input.factory(), null);
      solver.add(blockingClause);
      solver.sat();
    } while (!backbone.isEmpty());
    return backbone;
  }
}
