package io.github.muehmar.pojoextension.example.custombuildmethod;

import io.github.muehmar.pojoextension.annotations.BuildMethod;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.util.Optional;
import lombok.Value;

@Value
@SafeBuilder
public class CustomBuildMethodClass<T> {
  String prop1;
  String prop2;
  Optional<T> data;

  @BuildMethod
  static <T> String customBuildMethod(CustomBuildMethodClass<T> instance) {
    return instance.toString();
  }
}
