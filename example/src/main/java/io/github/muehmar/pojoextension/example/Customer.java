package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.example.CustomerExtension.Builder0;
import java.util.Objects;
import java.util.Optional;

@PojoExtension
public class Customer {
  private final String id;
  private final String name;
  private final Optional<String> nickname;
  @Nullable private final Integer age;

  // This constructor is used to allow instance creation for the safe builder
  Customer(String id, String name, String nickname, Integer age) {
    this.id = id;
    this.name = name;
    this.nickname = Optional.ofNullable(nickname);
    this.age = age;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Optional<String> getNickname() {
    return nickname;
  }

  public Optional<Integer> getAge() {
    return Optional.ofNullable(age);
  }

  public static Builder0 newBuilder() {
    return io.github.muehmar.pojoextension.example.CustomerExtension.newBuilder();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Customer customer = (Customer) o;
    return Objects.equals(id, customer.id)
        && Objects.equals(name, customer.name)
        && Objects.equals(nickname, customer.nickname)
        && Objects.equals(age, customer.age);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, name, nickname, age);
  }
}
