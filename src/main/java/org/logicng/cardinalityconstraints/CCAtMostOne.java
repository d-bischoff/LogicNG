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
import org.logicng.formulas.Variable;

import java.util.Collection;

/**
 * The interface for at-most-one (AMO) cardinality constraints.
 * @version 1.0
 * @since 1.0
 */
public abstract class CCAtMostOne {

  /**
   * Builds a cardinality constraint of the form {@code var_1 + var_2 + ... + var_n <= 1}.
   * @param vars the variables {@code var_1 ... var_n}
   * @return the CNF encoding of the cardinality constraint
   */
  public ImmutableFormulaList build(Collection<Variable> vars) {
    return this.build(vars.toArray(new Variable[vars.size()]));
  }

  /**
   * Builds a cardinality constraint of the form {@code var_1 + var_2 + ... + var_n <= 1}.
   * @param vars the variables {@code var_1 ... var_n}
   * @return the CNF encoding of the cardinality constraint
   */
  public abstract ImmutableFormulaList build(final Variable... vars);
}
