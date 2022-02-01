package io.github.muehmar.pojoextension.generator.impl;

import ch.bluecare.commons.data.PList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Function;

public class JavaModifiers {
  private final PList<JavaModifier> modifiers;

  private JavaModifiers(PList<JavaModifier> modifiers) {
    this.modifiers = modifiers;
  }

  public static JavaModifiers empty() {
    return new JavaModifiers(PList.empty());
  }

  public static JavaModifiers of(JavaModifier... modifiers) {
    return new JavaModifiers(PList.fromArray(modifiers));
  }

  public static JavaModifiers of(PList<JavaModifier> modifiers) {
    return new JavaModifiers(modifiers);
  }

  public String asString() {
    return modifiers
        .distinct(Function.identity())
        .sort(Comparator.comparingInt(JavaModifier::getOrder))
        .map(JavaModifier::asString)
        .mkString(" ");
  }

  public String asStringTrailingWhitespace() {
    final String s = asString();
    return s.isEmpty() ? s : s.concat(" ");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JavaModifiers that = (JavaModifiers) o;
    return Objects.equals(modifiers, that.modifiers);
  }

  @Override
  public int hashCode() {
    return Objects.hash(modifiers);
  }
}
