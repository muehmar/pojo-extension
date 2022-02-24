package io.github.muehmar.pojoextension.generator.model;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import io.github.muehmar.pojoextension.generator.model.type.Type;
import java.util.Optional;

@PojoExtension
@SuppressWarnings("java:S2160")
public class FieldBuilderMethod extends FieldBuilderMethodBase {
  private final Name fieldName;
  private final Optional<Name> innerClassName;
  private final Name methodName;
  private final Type returnType;
  private final PList<Argument> arguments;

  public FieldBuilderMethod(
      Name fieldName,
      Optional<Name> innerClassName,
      Name methodName,
      Type returnType,
      PList<Argument> arguments) {
    this.fieldName = fieldName;
    this.innerClassName = innerClassName;
    this.methodName = methodName;
    this.returnType = returnType;
    this.arguments = arguments;
  }

  public Name getFieldName() {
    return fieldName;
  }

  public Optional<Name> getInnerClassName() {
    return innerClassName;
  }

  public Name getMethodName() {
    return methodName;
  }

  public Type getReturnType() {
    return returnType;
  }

  public PList<Argument> getArguments() {
    return arguments;
  }

  public PList<Name> getArgumentNames() {
    return arguments.map(Argument::getName);
  }
}
