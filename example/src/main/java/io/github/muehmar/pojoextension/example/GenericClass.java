package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.util.List;

@SafeBuilder
public class GenericClass<T extends List<String> & Comparable<T>> {
  private final String prop1;
  private final T data;

  public GenericClass(String prop1, T data) {
    this.prop1 = prop1;
    this.data = data;
  }

  public String getProp1() {
    return prop1;
  }

  public T getData() {
    return data;
  }
}
