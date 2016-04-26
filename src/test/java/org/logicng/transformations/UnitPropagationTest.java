package org.logicng.transformations;

import org.junit.Test;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.PropositionalParser;

import static org.junit.Assert.assertEquals;

/**
 * Created by Daniel Bischoff on 28.01.2016. Copyright by Daniel Bischoff
 */
public class UnitPropagationTest {

  final private FormulaFactory factory = new FormulaFactory();
  final private PropositionalParser parser = new PropositionalParser(factory);

  @Test
  public void testComplex() throws Exception {
    Formula f1 = parser.parse("X1 & X2 & X3 & X4 & X5 & X6 & X7 & X8 & X9 & X10 & X11 & X12 & X13 & X14 & X15 & X16 & X17 & X18 & X19 & X20 & X21 & X22 & X23 & X24 &" +
            " X25 & X26 & X27 & X28 & X29 & U25 & Y494 & X30 & X31 & X32 & (~X1 | L | R) & (~X2 | ~L | ~R) & (~X3 | Y608 | Y628 | X33)" +
            " & (~X33 | ~Y608) & (~X33 | ~Y628) & (~X4 | Y494 | Y460 | Y830 | X34 | Y498) & (~X34 | ~Y494) & (~X34 | ~Y460) & (~X34 | ~Y498)" +
            " & (~X34 | ~Y830) & (~X5 | ~Y498 | ~Y494) & (~X6 | ~Y498 | ~Y460) & (~X7 | ~Y498 | ~Y830) & (~X8 | ~Y494 | ~Y460) & (~X9 | ~Y494 | ~Y830)" +
            " & (~X10 | ~Y460 | ~Y830) & (~X11 | Y631 | Y632 | X35) & (~X35 | ~Y631) & (~X35 | ~Y632) & (~X12 | X36 | X37 | Y641 | Y642 | Y640) & (~X36 | X38)" +
            " & (~X36 | X39) & (~X38 | Y460 | Y494) & (~X39 | Y631 | Y632) & (~X37 | X39) & (~X37 | ~Y460) & (~X37 | ~Y494) & (~X13 | X40 | X35 | X41)" +
            " & (~X40 | ~Y460) & (~X40 | ~Y494) & (~X41 | X42) & (~X41 | ~Y641) & (~X41 | ~Y642) & (~X41 | ~Y640) & (~X42 | X35 | Y460 | Y494)" +
            " & (~X14 | X43 | FS | FX) & (~X43 | ~FS) & (~X43 | ~FX) & (~X15 | X44 | Z06 | Z07) & (~X44 | ~Z06) & (~X44 | ~Z07) & (~X16 | X45 | B03 | ME05 | ME04)" +
            " & (~X45 | ~B03) & (~X45 | ~ME05) & (~X45 | ~ME04) & (~X17 | M642 | M651 | X46) & (~X46 | ~M651) & (~X46 | ~M642) & (~X18 | ~M651 | ~M642) " +
            " & (~X19 | ME05 | ME04 | X47) & (~X47 | ~ME05) & (~X47 | ~ME04) & (~X20 | ME05 | Y419 | X48) & (~X48 | ~ME05) & (~X48 | ~Y419)" +
            " & (~X21 | M157 | M279 | X49) & (~X49 | ~M157) & (~X49 | ~M279) & (~X22 | Y460 | Y494 | X40) & (~X23 | ~ME05 | ~ME04) & (~X24 | X50 | Y235 | Y220)" +
            " & (~X50 | ~Y235) & (~X50 | ~Y220) & (~X25 | Y223 | Y239 | Y266 | X51) & (~X51 | ~Y223) & (~X51 | ~Y239) & (~X51 | ~Y266) & (~X26 | X52 | Y238 | Y476)" +
            " & (~X52 | ~Y238) & (~X52 | ~Y476) & (~X27 | Y540 | X53 | X54) & (~X53 | Y413) & (~X53 | ~Y540) & (~X54 | ~Y540) & (~X54 | X55) & (~X55 | ~Y413 | Y540)" +
            " & (~X28 | X56 | X57) & (~X56 | X58) & (~X56 | ~Y413) & (~X56 | ~Y414) & (~X58 | Y962 | Y494 | Y460) & (~X57 | X59) & (~X57 | ~Y962) & (~X57 | ~Y494)" +
            " & (~X57 | ~Y460) & (~X59 | Y413 | Y414) & (~X29 | X60 | Y962 | Y494 | Y460 | X61 | Y413 | Y414) & (~X60 | ~Y413) & (~X60 | ~Y414) & (~X61 | ~Y962)" +
            " & (~X61 | ~Y494) & (~X61 | ~Y460) & (~X30 | Y494 | Y737L) & (~X31 | M157 | M279 | X45 | X62) & (~X62 | X63) & (~X62 | ~M157) & (~X62 | ~M279)" +
            " & (~X63 | B03 | ME05 | ME04) & (~X32 | B03 | ME05 | ME04 | M279 | M157 | X64) & (~X64 | ~B03) & (~X64 | ~ME05) & (~X64 | ~ME04) & (~X64 | ~M279) & (~X64 | ~M157)");

    Formula expectedResult = parser.parse("(L | R) & (~L | ~R) & (Y608 | Y628 | X33) & (~X33 | ~Y608) & (~X33 | ~Y628) & (Y631 | Y632 | X35) & (~X35 | ~Y631) & (~X35 | ~Y632) & (X36 | Y641 | Y642 | Y640) " +
            "& (~X36 | X38) & (~X36 | X39) & (~X39 | Y631 | Y632) & (X35 | X41) & (~X41 | X42) & (~X41 | ~Y641) & (~X41 | ~Y642) & (~X41 | ~Y640) & (X43 | FS | FX) " +
            "& (~X43 | ~FS) & (~X43 | ~FX) & (X44 | Z06 | Z07) & (~X44 | ~Z06) & (~X44 | ~Z07) & (X45 | B03 | ME05 | ME04) & (~X45 | ~B03) & (~X45 | ~ME05) & (~X45 | ~ME04) " +
            "& (M642 | M651 | X46) & (~X46 | ~M651) & (~X46 | ~M642) & (~M651 | ~M642) & (ME05 | ME04 | X47) & (~X47 | ~ME05) & (~X47 | ~ME04) & (ME05 | Y419 | X48) & (~X48 | ~ME05) " +
            "& (~X48 | ~Y419) & (M157 | M279 | X49) & (~X49 | ~M157) & (~X49 | ~M279) & (~ME05 | ~ME04) & (X50 | Y235 | Y220) & (~X50 | ~Y235) & (~X50 | ~Y220) & (Y223 | Y239 | Y266 | X51) " +
            "& (~X51 | ~Y223) & (~X51 | ~Y239) & (~X51 | ~Y266) & (X52 | Y238 | Y476) & (~X52 | ~Y238) & (~X52 | ~Y476) & (Y540 | X54) & (~X54 | ~Y540) & (~X54 | X55) & (M157 | M279 | X45 | X62)" +
            " & (~X62 | X63) & (~X62 | ~M157) & (~X62 | ~M279) & (~X63 | B03 | ME05 | ME04) & (B03 | ME05 | ME04 | M279 | M157 | X64) & (~X64 | ~B03) & (~X64 | ~ME05) & (~X64 | ~ME04)" +
            " & (~X64 | ~M279) & (~X64 | ~M157)");
    Formula result = f1.transform(new UnitPropagation());
    assertEquals(result.toString(), expectedResult, result);
  }

  @Test
  public void testSimple() throws Exception {
    UnitPropagation up = new UnitPropagation();
    Formula expectedResult = factory.verum();
    Formula result = factory.verum().transform(up);
    assertEquals(result.toString(), expectedResult, result);
    expectedResult = factory.falsum();
    result = factory.falsum().transform(up);
    assertEquals(expectedResult, result);
    expectedResult = factory.literal("x", true);
    result = expectedResult.transform(up);
    assertEquals(expectedResult, result);
    expectedResult = factory.variable("y");
    result = factory.and(factory.or(factory.variable("y"), factory.literal("x", false)), factory.variable("x")).transform(up);
    assertEquals(expectedResult, result);
  }
}