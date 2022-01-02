package io.github.muehmar.pojoextension.example.customannotations;

import io.github.muehmar.pojoextension.annotations.Nullable;
import java.util.Optional;

@AllRequiredExtension(extensionName = "CustomExtension")
public class AllRequiredClass implements CustomExtension {
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

  @Override
  public boolean equals(Object o) {
    return genEquals(o);
  }

  @Override
  public int hashCode() {
    return genHashCode();
  }

  @Override
  public String toString() {
    return genToString();
  }
}
