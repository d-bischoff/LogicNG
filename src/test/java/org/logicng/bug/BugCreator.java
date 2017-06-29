package org.logicng.bug;

import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.predicates.satisfiability.TautologyPredicate;

import java.util.*;

public class BugCreator {

  private static FormulaFactory factory = new FormulaFactory();
  private final static boolean PRODUCE_BUG = true;

  public static void main(String[] args) {
    ArrayList<Formula> list1 = new ArrayList<>();
    Formula f1 = factory.variable("X");
    Formula f2 = factory.variable("Y");
    list1.add(f1);
    list1.add(f2);

    Formula f3 = factory.variable("Z");
    Formula f4 = factory.and(f3, factory.not(f1));

    bar(list1);
    assert (!(factory.equivalence(f3, f4).holds(new TautologyPredicate(factory)))); // Z != Z & ~X
  }

  private static void bar(List<Formula> listOfFormulas) {
    for (Formula subFormula : listOfFormulas) {
      Formula union = factory.falsum();
      for (Formula subFormula2 : listOfFormulas)
        union = factory.or(union, subFormula2);
      for (Formula clause : union)
        foo(subFormula, clause);
    }
  }

  private static void foo(Formula dnf, Formula f) {
    SortedSet<Literal> cs;
    if (PRODUCE_BUG)
      cs = f.literals();
    else
      cs = new TreeSet<>(f.literals());

    SortedSet<Literal> removal = new TreeSet<>();

    //Formulas should be immutable. However they are not since you can edit formula.literals as it seems.
    //After we messed with the literals other - unrelated - checks, fail.


    //----Bug happens here. Most simple case
    for (Literal l : cs)
      if (dnf.literals().contains(l))
        removal.add(l);
    cs.removeAll(removal);
    //----Bug happens here even though no concurrentModificationException happens----
    //Iterator<Literal> it = cs.iterator();
    //while (it.hasNext()) {
    //  Literal l = it.next();
    //  if (dnf.literals().contains(l))
    //    cs.remove(l);
    //}
    //---------------------------the same with forEach--------------------------------
    //for(Literal l : cs)
    //if(dnf.literals().contains(l))
    //cs.remove(l);
    //--------------If using language level 8, this is the same problem:-------------
    //cs.removeIf(lit -> dnf.literals().contains(lit)); //Bug happens here
    //--------------------------------------------------------------------------------
  }

}
