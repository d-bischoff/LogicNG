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

package org.logicng.formulas;

import org.logicng.configurations.Configuration;
import org.logicng.configurations.ConfigurationType;
import org.logicng.formulas.printer.DefaultStringRepresentation;
import org.logicng.formulas.printer.FormulaStringRepresentation;
import org.logicng.functions.SubNodeFunction;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PseudoBooleanParser;
import org.logicng.pseudobooleans.PBEncoder;
import org.logicng.transformations.cnf.CNFEncoder;
import org.logicng.util.Pair;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.logicng.formulas.FType.AND;
import static org.logicng.formulas.FType.FALSE;
import static org.logicng.formulas.FType.LITERAL;
import static org.logicng.formulas.FType.NOT;
import static org.logicng.formulas.FType.OR;
import static org.logicng.formulas.FType.TRUE;

/**
 * The formula factory for LogicNG.
 * <p>
 * New formulas can only be generated by a formula factory.  It is implemented s.t. it is guaranteed that equivalent
 * formulas (in terms of associativity and commutativity) are hold exactly once in memory.
 * <p>
 * A formula factory is NOT thread-safe.  If you generate formulas from more than one thread you either need to synchronize the formula factory
 * yourself or you use a formula factory for each single thread.
 * @version 1.1
 * @since 1.0
 */
public final class FormulaFactory {

  public static final String CC_PREFIX = "@RESERVED_CC_";
  public static final String PB_PREFIX = "@RESERVED_PB_";
  public static final String CNF_PREFIX = "@RESERVED_CNF_";

  private final String name;

  private final CFalse cFalse;
  private final CTrue cTrue;
  private final FormulaStringRepresentation stringRepresentation;
  private final Map<ConfigurationType, Configuration> configurations;
  private final String ccPrefix;
  private final String pbPrefix;
  private final String cnfPrefix;
  private final SubNodeFunction subformulaFunction;
  private final PBEncoder pbEncoder;
  private final CNFEncoder cnfEncoder;
  private final PseudoBooleanParser parser;
  private Map<String, Variable> posLiterals;
  private Map<String, Literal> negLiterals;
  private Set<Variable> generatedVariables;
  private Map<Formula, Not> nots;
  private Map<Pair<Formula, Formula>, Implication> implications;
  private Map<LinkedHashSet<? extends Formula>, Equivalence> equivalences;
  private Map<LinkedHashSet<? extends Formula>, And> ands2;
  private Map<LinkedHashSet<? extends Formula>, And> ands3;
  private Map<LinkedHashSet<? extends Formula>, And> ands4;
  private Map<LinkedHashSet<? extends Formula>, And> andsN;
  private Map<LinkedHashSet<? extends Formula>, Or> ors2;
  private Map<LinkedHashSet<? extends Formula>, Or> ors3;
  private Map<LinkedHashSet<? extends Formula>, Or> ors4;
  private Map<LinkedHashSet<? extends Formula>, Or> orsN;
  private Map<PBOperands, PBConstraint> pbConstraints;
  private boolean cnfCheck;
  private boolean[] formulaAdditionResult;
  private int ccCounter;
  private int pbCounter;
  private int cnfCounter;

  /**
   * Constructor for a new formula factory.
   * @param name                 the name of the factory
   * @param stringRepresentation the string representation of the formulas
   */
  public FormulaFactory(final String name, final FormulaStringRepresentation stringRepresentation) {
    this.name = name;
    this.cFalse = new CFalse(this);
    this.cTrue = new CTrue(this);
    this.clear();
    this.formulaAdditionResult = new boolean[2];
    this.stringRepresentation = stringRepresentation;
    this.configurations = new EnumMap<>(ConfigurationType.class);
    this.cnfEncoder = new CNFEncoder(this);
    this.subformulaFunction = new SubNodeFunction();
    if (!name.isEmpty()) {
      this.ccPrefix = CC_PREFIX + name + "_";
      this.pbPrefix = PB_PREFIX + name + "_";
      this.cnfPrefix = CNF_PREFIX + name + "_";
    } else {
      this.ccPrefix = CC_PREFIX;
      this.pbPrefix = PB_PREFIX;
      this.cnfPrefix = CNF_PREFIX;
    }
    this.pbEncoder = new PBEncoder(this);
    this.parser = new PseudoBooleanParser(this);
  }

