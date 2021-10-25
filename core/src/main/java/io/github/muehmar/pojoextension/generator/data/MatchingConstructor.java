package io.github.muehmar.pojoextension.generator.data;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.data.MatchingConstructorExtension.Builder0;
import java.util.Objects;

@PojoExtension
public class MatchingConstructor {
  private final Constructor constructor;
  private final PList<PojoField> fields;

  public MatchingConstructor(Constructor constructor, PList<PojoField> fields) {
    this.constructor = constructor;
    this.fields = fields;
  }

  public Constructor getConstructor() {
    return constructor;
  }

  public PList<PojoField> getFields() {
    return fields;
  }

  public static Builder0 newBuilder() {
    return io.github.muehmar.pojoextension.generator.data.MatchingConstructorExtension.newBuilder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MatchingConstructor that = (MatchingConstructor) o;
    return Objects.equals(constructor, that.constructor) && Objects.equals(fields, that.fields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(constructor, fields);
  }

  @Override
  public String toString() {
    return "MatchingConstructor{" + "constructor=" + constructor + ", fields=" + fields + '}';
  }
}
