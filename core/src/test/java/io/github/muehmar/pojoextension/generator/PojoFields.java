package io.github.muehmar.pojoextension.generator;

import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.Type;

public class PojoFields {
  private PojoFields() {}

  public static PojoField requiredId() {
    return new PojoField(Type.integer(), Name.fromString("id"), true);
  }
}