  /**
   * Constructor for a new formula factory with a given name. This name is included in generated variables.
   * If you intent to mix formulas from different factories, you have to choose different names for the factories
   * to avoid name clashing of generated variables.
   * @param name the name of the factory
   */
  public FormulaFactory(final String name) {
    this(name, new DefaultStringRepresentation());
  }

  /**
   * Constructor for a new formula factory with a default empty name.
   * You should not mix formulas from formula factories without a name, since the names of generated variables will clash.
   */
  public FormulaFactory() {
    this("", new DefaultStringRepresentation());
  }

  /**
   * Returns {@code true} if a given list of formulas contains the negation of a  given formula,
   * {@code false} otherwise.
   * @param formulas the list of formulas
   * @param f        the formula
   * @return {@code true} if a given list of formulas contains a given formula, {@code false} otherwise
   */
  private static boolean containsComplement(final LinkedHashSet<Formula> formulas, Formula f) {
    return formulas.contains(f.negate());
  }

  /**
   * Removes all formulas from the factory cache.
   */
  public void clear() {
    this.posLiterals = new HashMap<>();
    this.negLiterals = new HashMap<>();
    this.generatedVariables = new HashSet<>();
    this.nots = new HashMap<>();
    this.implications = new HashMap<>();
    this.equivalences = new HashMap<>();
    this.ands2 = new HashMap<>();
    this.ands3 = new HashMap<>();
    this.ands4 = new HashMap<>();
    this.andsN = new HashMap<>();
    this.ors2 = new HashMap<>();
    this.ors3 = new HashMap<>();
    this.ors4 = new HashMap<>();
    this.orsN = new HashMap<>();
    this.pbConstraints = new HashMap<>();
    this.ccCounter = 0;
    this.pbCounter = 0;
    this.cnfCounter = 0;
  }

  /**
   * Returns the name of this formula factory.
   * @return the name of this formula factory
   */
  public String name() {
    return this.name;
  }

  /**
   * Returns the configuration for a given configuration type or {@code null} if there isn't any.
   * @param cType the configuration type
   * @return the configuration for a given configuration type
   */
  public Configuration configurationFor(final ConfigurationType cType) {
    return this.configurations.get(cType);
  }

  /**
   * Puts a new configuration into the configuration database.  If there is already a configuration present for this
   * type, it will be overwritten.
   * @param configuration the configuration
   */
  public void putConfiguration(final Configuration configuration) {
    this.configurations.put(configuration.type(), configuration);
  }

  /**
   * Returns a function to compute the sub-formulas.
   * @return a function to compute the sub-formulas
   */
  public SubNodeFunction subformulaFunction() {
    return this.subformulaFunction;
  }

  /**
   * Returns the default pseudo-Boolean encoder of this formula factory.
   * @return the default pseudo-Boolean encoder of this formula factory
   */
  public PBEncoder pbEncoder() {
    return this.pbEncoder;
  }

  /**
   * Returns the default CNF encoder of this formula factory.
   * @return the default CNF encoder of this formula factory
   */
  public CNFEncoder cnfEncoder() {
    return this.cnfEncoder;
  }

  /**
   * Creates a new binary operator with a given type and two operands.
   * @param type  the type of the formula
   * @param left  the left-hand side operand
   * @param right the right-hand side operand
   * @return the newly generated formula
   * @throws IllegalArgumentException if a wrong formula type is passed
   */
  public Formula binaryOperator(final FType type, final Formula left, final Formula right) {
    switch (type) {
      case IMPL:
        return this.implication(left, right);
      case EQUIV:
        return this.equivalence(left, right);
      default:
        throw new IllegalArgumentException("Cannot create a binary formula with operator: " + type);
    }
  }

  /**
   * Creates a new implication.
   * @param left  the left-hand side operand
   * @param right the right-hand side operand
   * @return a new implication
   */
  public Formula implication(final Formula left, final Formula right) {
    if (left.type() == FALSE || right.type() == TRUE)
      return this.verum();
    if (left.type() == TRUE)
      return right;
    if (right.type() == FALSE)
      return this.not(left);
    if (left.equals(right))
      return this.verum();
    final Pair<Formula, Formula> key = new Pair<>(left, right);
    Implication implication = this.implications.get(key);
    if (implication == null) {
      implication = new Implication(left, right, this);
      this.implications.put(key, implication);
    }
    return implication;
  }

