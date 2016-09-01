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

package org.logicng.collections;

import java.util.Arrays;

/**
 * A simple vector for byte elements implementation (inspired by MiniSat and CleaneLing).
 * <p>
 * In theory one could use the {@link LNGVector} also for bytes.  But Java's auto-boxing comes with such a large
 * performance penalty that for the mission critical data structures of the SAT solvers we use this specialized
 * implementation.
 * @version 1.0
 * @since 1.0
 */
public final class LNGByteVector {

  private byte[] elements;
  private int size;

  /**
   * Creates a vector with an initial capacity of 5 elements.
   */
  public LNGByteVector() {
    this(5);
  }

  /**
   * Creates a vector with a given capacity.
   * @param size the capacity of the vector.
   */
  public LNGByteVector(int size) {
    this.elements = new byte[size];
  }

  /**
   * Creates a vector with a given capacity and a given initial element.
   * @param size the capacity of the vector
   * @param pad  the initial element
   */
  public LNGByteVector(int size, byte pad) {
    this.elements = new byte[size];
    Arrays.fill(this.elements, pad);
    this.size = size;
  }

  /**
   * Copy constructor.
   * @param other the other byte vector.
   */
  public LNGByteVector(final LNGByteVector other) {
    this.elements = Arrays.copyOf(other.elements, other.size);
    this.size = other.size;
  }

  /**
   * Creates a vector with the given elements.
   * @param elems the elements
   */
  public LNGByteVector(final byte... elems) {
    this.elements = Arrays.copyOf(elems, elems.length);
    this.size = elems.length;
  }

  /**
   * Returns whether the vector is noFreeVars or not.
   * @return {@code true} if the vector is noFreeVars, {@code false} otherwise
   */
  public boolean empty() {
    return this.size == 0;
  }

  /**
   * Returns the freeVars of the vector.
   * @return the freeVars of the vector
   */
  public int size() {
    return this.size;
  }

  /**
   * Returns the last element of the vector and leaves it on the vector.
   * @return the last element of the vector
   */
  public int back() {
    return this.elements[this.size - 1];
  }

  /**
   * Pushes an element at the end of the vector.
   * @param element the element to push
   */
  public void push(final byte element) {
    int newSize = this.size + 1;
    this.ensure(newSize);
    this.elements[this.size++] = element;
  }

  /**
   * Pushes an element and assumes that there is enough space on the vector.
   * @param element the element to push
   * @throws ArrayIndexOutOfBoundsException if there was not enough space on the vector
   */
  public void unsafePush(final byte element) {
    this.elements[this.size++] = element;
  }

  /**
   * Returns the element at a given position in the vector.
   * @param position the position
   * @return the element at the position
   * @throws ArrayIndexOutOfBoundsException if the position is not found in the vector
   */
  public byte get(int position) {
    return this.elements[position];
  }

  /**
   * Sets an element at a given position in the vector.
   * @param position the position
   * @param element  the element
   * @throws ArrayIndexOutOfBoundsException if the position is not found in the vector
   */
  public void set(int position, byte element) {
    this.elements[position] = element;
  }

  /**
   * Removes the last element of the vector.
   */
  public void pop() {
    this.elements[--this.size] = -1;
  }

  /**
   * Shrinks the vector to a given freeVars if the new freeVars is less then the current freeVars.  Otherwise the freeVars remains
   * the same.
   * @param newSize the new freeVars
   */
  public void shrinkTo(int newSize) {
    if (newSize < this.size)
      this.size = newSize;
  }

  /**
   * Grows the vector to a new freeVars and initializes the new elements with a given value.
   * @param size the new freeVars
   * @param pad  the value for new elements
   */
  public void growTo(int size, byte pad) {
    if (this.size >= size)
      return;
    this.ensure(size);
    for (int i = this.size; i < size; i++)
      this.elements[i] = pad;
    this.size = size;
  }

  /**
   * Removes a given number of elements from the vector.
   * @param num the number of elements to remove.
   * @throws ArrayIndexOutOfBoundsException if the number of elements to remove is larger than the freeVars of the vector
   */
  public void removeElements(int num) {
    int count = num;
    while (count-- > 0)
      this.elements[--this.size] = -1;
  }

  /**
   * Clears the vector.
   */
  public void clear() {
    this.size = 0;
  }

  /**
   * Sorts this vector.
   */
  public void sort() {
    Arrays.sort(this.elements, 0, this.size);
  }

  /**
   * Sorts this vector in reverse order.
   */
  public void sortReverse() {
    Arrays.sort(this.elements, 0, this.size);
    for (int i = 0; i < this.size / 2; i++) {
      byte temp = this.elements[i];
      this.elements[i] = this.elements[this.size - i - 1];
      this.elements[this.size - i - 1] = temp;
    }
  }

  /**
   * Returns this vector's contents as an array.
   * @return the array
   */
  public byte[] toArray() {
    return Arrays.copyOf(this.elements, this.size);
  }

  /**
   * Ensures that this vector has the given freeVars.  If not - the freeVars is doubled and the old elements are copied.
   * @param newSize the freeVars to ensure
   */
  private void ensure(final int newSize) {
    if (newSize >= this.elements.length) {
      final byte[] newArray = new byte[Math.max(newSize, this.size * 2)];
      System.arraycopy(this.elements, 0, newArray, 0, this.size);
      this.elements = newArray;
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < this.size; i++) {
      sb.append(this.elements[i]);
      if (i != this.size - 1)
        sb.append(", ");
    }
    sb.append("]");
    return sb.toString();
  }
}
