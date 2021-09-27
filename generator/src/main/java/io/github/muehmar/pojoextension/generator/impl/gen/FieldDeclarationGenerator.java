package io.github.muehmar.pojoextension.generator.impl.gen;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.Writer;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import java.util.Comparator;

public class FieldDeclarationGenerator implements Generator<PojoField, PojoSettings> {
  private final PList<JavaModifier> modifiers;

  private FieldDeclarationGenerator(PList<JavaModifier> modifiers) {
    this.modifiers = modifiers;
  }

  public static FieldDeclarationGenerator ofModifiers(JavaModifier... modifiers) {
    return new FieldDeclarationGenerator(PList.fromArray(modifiers));
  }

  @Override
  public Writer generate(PojoField field, PojoSettings settings, Writer writer) {
    return writer.println(
        "%s %s %s;",
        modifiers
            .sort(Comparator.comparingInt(JavaModifier::getOrder))
            .map(JavaModifier::asString)
            .mkString(" "),
        field.getType().getClassName().asString(),
        field.getName().asString());
  }
}