  /**
   * Creates a new equivalence.
   * @param left  the left-hand side operand
   * @param right the right-hand side operand
   * @return a new equivalence
   */
  public Formula equivalence(final Formula left, final Formula right) {
    if (left.type() == TRUE)
      return right;
    if (right.type() == TRUE)
      return left;
    if (left.type() == FALSE)
      return this.not(right);
    if (right.type() == FALSE)
      return this.not(left);
    if (left.equals(right))
      return this.verum();
    if (left.equals(right.negate()))
      return this.falsum();
    final LinkedHashSet<Formula> key = new LinkedHashSet<>(Arrays.asList(left, right));
    Equivalence equivalence = this.equivalences.get(key);
    if (equivalence == null) {
      equivalence = new Equivalence(left, right, this);
      this.equivalences.put(key, equivalence);
    }
    return equivalence;
  }

  /**
   * Returns a (singleton) object for the constant "True".
   * @return an object for the constant "True"
   */
  public CTrue verum() {
    return this.cTrue;
  }

  /**
   * Creates the negation of a given formula.
   * <p>
   * Constants, literals and negations are negated directly and returned.
   * For all other formulas a new {@code Not} object is returned.
   * @param operand the given formula
   * @return the negated formula
   */
  public Formula not(final Formula operand) {
    if (operand.type() == LITERAL || operand.type() == FALSE || operand.type() == TRUE || operand.type() == NOT)
      return operand.negate();
    Not not = this.nots.get(operand);
    if (not == null) {
      not = new Not(operand, this);
      this.nots.put(operand, not);
    }
    return not;
  }

  /**
   * Returns a (singleton) object for the constant "False".
   * @return an object for the constant "False"
   */
  public CFalse falsum() {
    return this.cFalse;
  }

  /**
   * Creates a new n-ary operator with a given type and a list of operands.
   * @param type     the type of the formula
   * @param operands the list of operands
   * @return the newly generated formula
   * @throws IllegalArgumentException if a wrong formula type is passed
   */
  public Formula naryOperator(final FType type, final Collection<? extends Formula> operands) {
    return this.naryOperator(type, operands.toArray(new Formula[operands.size()]));
  }

  /**
   * Creates a new n-ary operator with a given type and a list of operands.
   * @param type     the type of the formula
   * @param operands the list of operands
   * @return the newly generated formula
   * @throws IllegalArgumentException if a wrong formula type is passed
   */
  public Formula naryOperator(final FType type, final Formula... operands) {
    switch (type) {
      case OR:
        return this.or(operands);
      case AND:
        return this.and(operands);
      default:
        throw new IllegalArgumentException("Cannot create an n-ary formula with operator: " + type);
    }
  }

  /**
   * Creates a new conjunction from an array of formulas.
   * @param operands the vector of formulas
   * @return a new conjunction
   */
  public Formula and(final Formula... operands) {
    final LinkedHashSet<Formula> ops = new LinkedHashSet<>(operands.length);
    Collections.addAll(ops, operands);
    return this.constructAnd(ops);
  }

  /**
   * Creates a new conjunction from a collection of formulas.
   * <p>
   * Note: The LinkedHashSet is used to eliminate duplicate sub-formulas and to respect the commutativity of operands.
   * @param operands the array of formulas
   * @return a new conjunction
   */
  public Formula and(final Collection<? extends Formula> operands) {
    final LinkedHashSet<Formula> ops = new LinkedHashSet<>(operands);
    return this.constructAnd(ops);
  }

