package org.logicng.solvers.visualization;

import org.logicng.collections.LNGIntVector;

/**
 * @author Daniel Bischoff
 *         on 01.09.2016.
 *         <p>
 *         Copyright by Daniel Bischoff
 */
public class DebugGraphDrawer implements GraphDrawer {

  private int currentLevel;

  @Override
  public int getCurrentLevel() {
    return this.currentLevel;
  }

  @Override
  public void sendSolution(LNGIntVector solutionForDrawer, int newLevel, LNGIntVector fulltrail, LNGIntVector traillim) {
    StringBuilder sb = new StringBuilder();
    sb.append("Solution recieved for level ").append(currentLevel).append(": ");
    visualizeTrail(sb,solutionForDrawer,traillim,fulltrail.size()-solutionForDrawer.size());
    sb.append("\n");
    sb.append("full trail: ");
    visualizeTrail(sb,fulltrail,traillim, 0);
    sb.append("\n");
    sb.append("traillim: ").append(traillim).append("\n");
    currentLevel = newLevel;
    sb.append("new level :").append(currentLevel).append("\n");
    System.out.println(sb.toString());
  }

  private void visualizeTrail(StringBuilder sb, LNGIntVector vec, LNGIntVector realPos, int offset) {
    sb.append("[");
    for (int i = 0; i < vec.size(); i++) {
      if(contains(realPos,i+offset)){
        //sb.append("_");
        sb.append(vec.get(i));
        //sb.append("_");
      }else
      {
        sb.append("(").append(vec.get(i)).append(")");
      }
      if (i == vec.size()-1) {
        sb.append("]");
      } else sb.append(",");
    }
  }

  private boolean contains(LNGIntVector vector, int elem) {
    for(int i = 0; i<vector.size();i++)
      if(vector.get(i)==elem)
        return true;
    return false;
  }

  @Override
  public void solverBacktrackedToLevel(int level) {
    System.out.println("Solver backtracked to level: " + level+"\n");
    currentLevel = Math.min(currentLevel, level);
  }
}
