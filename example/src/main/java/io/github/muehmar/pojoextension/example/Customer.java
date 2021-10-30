package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@PojoExtension
public class Customer extends CustomerExtension {
  private final String id;
  private final String name;
  private final Optional<String> nickname;
  @Nullable private final Integer age;
  private final double random;
  private final byte[] key;

  // This constructor is used to allow instance creation for the safe builder
  Customer(String id, String name, String nickname, Integer age, double random, byte[] key) {
    this.id = id;
    this.name = name;
    this.nickname = Optional.ofNullable(nickname);
    this.age = age;
    this.random = random;
    this.key = key;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public double getRandom() {
    return random;
  }

  public Optional<String> getNickname() {
    return nickname;
  }

  public Optional<Integer> getAge() {
    return Optional.ofNullable(age);
  }

  public byte[] getKey() {
    return key;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Customer customer = (Customer) o;
    return Double.compare(customer.random, random) == 0
        && Objects.equals(id, customer.id)
        && Objects.equals(name, customer.name)
        && Objects.equals(nickname, customer.nickname)
        && Objects.equals(age, customer.age)
        && Arrays.equals(key, customer.key);
  }

  @Override
  public int hashCode() {
    int result = Objects.hash(id, name, nickname, age, random);
    result = 31 * result + Arrays.hashCode(key);
    return result;
  }
}