  /**
   * Creates a new conjunction.
   * @param operands the formulas
   * @return a new conjunction
   */
  private Formula constructAnd(final LinkedHashSet<? extends Formula> operands) {
    And tempAnd = null;
    Map<LinkedHashSet<? extends Formula>, And> opAndMap = this.andsN;
    if (operands.size() > 1) {
      switch (operands.size()) {
        case 2:
          opAndMap = this.ands2;
          break;
        case 3:
          opAndMap = this.ands3;
          break;
        case 4:
          opAndMap = this.ands4;
          break;
        default:
          break;
      }
      tempAnd = opAndMap.get(operands);
    }
    if (tempAnd != null)
      return tempAnd;
    LinkedHashSet<? extends Formula> condensedOperands = operands.size() < 2
            ? operands
            : this.condenseOperandsAnd(operands);
    if (condensedOperands == null)
      return this.falsum();
    if (condensedOperands.isEmpty())
      return this.verum();
    if (condensedOperands.size() == 1)
      return condensedOperands.iterator().next();
    And and;
    Map<LinkedHashSet<? extends Formula>, And> condAndMap = this.andsN;
    switch (condensedOperands.size()) {
      case 2:
        condAndMap = this.ands2;
        break;
      case 3:
        condAndMap = this.ands3;
        break;
      case 4:
        condAndMap = this.ands4;
        break;
      default:
        break;
    }
    and = condAndMap.get(condensedOperands);
    if (and == null) {
      tempAnd = new And(condensedOperands, this, this.cnfCheck);
      opAndMap.put(operands, tempAnd);
      condAndMap.put(condensedOperands, tempAnd);
      return tempAnd;
    }
    opAndMap.put(operands, and);
    return and;
  }

  /**
   * Creates a new CNF from an array of clauses.
   * <p>
   * ATTENTION: it is assumed that the operands are really clauses - this is not checked for performance reasons.
   * Also no reduction of operands is performed - this method should only be used if you are sure that the CNF is free
   * of redundant clauses.
   * @param clauses the array of clauses
   * @return a new CNF
   */
  public Formula cnf(final Formula... clauses) {
    final LinkedHashSet<Formula> ops = new LinkedHashSet<>(clauses.length);
    Collections.addAll(ops, clauses);
    return this.constructCNF(ops);
  }

  /**
   * Creates a new CNF from a collection of clauses.
   * <p>
   * ATTENTION: it is assumed that the operands are really clauses - this is not checked for performance reasons.
   * Also no reduction of operands is performed - this method should only be used if you are sure that the CNF is free
   * of redundant clauses.
   * @param clauses the collection of clauses
   * @return a new CNF
   */
  public Formula cnf(final Collection<? extends Formula> clauses) {
    final LinkedHashSet<? extends Formula> ops = new LinkedHashSet<>(clauses);
    return this.constructCNF(ops);
  }

  /**
   * Creates a new CNF.
   * @param clauses the clauses
   * @return a new CNF
   */
  private Formula constructCNF(final LinkedHashSet<? extends Formula> clauses) {
    if (clauses.isEmpty())
      return this.verum();
    if (clauses.size() == 1)
      return clauses.iterator().next();
    Map<LinkedHashSet<? extends Formula>, And> opAndMap = this.andsN;
    switch (clauses.size()) {
      case 2:
        opAndMap = this.ands2;
        break;
      case 3:
        opAndMap = this.ands3;
        break;
      case 4:
        opAndMap = this.ands4;
        break;
      default:
        break;
    }
    And tempAnd = opAndMap.get(clauses);
    if (tempAnd != null)
      return tempAnd;
    tempAnd = new And(clauses, this, true);
    opAndMap.put(clauses, tempAnd);
    return tempAnd;
  }

  /**
   * Creates a new disjunction from an array of formulas.
   * @param operands the list of formulas
   * @return a new disjunction
   */
  public Formula or(final Formula... operands) {
    final LinkedHashSet<Formula> ops = new LinkedHashSet<>(operands.length);
    Collections.addAll(ops, operands);
    return this.constructOr(ops);
  }

  /**
   * Creates a new disjunction from a collection of formulas.
   * <p>
   * Note: The LinkedHashSet is used to eliminate duplicate sub-formulas and to respect the commutativity of operands.
   * @param operands the collection of formulas
   * @return a new disjunction
   */
  public Formula or(final Collection<? extends Formula> operands) {
    final LinkedHashSet<Formula> ops = new LinkedHashSet<>(operands);
    return this.constructOr(ops);
  }

