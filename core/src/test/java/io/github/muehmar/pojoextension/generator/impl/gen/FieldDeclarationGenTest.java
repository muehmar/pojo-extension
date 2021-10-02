package io.github.muehmar.pojoextension.generator.impl.gen;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.Type;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import io.github.muehmar.pojoextension.generator.writer.WriterFactory;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FieldDeclarationGenTest {

  @ParameterizedTest
  @MethodSource("privateAndFinalModifierUnordered")
  void generate_when_privateAndFinalModifierUnordered_then_correctOutputWithOrderedModifiers(
      PList<JavaModifier> modifiers) {
    final FieldDeclarationGen generator =
        FieldDeclarationGen.ofModifiers(modifiers.toArray(JavaModifier.class));
    final Writer writer =
        generator.generate(
            new PojoField(Type.string(), Name.fromString("id"), true),
            PojoSettings.defaultSettings(),
            WriterFactory.createDefault());

    assertTrue(writer.getRefs().exists("java.lang.String"::equals));

    assertEquals("private final String id;\n", writer.asString());
  }

  private static Stream<Arguments> privateAndFinalModifierUnordered() {
    return Stream.of(
        Arguments.of(PList.of(JavaModifier.FINAL, JavaModifier.PRIVATE)),
        Arguments.of(PList.of(JavaModifier.PRIVATE, JavaModifier.FINAL)));
  }
}