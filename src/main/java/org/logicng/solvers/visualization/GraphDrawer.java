package org.logicng.solvers.visualization;

import org.logicng.collections.LNGIntVector;

/**
 * @author Daniel Bischoff
 *         on 01.09.2016.
 *         <p>
 *         Copyright by Daniel Bischoff
 */
public interface GraphDrawer {
  int getCurrentLevel();

  int sendSolution(LNGIntVector solutionForDrawer, int newLevel, LNGIntVector fulltrail, LNGIntVector traillim);

  void solverBacktrackedToLevel(int level);
}