  /**
   * Creates a new disjunction.
   * @param operands the formulas
   * @return a new disjunction
   */
  private Formula constructOr(final LinkedHashSet<? extends Formula> operands) {
    Or tempOr = null;
    Map<LinkedHashSet<? extends Formula>, Or> opOrMap = this.orsN;
    if (operands.size() > 1) {
      switch (operands.size()) {
        case 2:
          opOrMap = this.ors2;
          break;
        case 3:
          opOrMap = this.ors3;
          break;
        case 4:
          opOrMap = this.ors4;
          break;
        default:
          break;
      }
      tempOr = opOrMap.get(operands);
    }
    if (tempOr != null)
      return tempOr;
    LinkedHashSet<? extends Formula> condensedOperands = operands.size() < 2
            ? operands
            : this.condenseOperandsOr(operands);
    if (condensedOperands == null)
      return this.verum();
    if (condensedOperands.isEmpty())
      return this.falsum();
    if (condensedOperands.size() == 1)
      return condensedOperands.iterator().next();
    Or or;
    Map<LinkedHashSet<? extends Formula>, Or> condOrMap = this.orsN;
    switch (condensedOperands.size()) {
      case 2:
        condOrMap = this.ors2;
        break;
      case 3:
        condOrMap = this.ors3;
        break;
      case 4:
        condOrMap = this.ors4;
        break;
      default:
        break;
    }
    or = condOrMap.get(condensedOperands);
    if (or == null) {
      tempOr = new Or(condensedOperands, this, this.cnfCheck);
      opOrMap.put(operands, tempOr);
      condOrMap.put(condensedOperands, tempOr);
      return tempOr;
    }
    opOrMap.put(operands, or);
    return or;
  }

  /**
   * Creates a new clause from an array of literals.
   * <p>
   * ATTENTION:  No reduction of operands is performed - this method should only be used if you are sure that the clause
   * is free of redundant or contradicting literals.
   * @param literals the collection of literals
   * @return a new clause
   */
  public Formula clause(final Literal... literals) {
    final LinkedHashSet<Literal> ops = new LinkedHashSet<>(literals.length);
    Collections.addAll(ops, literals);
    return this.constructClause(ops);
  }

  /**
   * Creates a new clause from a collection of literals.
   * <p>
   * ATTENTION:  No reduction of operands is performed - this method should only be used if you are sure that the clause
   * is free of contradicting literals.
   * @param literals the collection of literals
   * @return a new clause
   */
  public Formula clause(final Collection<? extends Literal> literals) {
    final LinkedHashSet<Literal> ops = new LinkedHashSet<>(literals);
    return this.constructClause(ops);
  }

  /**
   * Creates a new clause.
   * @param literals the literals
   * @return a new clause
   */
  private Formula constructClause(final LinkedHashSet<Literal> literals) {
    if (literals.isEmpty())
      return this.falsum();
    if (literals.size() == 1)
      return literals.iterator().next();
    Or tempOr = null;
    Map<LinkedHashSet<? extends Formula>, Or> opOrMap = this.orsN;
    if (literals.size() > 1) {
      switch (literals.size()) {
        case 2:
          opOrMap = this.ors2;
          break;
        case 3:
          opOrMap = this.ors3;
          break;
        case 4:
          opOrMap = this.ors4;
          break;
        default:
          break;
      }
      tempOr = opOrMap.get(literals);
    }
    if (tempOr != null)
      return tempOr;
    tempOr = new Or(literals, this, true);
    opOrMap.put(literals, tempOr);
    return tempOr;
  }

  /**
   * Creates a new literal instance with a given name and phase.
   * <p>
   * Literal names should not start with {@code @RESERVED} - these are reserved for internal literals.
   * @param name  the literal name
   * @param phase the literal phase
   * @return a new literal with the given name and phase
   */
  public Literal literal(final String name, boolean phase) {
    if (phase)
      return this.variable(name);
    else {
      Literal lit = this.negLiterals.get(name);
      if (lit == null) {
        lit = new Literal(name, false, this);
        this.negLiterals.put(name, lit);
      }
      return lit;
    }
  }

  /**
   * Creates a new literal instance with a given name and positive phase.
   * @param name the variable name
   * @return a new literal with the given name and positive phase
   */
  public Variable variable(final String name) {
    Variable var = this.posLiterals.get(name);
    if (var == null) {
      var = new Variable(name, this);
      this.posLiterals.put(name, var);
    }
    return var;
  }

