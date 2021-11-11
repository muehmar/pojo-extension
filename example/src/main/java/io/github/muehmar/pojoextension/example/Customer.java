package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.Getter;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160") // Not overriding equals and hashCode is fine
public class Customer extends CustomerExtension {
  private final String id;
  private final String name;
  private final Optional<String> nickname;
  @Nullable private final Integer age;
  private final double random;
  private final byte[] key;
  private final boolean flag;

  // This constructor is used to allow instance creation for the safe builder
  Customer(
      String id,
      String name,
      String nickname,
      Integer age,
      double random,
      byte[] key,
      boolean flag) {
    this.id = id;
    this.name = name;
    this.nickname = Optional.ofNullable(nickname);
    this.age = age;
    this.random = random;
    this.key = key;
    this.flag = flag;
  }

  @Getter("id")
  public String getIdentification() {
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

  public boolean isFlag() {
    return flag;
  }
}
