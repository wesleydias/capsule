/**
 * Copyright (c) Michael Steindorfer <Centrum Wiskunde & Informatica> and Contributors.
 * All rights reserved.
 *
 * This file is licensed under the BSD 2-Clause License, which accompanies this project
 * and is available under https://opensource.org/licenses/BSD-2-Clause.
 */
package io.usethesource.capsule.generators.relation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.pholser.junit.quickcheck.generator.ComponentizedGenerator;
import com.pholser.junit.quickcheck.generator.GenerationStatus;
import com.pholser.junit.quickcheck.generator.Shrink;
import com.pholser.junit.quickcheck.generator.Size;
import com.pholser.junit.quickcheck.random.SourceOfRandomness;
import io.usethesource.capsule.api.TernaryRelation;

import static com.pholser.junit.quickcheck.internal.Lists.removeFrom;
import static com.pholser.junit.quickcheck.internal.Lists.shrinksOfOneItem;
import static com.pholser.junit.quickcheck.internal.Ranges.Type.INTEGRAL;
import static com.pholser.junit.quickcheck.internal.Ranges.checkRange;
import static com.pholser.junit.quickcheck.internal.Sequences.halving;
import static java.util.stream.StreamSupport.stream;

@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class AbstractTernaryRelationGenerator<T extends TernaryRelation.Immutable>
    extends ComponentizedGenerator<T> {

  private Class<T> target;
  private Size sizeRange;

  public AbstractTernaryRelationGenerator(Class<T> target) {
    super(target);
    this.target = target;
  }

  public void configure(Size size) {
    this.sizeRange = size;
    checkRange(INTEGRAL, size.min(), size.max());
  }

  protected final int size(SourceOfRandomness random, GenerationStatus status) {
    return sizeRange != null ? random.nextInt(sizeRange.min(), sizeRange.max()) : status.size();
  }

  protected T empty() {
    try {
      final Method persistentSetOfEmpty = target.getMethod("of");
      return (T) persistentSetOfEmpty.invoke(null);
    } catch (NoSuchMethodException | SecurityException | IllegalAccessException
        | IllegalArgumentException | InvocationTargetException e) {
      throw new RuntimeException();
    }
  }

  @Override
  public int numberOfNeededComponents() {
    return 4;
  }

  @Override
  public T generate(SourceOfRandomness random, GenerationStatus status) {
    int size = size(random, status);

    T items = empty();
    for (int i = 0; i < size; ++i) {
      // Object item0 = componentGenerators().get(0).generate(random, status);
      // Object item1 = componentGenerators().get(1).generate(random, status);
      // Object item2 = componentGenerators().get(2).generate(random, status);

      Object triple = componentGenerators().get(3).generate(random, status);

      items = (T) items.__insert(triple);
    }

    return items;
  }

  @Override
  public List<T> doShrink(SourceOfRandomness random, T larger) {
    @SuppressWarnings("unchecked")
    List<Object> asList = new ArrayList<>(larger);

    List<T> shrinks = new ArrayList<>();
    shrinks.addAll(removals(asList));

    @SuppressWarnings("unchecked")
    Shrink<Object> generator = (Shrink<Object>) componentGenerators().get(0);

    List<List<Object>> oneItemShrinks = shrinksOfOneItem(random, asList, generator);
    shrinks.addAll(oneItemShrinks.stream().map(this::convert).filter(this::inSizeRange)
        .collect(Collectors.toList()));

    return shrinks;
  }

  private boolean inSizeRange(T items) {
    return sizeRange == null
        || (items.size() >= sizeRange.min() && items.size() <= sizeRange.max());
  }

  private List<T> removals(List<?> items) {
    return stream(halving(items.size()).spliterator(), false).map(i -> removeFrom(items, i))
        .flatMap(Collection::stream).map(this::convert).filter(this::inSizeRange)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  private T convert(List<?> items) {
    T converted = empty();
    for (Object item : items) {
      converted = (T) converted.__insert(item);
    }
    return converted;
  }

}
