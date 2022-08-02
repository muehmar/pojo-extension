package io.github.muehmar.pojoextension.generator.model;

import io.github.muehmar.pojoextension.generator.model.type.Type;
import lombok.Value;

@Value
public class BuildMethod {
  Name name;
  Type returnType;
}
