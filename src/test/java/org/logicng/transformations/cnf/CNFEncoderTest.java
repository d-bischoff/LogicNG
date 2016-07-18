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

package org.logicng.transformations.cnf;

import org.junit.Assert;
import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;

/**
 * Unit tests for the class {@link CNFEncoder}.
 * @version 1.1
 * @since 1.1
 */
public class CNFEncoderTest {

  private static final String p1 = "(x1 | x2) & x3 & x4 & ((x1 & x5 & ~(x6 | x7) | x8) | x9)";
  private static final String p2 = "(y1 | y2) & y3 & y4 & ((y1 & y5 & ~(y6 | y7) | y8) | y9)";
  private static final String p3 = "(z1 | z2) & z3 & z4 & ((z1 & z5 & ~(z6 | z7) | z8) | z9)";

  @Test
  public void testFactorization() throws ParserException {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final Formula phi1 = p.parse(p1);
    Assert.assertEquals(10, phi1.numberOfAtoms());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), phi1.cnf());
    f.putConfiguration(new CNFConfig.Builder().build());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), phi1.cnf());
    f.putConfiguration(new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.FACTORIZATION).build());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), phi1.cnf());
    CNFEncoder encoder = new CNFEncoder(f, new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.FACTORIZATION).build());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), encoder.encode(phi1));
  }

  @Test
  public void testTseitin() throws ParserException {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final Formula phi1 = p.parse(p1);
    final Formula phi2 = p.parse(p2);
    f.putConfiguration(new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.TSEITIN).build());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), phi1.cnf());
    f.putConfiguration(new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.TSEITIN).atomBoundary(8).build());
    Assert.assertEquals(p.parse("(@RESERVED_CNF_0 | ~x1) & (@RESERVED_CNF_0 | ~x2) & (~@RESERVED_CNF_0 | x1 | x2) & (~@RESERVED_CNF_1 | x1) & (~@RESERVED_CNF_1 | x5) & (~@RESERVED_CNF_1 | ~x6) & (~@RESERVED_CNF_1 | ~x7) & (@RESERVED_CNF_1 | ~x1 | ~x5 | x6 | x7) & (@RESERVED_CNF_2 | ~@RESERVED_CNF_1) & (@RESERVED_CNF_2 | ~x8) & (@RESERVED_CNF_2 | ~x9) & (~@RESERVED_CNF_2 | @RESERVED_CNF_1 | x8 | x9) & @RESERVED_CNF_0 & x3 & x4 & @RESERVED_CNF_2"), phi1.cnf());
    f.putConfiguration(new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.TSEITIN).atomBoundary(11).build());
    Assert.assertEquals(p.parse("(y1 | y2) & y3 & y4 & (y1 | y8 | y9) & (y5 | y8 | y9) & (~y6 | y8 | y9) & (~y7 | y8 | y9)"), phi2.cnf());
  }

  @Test
  public void testPG() throws ParserException {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final Formula phi1 = p.parse(p1);
    final Formula phi2 = p.parse(p2);
    f.putConfiguration(new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.PLAISTED_GREENBAUM).build());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), phi1.cnf());
    f.putConfiguration(new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.PLAISTED_GREENBAUM).atomBoundary(8).build());
    Assert.assertEquals(p.parse("@RESERVED_CNF_1 & x3 & x4 & @RESERVED_CNF_2 & (~@RESERVED_CNF_1 | x1 | x2) & (~@RESERVED_CNF_2 | @RESERVED_CNF_3 | x8 | x9) & (~@RESERVED_CNF_3 | x1) & (~@RESERVED_CNF_3 | x5) & (~@RESERVED_CNF_3 | ~x6) & (~@RESERVED_CNF_3 | ~x7)"), phi1.cnf());
    f.putConfiguration(new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.PLAISTED_GREENBAUM).atomBoundary(11).build());
    Assert.assertEquals(p.parse("(y1 | y2) & y3 & y4 & (y1 | y8 | y9) & (y5 | y8 | y9) & (~y6 | y8 | y9) & (~y7 | y8 | y9)"), phi2.cnf());
  }

  @Test
  public void testAdvanced() throws ParserException {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final Formula phi1 = p.parse(p1);
    final Formula phi2 = p.parse(p2);
    final Formula phi3 = p.parse(p3);
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), phi1.cnf());
    f.putConfiguration(new CNFConfig.Builder().createdClauseBoundary(5).atomBoundary(3).build());
    Assert.assertEquals(p.parse("(y1 | y2) & y3 & y4 & (~@RESERVED_CNF_0 | y1) & (~@RESERVED_CNF_0 | y5) & (~@RESERVED_CNF_0 | ~y6) & (~@RESERVED_CNF_0 | ~y7) & (@RESERVED_CNF_0 | ~y1 | ~y5 | y6 | y7) & (@RESERVED_CNF_0 | y8 | y9)"), phi2.cnf());
    f.putConfiguration(new CNFConfig.Builder().createdClauseBoundary(-1).distributionBoundary(5).atomBoundary(3).build());
    Assert.assertEquals(p.parse("(z1 | z2) & z3 & z4 & (~@RESERVED_CNF_2 | z1) & (~@RESERVED_CNF_2 | z5) & (~@RESERVED_CNF_2 | ~z6) & (~@RESERVED_CNF_2 | ~z7) & (@RESERVED_CNF_2 | ~z1 | ~z5 | z6 | z7) & (@RESERVED_CNF_2 | z8 | z9)"), phi3.cnf());
  }

  @Test
  public void testAdvancedWithPGFallback() throws ParserException {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final Formula phi1 = p.parse(p1);
    final Formula phi2 = p.parse(p2);
    final Formula phi3 = p.parse(p3);
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), phi1.cnf());
    f.putConfiguration(new CNFConfig.Builder().createdClauseBoundary(5).atomBoundary(3).fallbackAlgorithmForAdvancedEncoding(CNFConfig.Algorithm.PLAISTED_GREENBAUM).build());
    Assert.assertEquals(p.parse("(y1 | y2) & y3 & y4 & (@RESERVED_CNF_1 | y8 | y9) & (~@RESERVED_CNF_1 | y1) & (~@RESERVED_CNF_1 | y5) & (~@RESERVED_CNF_1 | ~y6) & (~@RESERVED_CNF_1 | ~y7)"), phi2.cnf());
    f.putConfiguration(new CNFConfig.Builder().createdClauseBoundary(-1).distributionBoundary(5).atomBoundary(3).fallbackAlgorithmForAdvancedEncoding(CNFConfig.Algorithm.PLAISTED_GREENBAUM).build());
    Assert.assertEquals(p.parse("(z1 | z2) & z3 & z4 & (@RESERVED_CNF_3 | z8 | z9) & (~@RESERVED_CNF_3 | z1) & (~@RESERVED_CNF_3 | z5) & (~@RESERVED_CNF_3 | ~z6) & (~@RESERVED_CNF_3 | ~z7)"), phi3.cnf());
  }

  @Test
  public void testTseitinEncoder() throws ParserException {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final Formula phi1 = p.parse(p1);
    CNFEncoder encoder1 = new CNFEncoder(f, new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.TSEITIN).build());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), encoder1.encode(phi1));
    CNFEncoder encoder2 = new CNFEncoder(f, new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.TSEITIN).atomBoundary(8).build());
    Assert.assertEquals(p.parse("(@RESERVED_CNF_0 | ~x1) & (@RESERVED_CNF_0 | ~x2) & (~@RESERVED_CNF_0 | x1 | x2) & (~@RESERVED_CNF_1 | x1) & (~@RESERVED_CNF_1 | x5) & (~@RESERVED_CNF_1 | ~x6) & (~@RESERVED_CNF_1 | ~x7) & (@RESERVED_CNF_1 | ~x1 | ~x5 | x6 | x7) & (@RESERVED_CNF_2 | ~@RESERVED_CNF_1) & (@RESERVED_CNF_2 | ~x8) & (@RESERVED_CNF_2 | ~x9) & (~@RESERVED_CNF_2 | @RESERVED_CNF_1 | x8 | x9) & @RESERVED_CNF_0 & x3 & x4 & @RESERVED_CNF_2"), encoder2.encode(phi1));
  }

  @Test
  public void testPGEncoder() throws ParserException {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final Formula phi1 = p.parse(p1);
    CNFEncoder encoder1 = new CNFEncoder(f, new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.PLAISTED_GREENBAUM).build());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), encoder1.encode(phi1));
    CNFEncoder encoder2 = new CNFEncoder(f, new CNFConfig.Builder().algorithm(CNFConfig.Algorithm.PLAISTED_GREENBAUM).atomBoundary(8).build());
    Assert.assertEquals(p.parse("@RESERVED_CNF_1 & x3 & x4 & @RESERVED_CNF_2 & (~@RESERVED_CNF_1 | x1 | x2) & (~@RESERVED_CNF_2 | @RESERVED_CNF_3 | x8 | x9) & (~@RESERVED_CNF_3 | x1) & (~@RESERVED_CNF_3 | x5) & (~@RESERVED_CNF_3 | ~x6) & (~@RESERVED_CNF_3 | ~x7)"), encoder2.encode(phi1));
  }

  @Test
  public void testAdvancedEncoder() throws ParserException {
    final FormulaFactory f = new FormulaFactory();
    final PropositionalParser p = new PropositionalParser(f);
    final Formula phi1 = p.parse(p1);
    final Formula phi2 = p.parse(p2);
    final Formula phi3 = p.parse(p3);
    CNFEncoder encoder1 = new CNFEncoder(f, new CNFConfig.Builder().build());
    Assert.assertEquals(p.parse("(x1 | x2) & x3 & x4 & (x1 | x8 | x9) & (x5 | x8 | x9) & (~x6 | x8 | x9) & (~x7 | x8 | x9)"), encoder1.encode(phi1));
    CNFEncoder encoder2 = new CNFEncoder(f, new CNFConfig.Builder().createdClauseBoundary(5).atomBoundary(3).build());
    Assert.assertEquals(p.parse("(y1 | y2) & y3 & y4 & (~@RESERVED_CNF_0 | y1) & (~@RESERVED_CNF_0 | y5) & (~@RESERVED_CNF_0 | ~y6) & (~@RESERVED_CNF_0 | ~y7) & (@RESERVED_CNF_0 | ~y1 | ~y5 | y6 | y7) & (@RESERVED_CNF_0 | y8 | y9)"), encoder2.encode(phi2));
    CNFEncoder encoder3 = new CNFEncoder(f, new CNFConfig.Builder().createdClauseBoundary(-1).distributionBoundary(5).atomBoundary(3).build());
    Assert.assertEquals(p.parse("(z1 | z2) & z3 & z4 & (~@RESERVED_CNF_2 | z1) & (~@RESERVED_CNF_2 | z5) & (~@RESERVED_CNF_2 | ~z6) & (~@RESERVED_CNF_2 | ~z7) & (@RESERVED_CNF_2 | ~z1 | ~z5 | z6 | z7) & (@RESERVED_CNF_2 | z8 | z9)"), encoder3.encode(phi3));
  }

}