  /**
   * Creates a new pseudo-Boolean constraint.
   * @param comparator   the comparator of the constraint
   * @param rhs          the right-hand side of the constraint
   * @param literals     the literals of the constraint
   * @param coefficients the coefficients of the constraint
   * @return the pseudo-Boolean constraint
   * @throws IllegalArgumentException if the number of literals and coefficients do not correspond
   */
  public PBConstraint pbc(final CType comparator, int rhs, final List<? extends Literal> literals, final List<Integer> coefficients) {
    int[] cfs = new int[coefficients.size()];
    for (int i = 0; i < coefficients.size(); i++)
      cfs[i] = coefficients.get(i);
    return this.constructPBC(comparator, rhs, literals.toArray(new Literal[literals.size()]), cfs);
  }

  /**
   * Creates a new pseudo-Boolean constraint.
   * @param comparator   the comparator of the constraint
   * @param rhs          the right-hand side of the constraint
   * @param literals     the literals of the constraint
   * @param coefficients the coefficients of the constraint
   * @return the pseudo-Boolean constraint
   * @throws IllegalArgumentException if the number of literals and coefficients do not correspond
   */
  public PBConstraint pbc(final CType comparator, int rhs, final Literal[] literals, final int[] coefficients) {
    return constructPBC(comparator, rhs, Arrays.copyOf(literals, literals.length), Arrays.copyOf(coefficients, coefficients.length));
  }

  private PBConstraint constructPBC(final CType comparator, int rhs, final Literal[] literals, final int[] coefficients) {
    final PBOperands operands = new PBOperands(literals, coefficients, comparator, rhs);
    PBConstraint constraint = this.pbConstraints.get(operands);
    if (constraint == null) {
      constraint = new PBConstraint(literals, coefficients, comparator, rhs, this);
      this.pbConstraints.put(operands, constraint);
    }
    return constraint;
  }

  /**
   * Creates a new cardinality constraint.
   * @param variables  the variables of the constraint
   * @param comparator the comparator of the constraint
   * @param rhs        the right-hand side of the constraint
   * @return the cardinality constraint
   * @throws IllegalArgumentException if there are negative variables
   */
  public PBConstraint cc(final CType comparator, int rhs, final Collection<Variable> variables) {
    final int[] coefficients = new int[variables.size()];
    Arrays.fill(coefficients, 1);
    final Variable[] vars = new Variable[variables.size()];
    int count = 0;
    for (final Variable var : variables)
      vars[count++] = var;
    return this.constructPBC(comparator, rhs, vars, coefficients);
  }

  /**
   * Creates a new cardinality constraint.
   * @param variables  the variables of the constraint
   * @param comparator the comparator of the constraint
   * @param rhs        the right-hand side of the constraint
   * @return the cardinality constraint
   * @throws IllegalArgumentException if there are negative variables
   */
  public PBConstraint cc(final CType comparator, int rhs, final Variable... variables) {
    final int[] coefficients = new int[variables.length];
    Arrays.fill(coefficients, 1);
    final Variable[] vars = new Variable[variables.length];
    int count = 0;
    for (final Variable var : variables)
      vars[count++] = var;
    return this.constructPBC(comparator, rhs, vars, coefficients);
  }

  /**
   * Creates a new at-most-one cardinality constraint.
   * @param variables the variables of the constraint
   * @return the at-most-one constraint
   * @throws IllegalArgumentException if there are negative variables
   */
  public PBConstraint amo(final Collection<Variable> variables) {
    return this.cc(CType.LE, 1, variables);
  }

  /**
   * Creates a new at-most-one cardinality constraint.
   * @param variables the variables of the constraint
   * @return the at-most-one constraint
   * @throws IllegalArgumentException if there are negative variables
   */
  public PBConstraint amo(final Variable... variables) {
    return this.cc(CType.LE, 1, variables);
  }

  /**
   * Creates a new exactly-one cardinality constraint.
   * @param variables the variables of the constraint
   * @return the exactly-one constraint
   * @throws IllegalArgumentException if there are negative variables
   */
  public PBConstraint exo(final Collection<Variable> variables) {
    return this.cc(CType.EQ, 1, variables);
  }

  /**
   * Creates a new exactly-one cardinality constraint.
   * @param variables the variables of the constraint
   * @return the exactly-one constraint
   * @throws IllegalArgumentException if there are negative variables
   */
  public PBConstraint exo(final Variable... variables) {
    return this.cc(CType.EQ, 1, variables);
  }

