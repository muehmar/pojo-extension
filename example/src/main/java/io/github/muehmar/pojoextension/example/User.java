package io.github.muehmar.pojoextension.example;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;
import lombok.Value;

@Value
@PojoExtension
public class User implements UserExtension {
  public static final String USER_LABEL = "label";
  String name;
  Optional<Integer> age;
}
