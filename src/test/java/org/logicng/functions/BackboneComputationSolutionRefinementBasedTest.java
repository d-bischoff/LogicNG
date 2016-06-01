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

import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.FormulaFunction;
import org.logicng.formulas.Literal;
import org.logicng.io.parsers.PropositionalParser;
import java.util.Collection;
import java.util.HashSet;
import static org.junit.Assert.assertEquals;


/**
 * Unit tests for {@link BackboneComputationSolutionRefinementBased}.
 * @version 1.0
 */
public class BackboneComputationSolutionRefinementBasedTest {

  private final FormulaFactory factory = new FormulaFactory();
  private final PropositionalParser parser = new PropositionalParser(factory);

  @Test
  public void testSimpleLiteral01() throws Exception {
    Formula f1 = parser.parse("a&(b|c)");
    FormulaFunction<Collection<Literal>> function = new BackboneComputationSolutionRefinementBased();
    Collection<Literal> calculatedBackbone = f1.apply(function);
    Collection<Literal> expectedBackbone = new HashSet<>();
    expectedBackbone.add(factory.literal("a", true));
    assertEquals(expectedBackbone, calculatedBackbone);
  }

  @Test
  public void testSimpleLiteral02() throws Exception {
    Formula f1 = parser.parse("~a&(b|c)");
    FormulaFunction<Collection<Literal>> function = new BackboneComputationSolutionRefinementBased();
    Collection<Literal> calculatedBackbone = f1.apply(function);
    Collection<Literal> expectedBackbone = new HashSet<>();
    expectedBackbone.add(factory.literal("a", false));
    assertEquals(expectedBackbone, calculatedBackbone);
  }

  @Test
  public void testSimpleLiteral03() throws Exception {
    Formula f1 = parser.parse("~a&b");
    FormulaFunction<Collection<Literal>> function = new BackboneComputationSolutionRefinementBased();
    Collection<Literal> calculatedBackbone = f1.apply(function);
    Collection<Literal> expectedBackbone = new HashSet<>();
    expectedBackbone.add(factory.literal("a", false));
    expectedBackbone.add(factory.literal("b", true));
    assertEquals(expectedBackbone, calculatedBackbone);
  }

  @Test
  public void testSimpleLiteral04() throws Exception {
    Formula f1 = parser.parse("~a|a");
    FormulaFunction<Collection<Literal>> function = new BackboneComputationSolutionRefinementBased();
    Collection<Literal> calculatedBackbone = f1.apply(function);
    Collection<Literal> expectedBackbone = new HashSet<>();
    assertEquals(expectedBackbone, calculatedBackbone);
  }
}