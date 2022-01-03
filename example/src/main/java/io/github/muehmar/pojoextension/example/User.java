package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160") // Not overriding equals and hashCode is fine
public class User extends UserBase {
  public static final String USER_LABEL = "label";
  private final String name;
  private final Optional<Integer> age;

  // Constructor with optional argument wrapped in Optional
  public User(String name, Optional<Integer> age) {
    this.name = name;
    this.age = age;
  }

  public String getName() {
    return name;
  }

  public Optional<Integer> getAge() {
    return age;
  }
}
