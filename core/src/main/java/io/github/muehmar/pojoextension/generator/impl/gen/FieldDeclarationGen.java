package io.github.muehmar.pojoextension.generator.impl.gen;

import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;

public class FieldDeclarationGen implements Generator<PojoField, PojoSettings> {
  private final JavaModifiers modifiers;

  private FieldDeclarationGen(JavaModifiers modifiers) {
    this.modifiers = modifiers;
  }

  public static Generator<PojoField, PojoSettings> ofModifiers(JavaModifier... modifiers) {
    return new FieldDeclarationGen(JavaModifiers.of(modifiers)).append(RefsGen.fieldRefs());
  }

  @Override
  public Writer generate(PojoField field, PojoSettings settings, Writer writer) {
    return writer.println(
        "%s%s %s;",
        modifiers.asStringTrailingWhitespace(),
        field.getType().getTypeDeclaration(),
        field.getName());
  }
}
