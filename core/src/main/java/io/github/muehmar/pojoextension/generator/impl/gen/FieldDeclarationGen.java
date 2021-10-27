package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.writer.Writer;

public class FieldDeclarationGen implements Generator<PojoField, PojoSettings> {
  private final JavaModifiers modifiers;

  private FieldDeclarationGen(JavaModifiers modifiers) {
    this.modifiers = modifiers;
  }

  public static FieldDeclarationGen ofModifiers(JavaModifier... modifiers) {
    return new FieldDeclarationGen(JavaModifiers.of(modifiers));
  }

  @Override
  public Writer generate(PojoField field, PojoSettings settings, Writer writer) {
    return writer
        .println(
            "%s%s %s;",
            modifiers.asStringTrailingWhitespace(), field.getType().getClassName(), field.getName())
        .ref(field.getType().getQualifiedName().asString());
  }
}
