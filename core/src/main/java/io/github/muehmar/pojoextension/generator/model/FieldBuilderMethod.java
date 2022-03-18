package io.github.muehmar.pojoextension.generator.model;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import java.util.Optional;
import lombok.Value;

@Value
@PojoExtension
public class FieldBuilderMethod implements FieldBuilderMethodExtension {
  Name fieldName;
  Optional<Name> innerClassName;
  Name methodName;
  Type returnType;
  PList<Argument> arguments;

  public PList<Name> getArgumentNames() {
    return arguments.map(Argument::getName);
  }
}
