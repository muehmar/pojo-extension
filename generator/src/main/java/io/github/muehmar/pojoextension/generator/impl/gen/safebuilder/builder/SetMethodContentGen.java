package io.github.muehmar.pojoextension.generator.impl.gen.safebuilder.builder;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;

public class SetMethodContentGen implements Generator<PojoField, PojoSettings> {
  @Override
  public Writer generate(PojoField field, PojoSettings settings, Writer writer) {
    return writer
        .println("this.%s = %s;", field.getName().asString(), field.getName().asString())
        .println("return this;");
  }
}
