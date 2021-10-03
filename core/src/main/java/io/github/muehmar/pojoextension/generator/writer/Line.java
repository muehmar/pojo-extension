package io.github.muehmar.pojoextension.generator.writer;

import ch.bluecare.commons.data.PList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

public class Line {
  private final PList<String> fragments;

  private Line(PList<String> fragments) {
    this.fragments = fragments;
  }

  public static Line ofString(String fragment) {
    return new Line(PList.single(fragment));
  }

  public static Line empty() {
    return new Line(PList.empty());
  }

  public Line append(String fragment) {
    return new Line(fragments.cons(fragment));
  }

  public Line prepend(String fragment) {
    return new Line(fragments.add(fragment));
  }

  public Line prepend(PList<String> fragments) {
    return new Line(this.fragments.concat(fragments.reverse()));
  }

  public Line removeTrailingBlankFragments() {
    return new Line(fragments.filter(dropWhileBlank()));
  }

  private static Predicate<String> dropWhileBlank() {
    final AtomicBoolean nonBlankSeen = new AtomicBoolean(false);
    return in -> {
      if (nonBlankSeen.get() || in.trim().length() > 0) {
        nonBlankSeen.set(true);
        return true;
      } else {
        return false;
      }
    };
  }

  public StringBuilder asStringBuilder() {
    final StringBuilder sb = new StringBuilder();
    fragments.reverse().forEach(sb::append);
    return sb;
  }

  public String asString() {
    return asStringBuilder().toString();
  }
}
