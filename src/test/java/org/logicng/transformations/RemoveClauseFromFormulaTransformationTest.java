package org.logicng.transformations;

import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author Daniel Bischoff on 13.06.2016. Copyright by Daniel Bischoff
 * @version 1.0
 * @since 1.0
 */
public class RemoveClauseFromFormulaTransformationTest {


  private final FormulaFactory fac = new FormulaFactory();

  @Test
  public void simple1() throws Exception {
    Formula clause = fac.literal("a", false);
    Formula form = fac.and(clause, fac.variable("b"));
    Formula exp = fac.variable("b");

    RemoveClauseFromFormulaTransformation trans = new RemoveClauseFromFormulaTransformation(clause);
    assertEquals(form.transform(trans), exp);
  }

  @Test
  public void simple2() throws Exception {
    Formula clause = fac.literal("a", false);
    Formula form = fac.and(clause, fac.variable("b"), fac.variable("c"));
    Formula exp = fac.and(fac.variable("b"), fac.variable("c"));

    RemoveClauseFromFormulaTransformation trans = new RemoveClauseFromFormulaTransformation(clause);
    assertEquals(form.transform(trans), exp);
  }

  @Test
  public void simple0() throws Exception {
    Formula clause = fac.literal("a", false);
    Formula exp = fac.verum();

    RemoveClauseFromFormulaTransformation trans = new RemoveClauseFromFormulaTransformation(clause);
    assertEquals(clause.transform(trans), exp);
  }

  @Test
  public void simple3() throws Exception {
    Formula clause = fac.literal("a", false);
    Formula form = fac.and(fac.variable("d"), clause, fac.variable("b"), fac.variable("c"));
    Formula exp = fac.and(fac.variable("d"), fac.variable("b"), fac.variable("c"));

    RemoveClauseFromFormulaTransformation trans = new RemoveClauseFromFormulaTransformation(clause);
    assertEquals(form.transform(trans), exp);
  }

  @Test
  public void medium0() throws Exception {
    Formula clause = fac.or(fac.literal("a", false), fac.literal("g", true));
    Formula form = fac.and(fac.variable("d"), clause, fac.variable("b"), fac.variable("c"));
    Formula exp = fac.and(fac.variable("d"), fac.variable("b"), fac.variable("c"));

    RemoveClauseFromFormulaTransformation trans = new RemoveClauseFromFormulaTransformation(clause);
    assertEquals(form.transform(trans), exp);
  }

}