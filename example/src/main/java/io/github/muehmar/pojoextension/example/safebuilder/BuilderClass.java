package io.github.muehmar.pojoextension.example.safebuilder;

import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import lombok.Value;

@Value
@SafeBuilder(builderName = "SafeBuilder")
public class BuilderClass {
  String prop1;
  String prop2;
}
