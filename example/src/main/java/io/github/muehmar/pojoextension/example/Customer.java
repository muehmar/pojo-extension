package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.Getter;
import io.github.muehmar.pojoextension.annotations.Nullable;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;
import lombok.Value;

@PojoExtension(builderSetMethodPrefix = "set")
@Value
public class Customer implements CustomerExtension {
  String id;
  String name;
  Optional<String> nickname;
  @Nullable Integer age;
  double random;
  byte[] key;
  boolean flag;

  @Getter("id")
  public String getIdentification() {
    return id;
  }

  @Getter("nickname")
  public Optional<String> getNick() {
    return nickname;
  }

  public Optional<Integer> getAge() {
    return Optional.ofNullable(age);
  }

  @Value
  @PojoExtension
  public static class Address implements CustomerAddressExtension {
    String street;
    String city;
  }
}
