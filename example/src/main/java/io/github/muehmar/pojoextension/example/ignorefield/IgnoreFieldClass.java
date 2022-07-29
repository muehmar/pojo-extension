package io.github.muehmar.pojoextension.example.ignorefield;

import io.github.muehmar.pojoextension.annotations.Ignore;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@SafeBuilder
@EqualsAndHashCode
@Getter
public class IgnoreFieldClass {
  private final String id;
  private final String name;
  @Ignore private final String deviated;

  public IgnoreFieldClass(String id, String name) {
    this.id = id;
    this.name = name;
    this.deviated = String.format("%s-%s", id, name);
  }
}
