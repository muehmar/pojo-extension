package io.github.muehmar.pojoextension.generator;

import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.Type.string;

import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;

public class PojoFields {
  private PojoFields() {}

  public static PojoField requiredId() {
    return new PojoField(Name.fromString("id"), Type.integer(), REQUIRED);
  }

  public static PojoField requiredMap() {
    return new PojoField(
        Name.fromString("someMap"), Type.map(string(), Type.list(string())), REQUIRED);
  }

  public static Argument toArgument(PojoField f) {
    return new Argument(f.getName(), f.getType());
  }

  public static Getter toGetter(PojoField f) {
    final Type returnType =
        f.isOptional() && !f.getType().isOptional() ? Type.optional(f.getType()) : f.getType();
    return Getter.newBuilder().setName(Getter.getterName(f)).setReturnType(returnType).build();
  }
}
