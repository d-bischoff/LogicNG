package org.logicng.solvers.datastructures;

import org.logicng.collections.LNGIntVector;
import org.logicng.collections.LNGVector;

import java.util.Map;

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

  private Map<Integer, String> idx2name;

  /**
   * Constructs a new list for a given solver. The order of the list is fixed.
   * The initial freeVars of the heap is 1000 elements.
   *
   * @param idx2name
   */
  public FixedList(Map<Integer, String> idx2name) {
    this.idx2name = idx2name;
  }

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
    boolean ret = entryToIndex.get(n) >= headIndex;
    System.out.println(idx2name.get(n)+(ret ? " is maybe free": " is not free"));
    return ret;
  }

  @Override
  public void accelerate(int n) {
    //throw new UnsupportedOperationException("acceleration is not supported for fixed lists.");
  }

  @Override
  public void setFree(int n) {
    System.out.println(idx2name.get(entryToIndex.get(n))+" was set free");
    headIndex = Math.min(headIndex,entryToIndex.get(n));
  }

  @Override
  public int getNextFreeVariableMarkAssigned() {
    int tobeReturned = indexToEntry.get(headIndex);
    System.out.println("returned "+idx2name.get(tobeReturned)+" to the solver as next free var (headindex was "+headIndex+")");
    headIndex++;
    return tobeReturned;
  }

  @Override
  public void remove(int n) {
    throw new UnsupportedOperationException("removing is not supported for fixed lists.");
  }

  @Override
  public void initialize(LNGIntVector ns) {
    System.out.print("initialize called with ");
    for(int i = 0; i<ns.size();i++){
      System.out.print(idx2name.get(ns.get(i))+",");
    }
    System.out.print("\n");
    indexToEntry = new LNGIntVector(ns);
    entryToIndex = new LNGIntVector(ns.size(),-1);
    for(int i = 0; i < ns.size(); i++){
      entryToIndex.set(ns.get(i),i);
    }
    System.out.println("fixed list ordering initialized as: ");
    for(int i = 0; i<indexToEntry.size();i++){
      System.out.print(idx2name.get(indexToEntry.get(i))+",");
    }
    System.out.print("\n");
  }

  @Override
  public void clear() {
    if(entryToIndex != null) entryToIndex.clear();
    if(indexToEntry != null) indexToEntry.clear();
    headIndex = 0;
    System.out.println("fixed list cleared");
  }

}
