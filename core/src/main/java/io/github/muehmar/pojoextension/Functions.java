package io.github.muehmar.pojoextension;

import ch.bluecare.commons.data.Pair;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class Functions {
  private Functions() {}

  public static <T> Function<Pair<T, Integer>, T> mapFirst(UnaryOperator<T> mapFirst) {
    return pair -> {
      if (pair.second() == 0) {
        return mapFirst.apply(pair.first());
      } else {
        return pair.first();
      }
    };
  }
}
