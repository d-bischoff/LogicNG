package org.logicng.solvers.datastructures;

import org.logicng.collections.LNGIntVector;

/**
 * @author Daniel Bischoff
 *         on 31.08.2016.
 *         <p>
 *         Copyright by Daniel Bischoff
 */
public interface VariableOrdering {

  boolean noFreeVars();

  boolean isMaybeFree(int n);

  void accelerate(int n);

  void setFree(int n);

  int getNextFreeVariableMarkAssigned();

  void remove(int n);

  void initialize(LNGIntVector ns);

  void clear();

  @Override
  String toString();
}
