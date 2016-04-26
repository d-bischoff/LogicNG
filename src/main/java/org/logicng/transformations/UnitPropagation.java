package org.logicng.transformations;

import org.logicng.datastructures.Assignment;
import org.logicng.formulas.FType;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaTransformation;
import org.logicng.formulas.Literal;
import org.logicng.predicates.CNFPredicate;

import static org.logicng.formulas.cache.TransformationCacheEntry.UNITPROPAGATED;

/**
 * This transformation performes unit propagation on the given formula. It's assumed that the input formula is in CNF.
 * If the given formula is {@code f = g & x} where g is any formula and x is a literal, the resulting formula is {@code g[true/x]}.
 * The transformation performes unit propagation recursively until there are no more units to propagate.
 *
 * @author Daniel Bischoff
 * @version 1.0
 */
public class UnitPropagation implements FormulaTransformation {

  @Override
  public Formula apply(Formula formula, boolean cache) {
    Formula result = formula.transformationCacheEntry(UNITPROPAGATED);
    if (result != null)
      return result;
    if (!formula.holds(new CNFPredicate()))
      throw new IllegalArgumentException("Input formula must be in CNF for unit propagation.");
    switch (formula.type()) {
      case AND:
        Assignment assignment = new Assignment();
        for (Formula sub : formula)
          if (sub.type() == FType.LITERAL)  //unit detected
            assignment.addLiteral((Literal) sub);
        if (assignment.size() == 0)
          result = formula;
        else
          result = apply(formula.restrict(assignment), cache);
        break;
      default:
        result = formula;
    }
    if (cache)
      formula.setTransformationCacheEntry(UNITPROPAGATED, result);
    return result;
  }
}
