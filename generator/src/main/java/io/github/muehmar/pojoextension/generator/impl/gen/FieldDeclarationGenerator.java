package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;

public class FieldDeclarationGenerator implements Generator<PojoField, PojoSettings> {
  @Override
  public Writer generate(PojoField field, PojoSettings settings, Writer writer) {
    return writer.println(
        "private final %s %s;",
        field.getType().getClassName().asString(), field.getName().asString());
  }
}
