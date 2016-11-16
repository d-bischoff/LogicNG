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

package org.logicng.bdds.jbuddy;

import org.junit.Assert;
import org.junit.Test;
import org.logicng.bdds.BDD;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.testutils.NQueensGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

/**
 * Unit tests for the BDDs.
 * @version 1.2
 * @since 1.2
 */
public class BDDModelEnumerationTest {

  private final FormulaFactory f;

  private final List<Formula> formulas;
  private final List<SortedSet<Variable>> variables;
  private final BigDecimal[] expected;

  public BDDModelEnumerationTest() {
    int[] problems = new int[]{3, 4, 5, 6, 7, 8, 9};
    this.expected = new BigDecimal[]{
            BigDecimal.valueOf(0),
            BigDecimal.valueOf(2),
            BigDecimal.valueOf(10),
            BigDecimal.valueOf(4),
            BigDecimal.valueOf(40),
            BigDecimal.valueOf(92),
            BigDecimal.valueOf(352)
    };

    this.f = new FormulaFactory();
    final NQueensGenerator generator = new NQueensGenerator(f);
    this.formulas = new ArrayList<>(problems.length);
    this.variables = new ArrayList<>(problems.length);

    for (final int problem : problems) {
      final Formula p = generator.generate(problem);
      formulas.add(p);
      variables.add(p.variables());
    }
  }

  @Test
  public void testModelCount() {
    for (int i = 0; i < this.formulas.size(); i++) {
      JBuddyFactory factory = new JBuddyFactory(10000, 10000, f);
      factory.setNumberOfVars(this.variables.get(i).size());
      final BDD bdd = factory.build(this.formulas.get(i));
      Assert.assertEquals(this.expected[i], bdd.modelCount());
    }
  }

  @Test
  public void testModelEnumeration() {
    for (int i = 0; i < this.formulas.size(); i++) {
      JBuddyFactory factory = new JBuddyFactory(10000, 10000, f);
      factory.setNumberOfVars(this.variables.get(i).size());
      final BDD bdd = factory.build(this.formulas.get(i));
      Set<Assignment> models = new HashSet<>(bdd.enumerateAllModels());
      Assert.assertEquals(this.expected[i].intValue(), models.size());
      for (final Assignment model : models)
        Assert.assertTrue(formulas.get(i).evaluate(model));
    }
  }

}
