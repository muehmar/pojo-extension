package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.List;
import java.util.Optional;

@PojoExtension
public class GenericClass<T extends List<String> & Comparable<T>, S>
    implements GenericClassExtension<T, S> {
  private final String prop1;
  private final T data;
  private final Optional<S> additionalData;

  public GenericClass(String prop1, T data, Optional<S> additionalData) {
    this.prop1 = prop1;
    this.data = data;
    this.additionalData = additionalData;
  }

  public String getProp1() {
    return prop1;
  }

  public T getData() {
    return data;
  }

  public Optional<S> getAdditionalData() {
    return additionalData;
  }

  @Override
  public boolean equals(Object o) {
    return genEquals(o);
  }

  @Override
  public int hashCode() {
    return genHashCode();
  }
}