  /**
   * Returns a new cardinality constraint auxiliary literal.
   * <p>
   * Remark: currently only the counter is increased - there is no check if the literal is already present.
   * @return the new cardinality constraint auxiliary literal
   */
  public Variable newCCVariable() {
    final Variable var = this.variable(this.ccPrefix + this.ccCounter++);
    this.generatedVariables.add(var);
    return var;
  }

  /**
   * Returns a new pseudo Boolean auxiliary literal.
   * <p>
   * Remark: currently only the counter is increased - there is no check if the literal is already present.
   * @return the new pseudo Boolean auxiliary literal
   */
  public Variable newPBVariable() {
    final Variable var = this.variable(this.pbPrefix + this.pbCounter++);
    this.generatedVariables.add(var);
    return var;
  }

  /**
   * Returns a new CNF auxiliary literal.
   * <p>
   * Remark: currently only the counter is increased - there is no check if the literal is already present.
   * @return the new CNF auxiliary literal
   */
  public Variable newCNFVariable() {
    final Variable var = this.variable(this.cnfPrefix + this.cnfCounter++);
    this.generatedVariables.add(var);
    return var;
  }

  /**
   * Returns a condensed array of operands for a given n-ary disjunction.
   * @param operands the formulas
   * @return a condensed array of operands
   */
  private LinkedHashSet<Formula> condenseOperandsOr(Collection<? extends Formula> operands) {
    final LinkedHashSet<Formula> ops = new LinkedHashSet<>();
    this.cnfCheck = true;
    for (Formula form : operands)
      if (form.type() == OR) {
        for (Formula f : ((NAryOperator) form).operands) {
          this.addFormulaOr(ops, f);
          if (!formulaAdditionResult[0])
            return null;
          if (!formulaAdditionResult[1])
            this.cnfCheck = false;
        }
      } else {
        this.addFormulaOr(ops, form);
        if (!formulaAdditionResult[0])
          return null;
        if (!formulaAdditionResult[1])
          this.cnfCheck = false;
      }
    return ops;
  }

  /**
   * Returns a condensed array of operands for a given n-ary conjunction.
   * @param operands the formulas
   * @return a condensed array of operands
   */
  private LinkedHashSet<Formula> condenseOperandsAnd(Collection<? extends Formula> operands) {
    final LinkedHashSet<Formula> ops = new LinkedHashSet<>();
    this.cnfCheck = true;
    for (Formula form : operands)
      if (form.type() == AND) {
        for (Formula f : ((NAryOperator) form).operands) {
          this.addFormulaAnd(ops, f);
          if (!formulaAdditionResult[0])
            return null;
          if (!formulaAdditionResult[1])
            this.cnfCheck = false;
        }
      } else {
        this.addFormulaAnd(ops, form);
        if (!formulaAdditionResult[0])
          return null;
        if (!formulaAdditionResult[1])
          this.cnfCheck = false;
      }
    return ops;
  }

  /**
   * Returns {@code true} if the given variable was generated, {@code false} otherwise.
   * @param var the variable to check
   * @return {@code true} if the given variable was generated
   */
  public boolean isGeneratedVariable(final Variable var) {
    return this.generatedVariables.contains(var);
  }

  /**
   * Returns the number of internal nodes of a given formula.
   * @param formula the formula
   * @return the number of internal nodes
   */
  public long numberOfNodes(final Formula formula) {
    return formula.apply(this.subformulaFunction).size();
  }

  /**
   * Parses a given string to a formula using a pseudo boolean parser.
   * @param string a string representing the formula
   * @return the formula
   * @throws ParserException if the parser throws an exception
   */
  public Formula parse(final String string) throws ParserException {
    return parser.parse(string);
  }

  /**
   * Adds a given formula to a list of operands.  If the formula is the neutral element for the respective n-ary
   * operation it will be skipped.  If a complementary formula is already present in the list of operands or the
   * formula is the dual element, {@code false} is stored as first element of the result array,
   * otherwise {@code true} is the first element of the result array.  If the added formula was a literal, the second
   * element in the result array is {@code true}, {@code false} otherwise.
   * @param ops the list of operands
   * @param f   the formula
   */
  private void addFormulaOr(final LinkedHashSet<Formula> ops, final Formula f) {
    if (f.type == FALSE) {
      formulaAdditionResult[0] = true;
      formulaAdditionResult[1] = true;
    } else if (f.type == TRUE || containsComplement(ops, f)) {
      formulaAdditionResult[0] = false;
      formulaAdditionResult[1] = false;
    } else {
      ops.add(f);
      formulaAdditionResult[0] = true;
      formulaAdditionResult[1] = f.type == LITERAL;
    }
  }

