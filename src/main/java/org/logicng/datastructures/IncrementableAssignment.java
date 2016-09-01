package org.logicng.datastructures;

import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Created by
 * @author Daniel Bischoff
 * on 14.01.2016.
 */
public class IncrementableAssignment extends Assignment {

  private final SortedSet<Variable> variableOrdering = new TreeSet<>();

  /**
   * Constructs a new noFreeVars assignment (without fast evaluation).
   */
  public IncrementableAssignment() {
    super(false);
  }

  /**
   * Constructs a new noFreeVars assignment.
   *
   * @param fastEvaluable indicates whether this assignment should be evaluable fast.  If this parameter is set to
   *                      {@code true} the internal data structures will be optimized for fast evaluation but
   *                      creation of the object or adding literals can take longer.
   */
  public IncrementableAssignment(Boolean fastEvaluable) {
    super(fastEvaluable);
  }

  /**
   * Constructs a new assignment with a single literal assignment.
   *
   * @param lit           the literal
   * @param fastEvaluable indicates whether this assignment should be evaluable fast.  If this parameter is set to
   *                      {@code true} the internal data structures will be optimized for fast evaluation but
   *                      creation of the object or adding literals can take longer.
   */
  public IncrementableAssignment(Literal lit, boolean fastEvaluable) {
    super(lit, fastEvaluable);
    variableOrdering.add(lit.variable());
  }

  /**
   * Constructs a new assignment with a single literal assignment (without fast evaluation).
   *
   * @param lit the literal
   */
  public IncrementableAssignment(Literal lit) {
    super(lit, false);
    variableOrdering.add(lit.variable());
  }

  /**
   * Constructs a new assignment for a given collection of literals (without fast evaluation).
   * The order of the elements (e.g. if one inputs a List or SortedSet) dictates the ordering in the variable order of the assignment.
   *
   * @param lits a new assignment for a given collection of literals
   */
  public IncrementableAssignment(final Collection<? extends Literal> lits) {
    super(lits, false);
    lits.stream().<Variable>map(Literal::variable).collect(Collectors.toCollection(() -> variableOrdering));
  }

  /**
   * Constructs a new assignment for a given collection of literals.
   * The order of the elements (e.g. if one inputs a List or SortedSet) dictates the ordering in the variable order of the assignment.
   *
   * @param lits          a new assignment for a given collection of literals
   * @param fastEvaluable indicates whether this assignment should be evaluable fast.  If this parameter is set to
   *                      {@code true} the internal data structures will be optimized for fast evaluation but
   *                      creation of the object or adding literals can take longer.
   */
  public IncrementableAssignment(Collection<? extends Literal> lits, boolean fastEvaluable) {
    super(lits, fastEvaluable);
    lits.stream().<Variable>map(Literal::variable).collect(Collectors.toCollection(() -> variableOrdering));
  }

  /**
   * Increments the assignment by flipping the lowest negative variable in it and the positive one that are even lower.
   * E.g if the assignment looks like {@code x5=false x4=true x3=false x2=true x1=true} (where x1 is the lowest ordered variable and x5 the highest)
   * and increment is called, the resulting assignment looks like this: {@code x5=false x4=true x3=true x2=false x1=false}.
   * I.e. {@code x3} als lowest negative variable was flipped, and so where the even lower and positive {@code x2} and {@code x1}.
   * Note: if increment is called on an all-positive assignment, it will return false and assignes {@code false} to all variables.
   *
   * @return true if the assignment was incremented, false if this was not possible (all variables contained where already positive)
   */
  public boolean increment() {
    for (Variable v : variableOrdering) {
      if (neg.contains(v.negate())) {
        pos.add(v);
        neg.remove(v.negate());
        negVars.remove(v);
        return true;
      } else {
        assert (pos.contains(v));
        neg.add(v.negate());
        pos.remove(v);
        negVars.add(v);
      }
    }
    return false;
  }

  /**
   * Returns the ordering of the variables in this incrementable assignment.
   * The variables added first have low order, elements added later have a high order.
   *
   * @return the variable ordering
   */
  public SortedSet<Variable> getVariableOrdering() {
    return variableOrdering;
  }

  @Override
  /**
   * Add a single literal to this assignment. If is added as the highest element of the variable ordering.
   * @param lit the literal
   */
  public void addLiteral(Literal lit) {
    super.addLiteral(lit);
    variableOrdering.add(lit.variable());
  }
}
