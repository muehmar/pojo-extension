package io.github.muehmar.pojoextension.generator;

import io.github.muehmar.pojoextension.generator.data.Argument;
import io.github.muehmar.pojoextension.generator.data.Getter;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;

public class PojoFields {
  private PojoFields() {}

  public static PojoField requiredId() {
    return new PojoField(Name.fromString("id"), Type.integer(), true);
  }

  public static Argument toArgument(PojoField f) {
    return new Argument(f.getName(), f.getType());
  }

  public static Getter toGetter(PojoField f) {
    return new Getter(Getter.getterName(f), f.getType());
  }
}
