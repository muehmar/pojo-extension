package io.github.muehmar.pojoextension.example.safebuilder;

import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import lombok.Value;

@Value
@SafeBuilder(packagePrivateBuilder = true)
public class PackagePrivateBuilderClass {
  String prop1;
  String prop2;
}
