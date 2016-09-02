package org.logicng.solvers.sat;

import org.junit.Test;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ForbearingParser;
import org.logicng.solvers.MiniSat;
import org.logicng.transformations.dnf.DNFFactorization;

/**
 * @author Daniel Bischoff
 *         on 01.09.2016.
 *         <p>
 *         Copyright by Daniel Bischoff
 */
public class MiniSat2SolverDiagramGenerationTest {

  @Test
  public void testSomething() {
    try {
      FormulaFactory f = new FormulaFactory();
      ForbearingParser p = new ForbearingParser(f);

      MiniSat orig = MiniSat.miniSat(f);
      MiniSat diagram = MiniSat.diagramSat(f);
      Formula xAndy = p.parse("x/(a+-b/c)/(-y+-a)/(a+(y+x+-c/d))").cnf();
      System.out.println(xAndy.transform(new DNFFactorization()));
      orig.add(xAndy);
      diagram.add(xAndy);
      if (Tristate.TRUE == orig.sat())
        System.out.println("solvable by" + orig.model());
      if (Tristate.TRUE == diagram.sat())
        System.out.println("something i guess");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}