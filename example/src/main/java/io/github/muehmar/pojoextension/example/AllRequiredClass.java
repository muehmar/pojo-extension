package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.Nullable;
import java.util.Optional;

@AllRequiredExtension
public class AllRequiredClass extends AllRequiredClassExtension {
  private final String id;
  private final Optional<Boolean> flag;
  @Nullable private final Integer age;

  public AllRequiredClass(String id, Optional<Boolean> flag, Integer age) {
    this.id = id;
    this.flag = flag;
    this.age = age;
  }

  public String getId() {
    return id;
  }

  public Optional<Boolean> getFlag() {
    return flag;
  }

  public Integer getAge() {
    return age;
  }
}