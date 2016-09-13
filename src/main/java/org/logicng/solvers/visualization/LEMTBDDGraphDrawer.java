package org.logicng.solvers.visualization;

import org.logicng.collections.LNGIntVector;
import org.logicng.collections.LNGVector;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Literal;
import org.logicng.formulas.Variable;
import org.logicng.solvers.datastructures.MSVariable;
import org.symcom.bischoff.zslemtbdd.ZSLEMTBDDManager;
import org.symcom.bischoff.zslemtbdd.ZSLEMTBDDNode;

import java.util.Map;

/**
 * @author Daniel Bischoff
 *         on 01.09.2016.
 *         <p>
 *         Copyright by Daniel Bischoff
 */
public class LEMTBDDGraphDrawer implements GraphDrawer {

  private enum ChildType {LOWCHILD, HIGHCHILD}

  private int currentLevel;
  private ZSLEMTBDDNode pointer;
  private ZSLEMTBDDNode root;
  private FormulaFactory fac;
  private ZSLEMTBDDManager mgr;
  private Map<Integer, String> idx2name;
  private ChildType nextChild = null; // ChildType.LOWCHILD;
  private LNGVector<MSVariable> vars;

  public LEMTBDDGraphDrawer(Map<Integer, String> idx2name, LNGVector<MSVariable> vars, FormulaFactory fac) {
    this.fac = fac;
    mgr = new ZSLEMTBDDManager(fac);
    this.idx2name = idx2name;
    this.vars = vars;
  }

  @Override
  public ZSLEMTBDDNode getResult() {
    //mgr.removeDuplicatesInTree(root);
    return root;
  }

  @Override
  public int getCurrentLevel() {
    return currentLevel;
  }


  @Override
  public int sendSolution(LNGIntVector solutionForDrawer, int newLevel, LNGIntVector fulltrail, LNGIntVector traillim) {
    assert currentLevel < traillim.size();
    for (int i = 0; i < solutionForDrawer.size(); i++) {
      int varAsLitInt = solutionForDrawer.get(i);
      int varAsVarInt = varAsLitInt >> 1;
      MSVariable varAsMSVar = vars.get(varAsVarInt);
      boolean decision = varAsMSVar.reason() == null;
      assert i != 0 || decision;
      boolean phase = (varAsLitInt & 1) != 1;
      Variable varAsFormula = fac.variable(idx2name.get(varAsVarInt));
      Literal varAsLiteral = fac.literal(idx2name.get(varAsVarInt), phase);
      int debugShouldNeverBe2 = 0;
      assert varAsFormula != null;

      if (decision) {
        if (pointer == null) {
          assert root == null;
          assert i == 0;
          assert currentLevel == 0;
          root = new ZSLEMTBDDNode(mgr, varAsFormula);
          pointer = root;
        } else {
          if (!pointer.getVar().equals(varAsFormula)) {
            assert pointer != null;
            assert root != null;
            assert nextChild !=null;
            ZSLEMTBDDNode next = new ZSLEMTBDDNode(mgr, varAsFormula);
            if (nextChild.equals(ChildType.HIGHCHILD)) {
              pointer.setHighChild(next);
            } else
              pointer.setLowChild(next);
            pointer = next;
          } else {
            debugShouldNeverBe2++;
            assert debugShouldNeverBe2 <= 1;
            System.out.println("what happened?");
          }
        }
        nextChild = phase ? ChildType.HIGHCHILD : ChildType.LOWCHILD;
      } else {
        if (pointer == null) {
          assert root == null;
          assert i == 0;
          root = new ZSLEMTBDDNode(mgr, null, true);
        }
        if (nextChild.equals(ChildType.HIGHCHILD)) {
          pointer.addToHighLabel(varAsLiteral);
        } else {
          pointer.addToLowLabel(varAsLiteral);
        }
      }
    }
    if (nextChild.equals(ChildType.HIGHCHILD))
      pointer.setHighChild(mgr.posTerminal);
    else
      pointer.setLowChild(mgr.posTerminal);

    currentLevel = newLevel;
    System.out.println("currentlevel was set to "+currentLevel);
    backtrackUntilNodeHasOnly1Child();
    System.out.println("backtracked to level "+currentLevel);
    //while (newLevel >= 0 && (fulltrail.get(traillim.get(newLevel)) & 1) == 0)
    //  newLevel--;
    //currentLevel = newLevel;
    if(currentLevel==0)
      assert pointer.equals(root);
    return currentLevel;
  }

  private void backtrackUntilNodeHasOnly1Child() {
    while (true) {
      if (pointer.isTerminal() || (pointer.hasHighChild() && pointer.hasLowChild())) {
        int parentsize = pointer.getParents().size();
        if (parentsize == 0) {
          assert currentLevel==0;
          currentLevel = -1;
          return;
        }
        assert parentsize == 1;
        pointer = pointer.getParents().iterator().next();
        currentLevel--;
      } else {
        return;
      }
    }
  }

  @Override
  public void solverBacktrackedToLevel(int level) {
    System.out.println("solver backtracked to level " + level + " currentlevel was " + currentLevel);


    //assert (pointer==null || (pointer.hasLowChild() ^ pointer.hasHighChild()));
    for (int i = currentLevel; i > level; i--) {
      if (pointer.hasLowChild())
        pointer.setHighChild(mgr.negTerminal);
      else if (pointer.hasHighChild())
        pointer.setLowChild(mgr.negTerminal);
      assert pointer.getParents().size() == 1;
      pointer = pointer.getParents().iterator().next();
    }

    currentLevel = Math.min(currentLevel, level);
  }

}
