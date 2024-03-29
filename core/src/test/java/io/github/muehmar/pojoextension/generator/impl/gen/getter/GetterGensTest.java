package io.github.muehmar.pojoextension.generator.impl.gen.getter;

import static org.junit.jupiter.api.Assertions.*;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojoextension.generator.PojoFields;
import io.github.muehmar.pojoextension.generator.Pojos;
import io.github.muehmar.pojoextension.generator.impl.gen.Refs;
import io.github.muehmar.pojoextension.generator.model.Getter;
import io.github.muehmar.pojoextension.generator.model.Pojo;
import io.github.muehmar.pojoextension.generator.model.PojoAndField;
import io.github.muehmar.pojoextension.generator.model.PojoField;
import io.github.muehmar.pojoextension.generator.model.settings.PojoSettings;
import org.junit.jupiter.api.Test;

class GetterGensTest {

  @Test
  void optionalGetterMethod_when_optionalFieldInSample_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> gen = GetterGens.optionalGetterMethod();

    final Writer writer =
        gen.generate(
            Pojos.sample().getPojoAndFields().apply(2),
            PojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals(
        "default String getNicknameOr(String nickname) {\n"
            + "  return getNickname().orElse(nickname);\n"
            + "}",
        writer.asString());

    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void optionalGetterMethod_when_optionalFieldGetterReturnsNullable_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> gen = GetterGens.optionalGetterMethod();
    final Pojo sample = Pojos.sample();
    final PojoField optionalField = sample.getFields().apply(2);
    final Getter getter =
        PojoFields.toGetter(optionalField).withReturnType(optionalField.getType());
    final Pojo pojo = sample.withGetters(PList.single(getter));

    final PojoAndField pojoAndField = PojoAndField.of(pojo, optionalField);

    final Writer writer =
        gen.generate(pojoAndField, PojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "default String getNicknameOr(String nickname) {\n"
            + "  return Optional.ofNullable(getNickname()).orElse(nickname);\n"
            + "}",
        writer.asString());
    assertEquals(PList.single(Refs.JAVA_UTIL_OPTIONAL), writer.getRefs());
  }

  @Test
  void optionalGetterMethod_when_requiredField_then_noOutput() {
    final Generator<PojoAndField, PojoSettings> gen = GetterGens.optionalGetterMethod();

    final Writer writer =
        gen.generate(
            Pojos.sample().getPojoAndFields().apply(0),
            PojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
    assertTrue(writer.getRefs().isEmpty());
  }

  @Test
  void optionalGetterMethod_when_genericField_then_correctOutput() {
    final Generator<PojoAndField, PojoSettings> gen = GetterGens.optionalGetterMethod();

    final Writer writer =
        gen.generate(
            Pojos.genericSample().getPojoAndFields().apply(2),
            PojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals(
        "default S getAdditionalDataOr(S additionalData) {\n"
            + "  return getAdditionalData().orElse(additionalData);\n"
            + "}",
        writer.asString());

    assertTrue(writer.getRefs().isEmpty());
  }
}
