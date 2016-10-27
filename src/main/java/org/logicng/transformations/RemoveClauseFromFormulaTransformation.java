package org.logicng.transformations;

import org.logicng.formulas.FType;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.FormulaTransformation;
import org.logicng.predicates.CNFPredicate;

import java.util.LinkedList;

/**
 * @author Daniel Bischoff on 13.06.2016. Copyright by Daniel Bischoff
 * @version 1.0
 * @since 1.0
 */
public class RemoveClauseFromFormulaTransformation implements FormulaTransformation {

  private final Formula clause;

  /**
   * Constructs a new transformation. The transformation removes
   * the clause given in this constructor from any CNF formula it is applied to.
   *
   * @param clause
   *     - the clause to be removed from other formulas. This formula has to be a clause, i.e. a disjunction of literals
   *     (potentially empty or single element disjunction)
   */
  public RemoveClauseFromFormulaTransformation(Formula clause) {
    if (!clause.type().equals(FType.AND)) {
      if (clause.holds(new CNFPredicate())) {
        this.clause = clause;
        return;
      }
    }
    throw new IllegalArgumentException("the formula given is not a clause");
  }

  /**
   * Removes the clause, this transformation was constructed with, from the formula.
   *
   * @param formula
   *     - the formula from which the clause shell be removed from. Must be in CNF.
   * @param cache
   *     - currently does nothing, i.e. is not supported for this transformation
   *
   * @return a cnf formula that does not contain the clause anymore. If the formula inserted was equal to the clause,
   * true is returned (empty conjunction).
   */
  @Override
  public Formula apply(Formula formula, boolean cache) {
    FormulaFactory fac = formula.factory();
    if (formula.holds(new CNFPredicate())) {
      assert (formula.holds(new CNFPredicate()));
      switch (formula.type()) {
        case TRUE:
        case FALSE:
          return formula;
        case LITERAL:
        case OR:
          if (formula.equals(clause))
            return fac.verum();
        case AND:
          LinkedList<Formula> remains = new LinkedList<>();
          for (Formula sub : formula) {
            assert sub.type().equals(FType.OR) || sub.type().equals(FType.LITERAL);
            if (!sub.equals(clause))
              remains.add(sub);
          }
          return fac.and(remains);
      }
    }
    throw new UnsupportedOperationException("Transformation not supported for this formula type. Formula must be CNF");
  }
}
