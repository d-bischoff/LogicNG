package org.logicng.solvers.datastructures;

import org.logicng.collections.LNGIntVector;

/**
 * @author Daniel Bischoff
 *         on 31.08.2016.
 *         <p>
 *         Copyright by Daniel Bischoff
 */
public class FixedList implements VariableOrdering {

  private LNGIntVector entryToIndex;
  private LNGIntVector indexToEntry;
  private int headIndex;

  /**
   * Constructs a new list for a given solver. The order of the list is fixed.
   * The initial freeVars of the heap is 1000 elements.
   *
   */
  public FixedList() { }

  @Override
  public boolean noFreeVars() {
    return headIndex == entryToIndex.size();
  }

  @Override
  public boolean isMaybeFree(int n) {
    if(entryToIndex==null){
      assert indexToEntry == null;
      return true;
    }
    return entryToIndex.get(n) >= headIndex;
  }

  @Override
  public void accelerate(int n) {
    return;
    //throw new UnsupportedOperationException("acceleration is not supported for fixed lists.");
  }

  @Override
  public void setFree(int n) {
    headIndex = Math.min(headIndex,entryToIndex.get(n));
  }

  @Override
  public int getNextFreeVariableMarkAssigned() {
    return indexToEntry.get(headIndex++);
  }

  @Override
  public void remove(int n) {
    throw new UnsupportedOperationException("removing is not supported for fixed lists.");
  }

  @Override
  public void initialize(LNGIntVector ns) {
    entryToIndex = new LNGIntVector(ns);
    indexToEntry = new LNGIntVector(ns.size(),-1);
    for(int i = 0; i < ns.size(); i++){
      indexToEntry.set(ns.get(i),i);
    }
  }

  @Override
  public void clear() {

  }

}
