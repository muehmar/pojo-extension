package io.github.muehmar.pojoextension.generator.impl.gen;

import static io.github.muehmar.pojoextension.generator.data.Necessity.REQUIRED;
import static io.github.muehmar.pojoextension.generator.data.type.Types.integer;
import static io.github.muehmar.pojoextension.generator.data.type.Types.string;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_INTEGER;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_LANG_STRING;
import static io.github.muehmar.pojoextension.generator.impl.gen.Refs.JAVA_UTIL_MAP;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.data.Name;
import io.github.muehmar.pojoextension.generator.data.PojoField;
import io.github.muehmar.pojoextension.generator.data.settings.PojoSettings;
import io.github.muehmar.pojoextension.generator.data.type.Types;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class FieldDeclarationGenTest {

  @ParameterizedTest
  @MethodSource("privateAndFinalModifierUnordered")
  void generate_when_privateAndFinalModifierUnordered_then_correctOutputWithOrderedModifiers(
      PList<JavaModifier> modifiers) {
    final Generator<PojoField, PojoSettings> generator =
        FieldDeclarationGen.ofModifiers(modifiers.toArray(JavaModifier.class));
    final Writer writer =
        generator.generate(
            new PojoField(Name.fromString("someMap"), Types.map(string(), integer()), REQUIRED),
            PojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JAVA_LANG_STRING::equals));
    assertTrue(writer.getRefs().exists(JAVA_LANG_INTEGER::equals));
    assertTrue(writer.getRefs().exists(JAVA_UTIL_MAP::equals));

    assertEquals("private final Map<String,Integer> someMap;", writer.asString());
  }

  private static Stream<Arguments> privateAndFinalModifierUnordered() {
    return Stream.of(
        Arguments.of(PList.of(JavaModifier.FINAL, JavaModifier.PRIVATE)),
        Arguments.of(PList.of(JavaModifier.PRIVATE, JavaModifier.FINAL)));
  }
}
