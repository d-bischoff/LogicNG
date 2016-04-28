package org.logicng.datastructures;

import org.junit.Before;
import org.junit.Test;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Daniel Bischoff on 21.01.2016. Copyright by Daniel Bischoff
 */
public class IncrementableAssignmentTest {

  private final FormulaFactory f = new FormulaFactory();
  private IncrementableAssignment as1;
  private IncrementableAssignment as2;

  @Before
  public void setUp() {
    Set<Literal> vars = new HashSet<>();
    vars.add(f.variable("a"));
    vars.add(f.variable("b"));
    vars.add(f.variable("c"));
    vars.add(f.literal("d", false));

    as1 = new IncrementableAssignment(vars);
    as2 = new IncrementableAssignment();

    vars.forEach(as2::addLiteral);
  }

  @Test
  public void testConstructor() throws Exception {
    assertTrue(as1.getVariableOrdering().size() == as2.getVariableOrdering().size());
    assertTrue(as1.formula(f) == as2.formula(f));

    for (Variable l : as1.getVariableOrdering()) {
      assertTrue(as2.getVariableOrdering().contains(l));
    }
    for (Variable l : as2.getVariableOrdering()) {
      assertTrue(as1.getVariableOrdering().contains(l));
    }
  }

  @Test
  public void testIncrement() throws Exception {
    assertTrue(as1.increment());
    assertTrue(as1.literals().contains(f.literal("d", true)));
    as1.increment();
    as1.increment();
    as1.increment();
    as1.increment();
    as1.increment();
    as1.increment();
    as1.increment();
    assertFalse(as1.increment());
    assertTrue(as1.pos.isEmpty());
  }

  @Test
  public void testAddLiteral() throws Exception {
    as1.addLiteral(f.variable("f"));
    assertTrue(as1.getVariableOrdering().contains(f.variable("f")));
  }
}