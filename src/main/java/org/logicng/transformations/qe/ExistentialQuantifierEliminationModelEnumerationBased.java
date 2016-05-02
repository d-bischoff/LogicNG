package org.logicng.transformations.qe;

import org.logicng.datastructures.Assignment;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.FormulaTransformation;
import org.logicng.formulas.Variable;
import org.logicng.predicates.CNFPredicate;
import org.logicng.solvers.MiniSat;

import java.util.Arrays;
import java.util.Collection;

/**
 * This transformation eliminates a number of existentially quantified variables by a model enumeration based algorithm.
 * This method for quantifier elimination can be faster if the number of free variables in the formula is small.
 * This transformation cannot be cached since it is dependent on the set of free variables.
 *
 * @version 1.0.0
 */
public class ExistentialQuantifierEliminationModelEnumerationBased implements FormulaTransformation {

  private final Variable[] freeVariables;

  /**
   * Constructs a new existential quantifier elimination for the given collection of free variables.
   * Any formulas constructed with this transformation will only contain those free variables.
   *
   * @param freeVariables the variables which are NOT quantified in the formula
   */
  public ExistentialQuantifierEliminationModelEnumerationBased(final Collection<Variable> freeVariables) {
    this.freeVariables = freeVariables.toArray(new Variable[freeVariables.size()]);
  }

  public ExistentialQuantifierEliminationModelEnumerationBased(final Variable... variables) {
    this.freeVariables = Arrays.copyOf(variables, variables.length);
  }

  @Override
  public Formula apply(Formula input, boolean cache) {
    if (!input.holds(new CNFPredicate()))
      throw new IllegalArgumentException("The formula must be in CNF");
    FormulaFactory factory = input.factory();
    MiniSat solver = MiniSat.miniSat(factory);
    solver.add(input);
    Formula output = factory.falsum();
    while (solver.sat() == Tristate.TRUE) {
      Assignment model = solver.model(freeVariables);
      output = factory.or(output, model.formula(factory));
      solver.add(model.blockingClause(factory, null));
    }
    return output;
  }
}
