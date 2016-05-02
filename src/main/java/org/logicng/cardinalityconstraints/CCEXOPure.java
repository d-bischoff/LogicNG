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

package org.logicng.cardinalityconstraints;

import org.logicng.collections.ImmutableFormulaList;
import org.logicng.formulas.FType;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;

import java.util.LinkedList;
import java.util.List;

/**
 * Encodes that exactly one variable is assigned value true.  Uses the 'naive' encoding with no introduction
 * of new variables but quadratic size.
 * @version 1.0
 * @since 1.0
 */
public final class CCEXOPure extends CCExactlyOne {

  private final FormulaFactory f;

  /**
   * Constructs the naive EXO encoder.
   * @param f the formula factory
   */
  public CCEXOPure(final FormulaFactory f) {
    this.f = f;
  }

  @Override
  public ImmutableFormulaList build(final Variable... vars) {
    final List<Formula> result = new LinkedList<>();
    if (vars.length == 0)
      return new ImmutableFormulaList(FType.AND);
    if (vars.length == 1) {
      result.add(vars[0]);
      return new ImmutableFormulaList(FType.AND, result);
    }
    result.add(this.f.clause(vars));
    for (int i = 0; i < vars.length; i++)
      for (int j = i + 1; j < vars.length; j++)
        result.add(this.f.clause(vars[i].negate(), vars[j].negate()));
    return new ImmutableFormulaList(FType.AND, result);
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
