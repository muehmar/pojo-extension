package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;

public class FieldDeclarationGenerator implements Generator<PojoField, PojoSettings> {
  private final JavaModifiers modifiers;

  private FieldDeclarationGenerator(JavaModifiers modifiers) {
    this.modifiers = modifiers;
  }

  public static FieldDeclarationGenerator ofModifiers(JavaModifier... modifiers) {
    return new FieldDeclarationGenerator(JavaModifiers.of(modifiers));
  }

  @Override
  public Writer generate(PojoField field, PojoSettings settings, Writer writer) {
    return writer.println(
        "%s%s %s;",
        modifiers.asStringTrailingWhitespace(),
        field.getType().getClassName().asString(),
        field.getName().asString());
  }
}
