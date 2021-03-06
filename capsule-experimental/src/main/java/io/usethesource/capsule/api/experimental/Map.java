/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.api.experimental;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

import io.usethesource.capsule.util.iterator.SupplierIterator;

public interface Map<K, V> extends Iterable<K>, Function<K, Optional<V>> {

  long size();

  boolean isEmpty();

  boolean contains(final Object o);

  boolean containsValue(final Object o);

  // default boolean containsAll(final Set<K> set) {
  // for (K item : set) {
  // if (!contains(item)) {
  // return false;
  // }
  // }
  // return true;
  //
  // }

  // K get(final Object o);

  @Override
  SupplierIterator<K, V> iterator();

  // Iterator<V> valueIterator();

  // @Deprecated // TODO: replace with SupplierIterator interface
  Iterator<java.util.Map.Entry<K, V>> entryIterator();

  // @Deprecated // TODO: replace with SupplierIterator interface
  // Set<java.util.Map.Entry<K, V>> entrySet();

  /**
   * The hash code of a map is order independent by combining the hashes of the elements (both keys
   * and values) via a bitwise XOR operation.
   *
   * @return XOR reduction of all hashes of elements
   */
  @Override
  int hashCode();

  @Override
  boolean equals(Object other);

  Map.Immutable<K, V> asImmutable();

  interface Immutable<K, V> extends Map<K, V> {

    Map.Immutable<K, V> insert(final K key, final V val);

    Map.Immutable<K, V> remove(final K key);

    Map.Immutable<K, V> insertAll(final Map<? extends K, ? extends V> map);

    boolean isTransientSupported();

    Map.Transient<K, V> asTransient();

    java.util.Map<K, V> asJdkCollection();
  }

  interface Transient<K, V> extends Map<K, V> {

    V insert(final K key, final V val);

    V remove(final K key);

    boolean insertAll(final Map<? extends K, ? extends V> map);

  }

}
