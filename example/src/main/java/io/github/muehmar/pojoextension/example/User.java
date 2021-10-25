package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Objects;
import java.util.Optional;

@PojoExtension
public class User {
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(name, user.name) && Objects.equals(age, user.age);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, age);
  }

  @Override
  public String toString() {
    return "User{" + "name='" + name + '\'' + ", age=" + age + '}';
  }
}