  /**
   * Adds a given formula to a list of operands.  If the formula is the neutral element for the respective n-ary
   * operation it will be skipped.  If a complementary formula is already present in the list of operands or the
   * formula is the dual element, {@code false} is stored as first element of the result array,
   * otherwise {@code true} is the first element of the result array.  If the added formula was a clause, the second
   * element in the result array is {@code true}, {@code false} otherwise.
   * @param ops the list of operands
   * @param f   the formula
   */
  private void addFormulaAnd(final LinkedHashSet<Formula> ops, final Formula f) {
    if (f.type() == TRUE) {
      formulaAdditionResult[0] = true;
      formulaAdditionResult[1] = true;
    } else if (f.type == FALSE || containsComplement(ops, f)) {
      formulaAdditionResult[0] = false;
      formulaAdditionResult[1] = false;
    } else {
      ops.add(f);
      formulaAdditionResult[0] = true;
      formulaAdditionResult[1] = f.type == LITERAL || f.type == OR && ((Or) f).isCNFClause();
    }
  }

  /**
   * Returns a string representation of a formula with this factories string representation
   * @param formula the formula
   * @return the string representation
   */
  public String string(final Formula formula) {
    return this.stringRepresentation.toString(formula);
  }

  /**
   * Returns a string representation of a formula with this factories string representation
   * @param formula              the formula
   * @param stringRepresentation the string representation
   * @return the string representation
   */
  public String string(final Formula formula, final FormulaStringRepresentation stringRepresentation) {
    return stringRepresentation.toString(formula);
  }  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Name:              ").append(this.name).append("\n");
    sb.append("Positive Literals: ").append(this.posLiterals.size()).append("\n");
    sb.append("Negative Literals: ").append(this.negLiterals.size()).append("\n");
    sb.append("Negations:         ").append(this.nots.size()).append("\n");
    sb.append("Implications:      ").append(this.implications.size()).append("\n");
    sb.append("Equivalences:      ").append(this.equivalences.size()).append("\n");
    sb.append("Conjunctions (2):  ").append(this.ands2.size()).append("\n");
    sb.append("Conjunctions (3):  ").append(this.ands3.size()).append("\n");
    sb.append("Conjunctions (4):  ").append(this.ands4.size()).append("\n");
    sb.append("Conjunctions (>4): ").append(this.andsN.size()).append("\n");
    sb.append("Disjunctions (2):  ").append(this.ors2.size()).append("\n");
    sb.append("Disjunctions (3):  ").append(this.ors3.size()).append("\n");
    sb.append("Disjunctions (4):  ").append(this.ors4.size()).append("\n");
    sb.append("Disjunctions (>4): ").append(this.orsN.size()).append("\n");
    return sb.toString();
  }

  /**
   * Helper class for the operands of a pseudo-Boolean constraint.
   */
  private static final class PBOperands {
    private final Literal[] literals;
    private final int[] coefficients;
    private final CType comparator;
    private final int rhs;

    /**
     * Constructs a new instance.
     * @param literals     the literals of the constraint
     * @param coefficients the coefficients of the constraint
     * @param comparator   the comparator of the constraint
     * @param rhs          the right-hand side of the constraint
     */
    public PBOperands(final Literal[] literals, final int[] coefficients, final CType comparator, int rhs) {
      this.literals = literals;
      this.coefficients = coefficients;
      this.comparator = comparator;
      this.rhs = rhs;
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.rhs, this.comparator, Arrays.hashCode(coefficients), Arrays.hashCode(literals));
    }

    @Override
    public boolean equals(final Object other) {
      if (this == other)
        return true;
      if (other instanceof PBOperands) {
        final PBOperands o = (PBOperands) other;
        return this.rhs == o.rhs && this.comparator == o.comparator
                && Arrays.equals(this.coefficients, o.coefficients)
                && Arrays.equals(this.literals, o.literals);
      }
      return false;
    }
  }




}
